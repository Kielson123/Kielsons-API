package com.kielson.mixin;

import com.kielson.KielsonsEntityAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ProjectileEntity.class)
abstract class ProjectileEntityMixin{

    @ModifyArgs(method = "setVelocity(DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;calculateVelocity(DDDFF)Lnet/minecraft/util/math/Vec3d;"))
    private void KielsonsAPI$applyRangedAccuracyAttribute(Args args) {
        final int ARG = 4;
        Entity owner = ((ProjectileEntity) (Object) this).getOwner();
        float uncertainty = args.get(ARG);
        if (owner instanceof LivingEntity livingEntity && livingEntity.getAttributeInstance(KielsonsEntityAttributes.RANGED_ACCURACY) != null) {
            args.set(ARG, Math.max(0.0f, (float) (uncertainty - (0.1f * livingEntity.getAttributeValue(KielsonsEntityAttributes.RANGED_ACCURACY)))));
        }
    }
}
