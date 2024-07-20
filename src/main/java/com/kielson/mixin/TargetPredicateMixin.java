package com.kielson.mixin;

import com.kielson.KielsonsEntityAttributes;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TargetPredicate.class)
abstract class TargetPredicateMixin {
    @Unique private final TargetPredicate targetPredicate = (TargetPredicate)(Object) this;

    @ModifyVariable(method = "test", at = @At("STORE"), ordinal = 1)
    private double KielsonsAPI$adjustMobDetectionRange(double e, @Local(ordinal = 0) double d, @Local(ordinal = 1, argsOnly = true) LivingEntity targetEntity) {
        if (targetEntity == null) {
            return e;
        }
        EntityAttributeInstance instance = targetEntity.getAttributeInstance(KielsonsEntityAttributes.MOB_DETECTION_RANGE);
        if (instance == null) {
            return e;
        }
        return Math.max((targetPredicate.baseMaxDistance * d) - (0.1 * instance.getValue()), 2.0);
    }
}
