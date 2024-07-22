package com.kielson.mixin;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Random;

@Mixin(PersistentProjectileEntity.class)
abstract class PersistentProjectileEntityMixin {
    @Unique private final PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity)(Object) this;
    @Unique private static final Random CRIT_RANDOM = new Random();
    @Shadow private double damage;
    @Shadow public abstract boolean isCritical();

    /*@Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSources;arrow(Lnet/minecraft/entity/projectile/PersistentProjectileEntity;Lnet/minecraft/entity/Entity;)Lnet/minecraft/entity/damage/DamageSource;", shift = At.Shift.AFTER))
    private void KielsonsAPI$applyRangedDamageAttribute(EntityHitResult entityHitResult, CallbackInfo ci, @Local(ordinal = 0) LocalDoubleRef damage, @Local(ordinal = 1) Entity entity){
        if(damage.get() <= 0) return;
        if(entity instanceof LivingEntity livingEntity && livingEntity.getAttributeInstance(KielsonsEntityAttributes.RANGED_DAMAGE) != null) {
            damage.set(damage.get() * livingEntity.getAttributeValue(KielsonsEntityAttributes.RANGED_DAMAGE));
        }
    }*/

    @ModifyVariable(method = "onEntityHit", at = @At("STORE"), ordinal = 0)
    private int KielsonsAPI$modifyCritDamage(int value) {
        if (!isCritical()) return value;
        double velocity = persistentProjectileEntity.getVelocity().length();
        float critMultiplier = 1F + (0.05F + CRIT_RANDOM.nextFloat() * 0.45F);
        return (int) Math.round(MathHelper.clamp(velocity * this.damage * critMultiplier, 0.0, 2.147483647E9));
    }
}
