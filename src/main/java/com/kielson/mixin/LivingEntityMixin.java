package com.kielson.mixin;

import com.kielson.KielsonsAPIComponents;
import com.kielson.KielsonsAPIEntityAttributes;
import com.kielson.events.KielsonsAPIEvents;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    @Unique private final LivingEntity livingEntity = (LivingEntity) (Object) this;
    @Unique private final Player attackingPlayer = livingEntity.getLastHurtByPlayer();

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "createLivingAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;", require = 1, allow = 1, at = @At("RETURN"))
    private static void KielsonsAPI$addAttributes(final CallbackInfoReturnable<AttributeSupplier.Builder> info) {
        info.getReturnValue()
                .add(KielsonsAPIEntityAttributes.HEALING_MULTIPLIER)
                .add(KielsonsAPIEntityAttributes.RANGED_DAMAGE)
                .add(KielsonsAPIEntityAttributes.SWIMMING_SPEED)
                .add(KielsonsAPIEntityAttributes.PULL_TIME);
    }

    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true)
    private float KielsonsAPI$heal(float amount) {
        return KielsonsAPIEvents.ON_HEAL.invoker().onHeal(livingEntity, amount);
    }

    @ModifyArg(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"), index = 2)
    protected int KielsonsAPI$modifyExperience(int originalXP) {
        if (this.attackingPlayer == null) {
            return originalXP;
        }
        AttributeInstance attributeInstance = attackingPlayer.getAttribute(KielsonsAPIEntityAttributes.EXPERIENCE);
        if (attributeInstance == null) {
            return originalXP;
        }
        return(int) (originalXP * attributeInstance.getValue());
    }

    @Inject(method = "getOffhandItem", at = @At("HEAD"), cancellable = true)
    private void Kielson$getOffHandStack(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack mainHandStack = livingEntity.getItemBySlot(EquipmentSlot.MAINHAND);
        Boolean mainHandStackComponent = mainHandStack.get(KielsonsAPIComponents.TWO_HANDED);
        if (Boolean.TRUE.equals(mainHandStackComponent)) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "getMainHandItem", at = @At("HEAD"), cancellable = true)
    private void Kielson$getMainHandStack(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack offHandStack = livingEntity.getItemBySlot(EquipmentSlot.OFFHAND);
        Boolean offHandStackComponent = offHandStack.get(KielsonsAPIComponents.TWO_HANDED);
        if (Boolean.TRUE.equals(offHandStackComponent)) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
