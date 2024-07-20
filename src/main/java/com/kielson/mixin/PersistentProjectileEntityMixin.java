package com.kielson.mixin;

import com.kielson.KielsonsEntityAttributes;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(PersistentProjectileEntity.class)
abstract class PersistentProjectileEntityMixin {

    @Unique private static final Random CRIT_RANDOM = new Random();

    @Shadow
    private double damage;

    @Shadow
    public abstract boolean isCritical();

    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSources;arrow(Lnet/minecraft/entity/projectile/PersistentProjectileEntity;Lnet/minecraft/entity/Entity;)Lnet/minecraft/entity/damage/DamageSource;", shift = At.Shift.AFTER))
    private void KielsonsAPI$applyRangedDamageAttribute(EntityHitResult entityHitResult, CallbackInfo ci, @Local(ordinal = 0) LocalDoubleRef damage, @Local(ordinal = 1) Entity entity){
        if(damage.get() <= 0) {
            return;
        }
        if(entity instanceof LivingEntity livingEntity && livingEntity.getAttributeInstance(KielsonsEntityAttributes.RANGED_DAMAGE) != null) {
            damage.set(damage.get() * livingEntity.getAttributeValue(KielsonsEntityAttributes.RANGED_DAMAGE));
        }
    }

    @ModifyVariable(method = "onEntityHit", at = @At("STORE"), ordinal = 0)
    private int KielsonsAPI$modifyCritDamage(int value) {
        if (!isCritical()) { return value; }
        var projectile = (PersistentProjectileEntity) ((Object) this);
        var velocity = projectile.getVelocity().length();
        var critMultiplier = 1F + (0.05F + CRIT_RANDOM.nextFloat() * 0.45F);
        return (int) Math.round(MathHelper.clamp(velocity * this.damage * critMultiplier, 0.0, 2.147483647E9));
    }
}
