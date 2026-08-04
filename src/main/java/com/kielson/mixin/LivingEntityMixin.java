package com.kielson.mixin;

import com.kielson.KielsonsAPIComponents;
import com.kielson.KielsonsAPIEntityAttributes;
import com.kielson.events.KielsonsAPIEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.kielson.KielsonsAPI.isBetterCombatLoaded;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    @Unique private final LivingEntity livingEntity = (LivingEntity) (Object) this;
    @Unique private final Player attackingPlayer = livingEntity.getLastHurtByPlayer();
    @Unique private int ticks;

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "createLivingAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;", require = 1, allow = 1, at = @At("RETURN"))
    private static void KielsonsAPI$addAttributes(final CallbackInfoReturnable<AttributeSupplier.Builder> info) {
        info.getReturnValue()
                .add(KielsonsAPIEntityAttributes.PASSIVE_REGENERATION)
                .add(KielsonsAPIEntityAttributes.HEALING_MULTIPLIER)
                .add(KielsonsAPIEntityAttributes.RANGED_DAMAGE)
                .add(KielsonsAPIEntityAttributes.SWIMMING_SPEED)
                .add(KielsonsAPIEntityAttributes.PULL_TIME);
    }

    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true)
    private float KielsonsAPI$heal(float heal) {
        return KielsonsAPIEvents.ON_HEAL.invoker().onHeal(livingEntity, heal);
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
        if (Boolean.TRUE.equals(mainHandStackComponent) && !isBetterCombatLoaded()) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "getMainHandItem", at = @At("HEAD"), cancellable = true)
    private void Kielson$getMainHandStack(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack offHandStack = livingEntity.getItemBySlot(EquipmentSlot.OFFHAND);
        Boolean offHandStackComponent = offHandStack.get(KielsonsAPIComponents.TWO_HANDED);
        if (Boolean.TRUE.equals(offHandStackComponent) && !isBetterCombatLoaded()) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void Kielson$tick(CallbackInfo info) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;

        if(this.ticks < 20) {
            this.ticks++;
        } else {
            KielsonsAPIEvents.EVERY_SECOND.invoker().everySecond(livingEntity);
            this.ticks = 0;
        }
    }
}
