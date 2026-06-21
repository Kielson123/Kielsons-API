package com.kielson.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;

@Mixin(AbstractArrow.class)
abstract class PersistentProjectileEntityMixin {
    @Unique private final AbstractArrow persistentProjectileEntity = (AbstractArrow)(Object) this;
    @Unique private static final Random CRIT_RANDOM = new Random();
    @Shadow private double baseDamage;
    @Shadow public abstract boolean isCritArrow();

    @ModifyVariable(method = "onHitEntity", at = @At("STORE"), ordinal = 0)
    private int KielsonsAPI$modifyCritDamage(int value) {
        if (!isCritArrow()) return value;
        double velocity = persistentProjectileEntity.getDeltaMovement().length();
        float critMultiplier = 1f + (0.05f + CRIT_RANDOM.nextFloat() * 0.45F);
        return (int) Math.round(Mth.clamp(velocity * this.baseDamage * critMultiplier, 0.0, 2.147483647E9));
    }
}
