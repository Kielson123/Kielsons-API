package com.kielson.mixin;

import com.kielson.KielsonsEntityAttributes;
import com.kielson.util.CustomRangedWeapon;
import com.kielson.util.RangedConfig;
import com.kielson.util.ScalingUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    @Shadow @Final private static float DEFAULT_SPEED;
    private static final float STANDARD_VELOCITY = DEFAULT_SPEED;
    private static final float STANDARD_DAMAGE = 9;

    private RangedConfig config() {
        return ((CustomRangedWeapon) this).getRangedWeaponConfig();
    }

    public float getCustomPullProgress(int useTicks) {
        float pullTime = config().pull_time() > 0 ? config().pull_time() : 25;
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
    @Inject(method = "getPullTime", at = @At("HEAD"), cancellable = true)
    private static void applyCustomPullTime(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        var item = stack.getItem();
        if (item instanceof CustomRangedWeapon weapon) {
            var pullTime = weapon.getRangedWeaponConfig().pull_time();
            if (pullTime > 0) {
                cir.setReturnValue(pullTime);
                cir.cancel();
            }
        }
    }

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
