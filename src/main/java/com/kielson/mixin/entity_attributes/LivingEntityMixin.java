package com.kielson.mixin.entity_attributes;

import com.kielson.KielsonsAPIEntityAttributes;
import com.kielson.util.BooleanAttribute;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique private final LivingEntity livingEntity = (LivingEntity) (Object) this;

    @Inject(method = "createLivingAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;", require = 1, allow = 1, at = @At("RETURN"))
    private static void Kielson$addAttributes(final CallbackInfoReturnable<AttributeSupplier.Builder> info) {
        info.getReturnValue()
                .add(KielsonsAPIEntityAttributes.FREEZING_RESISTANCE)
                .add(KielsonsAPIEntityAttributes.PASSIVE_REGENERATION)
                .add(KielsonsAPIEntityAttributes.HEALING_MULTIPLIER)
                .add(KielsonsAPIEntityAttributes.RANGED_DAMAGE)
                .add(KielsonsAPIEntityAttributes.SWIMMING_SPEED)
                .add(KielsonsAPIEntityAttributes.PULL_TIME)

                .add(KielsonsAPIEntityAttributes.BLINDNESS_IMMUNITY)
                .add(KielsonsAPIEntityAttributes.DARKNESS_IMMUNITY)
                .add(KielsonsAPIEntityAttributes.WEAKNESS_IMMUNITY)
                .add(KielsonsAPIEntityAttributes.MINING_FATIGUE_IMMUNITY)
                .add(KielsonsAPIEntityAttributes.POISON_IMMUNITY)
                .add(KielsonsAPIEntityAttributes.WITHER_IMMUNITY)
                .add(KielsonsAPIEntityAttributes.LEVITATION_IMMUNITY)
                .add(KielsonsAPIEntityAttributes.SLOWNESS_IMMUNITY);
    }

    @ModifyArg(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"), index = 2)
    protected int Kielson$modifyExperience(int originalXP) {
        Player attackingPlayer = livingEntity.getLastHurtByPlayer();
        if (attackingPlayer == null) {
            return originalXP;
        }
        AttributeInstance attributeInstance = attackingPlayer.getAttribute(KielsonsAPIEntityAttributes.EXPERIENCE_MULTIPLIER);
        if (attributeInstance == null) {
            return originalXP;
        }
        return(int) (originalXP * attributeInstance.getValue());
    }

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("RETURN"), cancellable = true)
    private void Kielson$applyFreezingResistance(DamageSource damageSource, float damage, CallbackInfoReturnable<Float> cir) {
        if (damageSource.is(DamageTypes.FREEZE)) {
            AttributeInstance resistanceInstance = livingEntity.getAttribute(KielsonsAPIEntityAttributes.FREEZING_RESISTANCE);
            if (resistanceInstance != null) {
                float resistance = (float) resistanceInstance.getValue();
                float originalDamage = cir.getReturnValue();
                cir.setReturnValue(originalDamage * (1.0f - resistance));
            }
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void Kielson$cancelFreezeHurtAnimation(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (source.is(DamageTypes.FREEZE)) {
            AttributeInstance resistanceInstance = livingEntity.getAttribute(KielsonsAPIEntityAttributes.FREEZING_RESISTANCE);
            if (resistanceInstance != null && resistanceInstance.getValue() >= 1.0f) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void Kielson$blockStatusEffects(MobEffectInstance newEffect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity living = (LivingEntity) (Object) this;

        if (newEffect.getEffect() == MobEffects.BLINDNESS && BooleanAttribute.isTrue(living, KielsonsAPIEntityAttributes.BLINDNESS_IMMUNITY)) cir.setReturnValue(false);
        if (newEffect.getEffect() == MobEffects.DARKNESS && BooleanAttribute.isTrue(living, KielsonsAPIEntityAttributes.DARKNESS_IMMUNITY)) cir.setReturnValue(false);
        if (newEffect.getEffect() == MobEffects.WEAKNESS && BooleanAttribute.isTrue(living, KielsonsAPIEntityAttributes.WEAKNESS_IMMUNITY)) cir.setReturnValue(false);
        if (newEffect.getEffect() == MobEffects.MINING_FATIGUE && BooleanAttribute.isTrue(living, KielsonsAPIEntityAttributes.MINING_FATIGUE_IMMUNITY)) cir.setReturnValue(false);
        if (newEffect.getEffect() == MobEffects.POISON && BooleanAttribute.isTrue(living, KielsonsAPIEntityAttributes.POISON_IMMUNITY)) cir.setReturnValue(false);
        if (newEffect.getEffect() == MobEffects.WITHER && BooleanAttribute.isTrue(living, KielsonsAPIEntityAttributes.WITHER_IMMUNITY)) cir.setReturnValue(false);
        if (newEffect.getEffect() == MobEffects.LEVITATION && BooleanAttribute.isTrue(living, KielsonsAPIEntityAttributes.LEVITATION_IMMUNITY)) cir.setReturnValue(false);
        if (newEffect.getEffect() == MobEffects.SLOWNESS && BooleanAttribute.isTrue(living, KielsonsAPIEntityAttributes.SLOWNESS_IMMUNITY)) cir.setReturnValue(false);
    }
}
