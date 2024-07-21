package com.kielson.mixin;

import com.kielson.KielsonsEntityAttributes;
import com.kielson.util.CustomRangedWeapon;
import com.kielson.util.RangedConfig;
import com.kielson.util.ScalingUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public class BowItemMixin {
    private RangedConfig config() {
        return ((CustomRangedWeapon) this).getRangedWeaponConfig();
    }

    @Unique
    public float getCustomPullProgress(int useTicks) {
        float pullTime = config().pull_time() > 0 ? config().pull_time() : 20;
        float f = (float)useTicks / pullTime;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    /**
     * Apply custom pull time
     */
    @WrapOperation(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"))
    private float applyCustomPullTime(int ticks, Operation<Float> original) {
        if (config().pull_time() > 0) {
            return getCustomPullProgress(ticks);
        } else {
            return original.call(ticks);
        }
    }

    @Unique private static final float STANDARD_DAMAGE = 6;
    @Unique private static final float STANDARD_VELOCITY = 3.0F;

    /**
     * Apply custom velocity and damage-
     */
    @Inject(method = "shoot", at = @At(value = "RETURN"))
    private void applyCustomVelocityAndDamage(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, LivingEntity target, CallbackInfo ci) {
        if (projectile instanceof PersistentProjectileEntity persistentProjectile) {
            if (config().velocity() > 0F) {
                // 3.0F is the default hardcoded velocity of bows
                persistentProjectile.setVelocity(projectile.getVelocity().multiply(config().velocity() / 3.0F));
            }
            var rangedDamage = shooter.getAttributeValue(KielsonsEntityAttributes.RANGED_DAMAGE);
            if (rangedDamage > 0) {
                var multiplier = ScalingUtil.arrowDamageMultiplier(STANDARD_DAMAGE, rangedDamage, STANDARD_VELOCITY, config().velocity());
                var finalDamage = persistentProjectile.getDamage() * multiplier;
                persistentProjectile.setDamage(finalDamage);
            }
        }
    }
}
