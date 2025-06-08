package com.kielson.mixin;

import com.kielson.KielsonsAPIComponents;
import com.kielson.KielsonsAPIEntityAttributes;
import com.kielson.events.KielsonsAPIEvents;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;
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
    @Unique private final PlayerEntity attackingPlayer = livingEntity.getAttackingPlayer();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "createLivingAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;", require = 1, allow = 1, at = @At("RETURN"))
    private static void KielsonsAPI$addAttributes(final CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        info.getReturnValue()
                .add(KielsonsAPIEntityAttributes.HEALING_MULTIPLIER)
                .add(KielsonsAPIEntityAttributes.MOB_DETECTION_RANGE)
                .add(KielsonsAPIEntityAttributes.RANGED_DAMAGE)
                .add(KielsonsAPIEntityAttributes.SWIMMING_SPEED)
                .add(KielsonsAPIEntityAttributes.PULL_TIME);
    }

    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true)
    private float KielsonsAPI$heal(float amount) {
        return KielsonsAPIEvents.ON_HEAL.invoker().onHeal(livingEntity, amount);
    }

    /**
     * @author DaFuqs
     */
    @ModifyArg(method = "travelInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
    public float KielsonsAPI$waterSpeed(float original) {
        EntityAttributeInstance waterSpeed = livingEntity.getAttributeInstance(KielsonsAPIEntityAttributes.SWIMMING_SPEED);
        if (waterSpeed == null) {
            return original;
        } else {
            if (waterSpeed.getBaseValue() != original) {
                waterSpeed.setBaseValue(original);
            }
            return (float) waterSpeed.getValue();
        }
    }

    /**
     * @author DaFuqs
     */
    @ModifyExpressionValue(method = "swimUpward", at = @At(value = "CONSTANT", args = "doubleValue=0.03999999910593033D"))
    public double KielsonsAPI$modifyUpwardSwimming(double original, TagKey<Fluid> fluid) {
        if (fluid == FluidTags.WATER) {
            EntityAttributeInstance waterSpeed = livingEntity.getAttributeInstance(KielsonsAPIEntityAttributes.SWIMMING_SPEED);
            if (waterSpeed == null) {
                return original;
            } else {
                if (waterSpeed.getBaseValue() != original) {
                    waterSpeed.setBaseValue(original);
                }
                return waterSpeed.getValue();
            }
        } else {
            return original;
        }
    }

    @ModifyArg(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V"), index = 2)
    protected int KielsonsAPI$modifyExperience(int originalXP) {
        if (this.attackingPlayer == null) {
            return originalXP;
        }
        EntityAttributeInstance attributeInstance = attackingPlayer.getAttributeInstance(KielsonsAPIEntityAttributes.EXPERIENCE);
        if (attributeInstance == null) {
            return originalXP;
        }
        return(int) (originalXP * attributeInstance.getValue());
    }

    /**
     * @author DaFuqs
     */
    @Environment(EnvType.CLIENT)
    @ModifyExpressionValue(method = "knockDownwards", at = @At(value = "CONSTANT", args = "doubleValue=-0.03999999910593033D"))
    public double KielsonsAPI$knockDownwards(double original) {
        EntityAttributeInstance waterSpeed = livingEntity.getAttributeInstance(KielsonsAPIEntityAttributes.SWIMMING_SPEED);
        if (waterSpeed == null) {
            return original;
        } else {
            if (waterSpeed.getBaseValue() != -original) {
                waterSpeed.setBaseValue(-original);
            }
            return -waterSpeed.getValue();
        }
    }



    @Inject(method = "getOffHandStack", at = @At("HEAD"), cancellable = true)
    private void Kielson$getOffHandStack(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack mainHandStack = livingEntity.getEquippedStack(EquipmentSlot.MAINHAND);
        Boolean mainHandStackComponent = mainHandStack.get(KielsonsAPIComponents.TWO_HANDED);
        if (Boolean.TRUE.equals(mainHandStackComponent)) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "getMainHandStack", at = @At("HEAD"), cancellable = true)
    private void Kielson$getMainHandStack(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack offHandStack = livingEntity.getEquippedStack(EquipmentSlot.OFFHAND);
        Boolean offHandStackComponent = offHandStack.get(KielsonsAPIComponents.TWO_HANDED);
        if (Boolean.TRUE.equals(offHandStackComponent)) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
