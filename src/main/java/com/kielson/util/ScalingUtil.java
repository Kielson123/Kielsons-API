package com.kielson.util;

import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;

public class ScalingUtil {
    private static final float STANDARD_BOW_VELOCITY = 3F;
    private static final float STANDARD_BOW_DAMAGE = 6.0F;
    private static final float STANDARD_CROSSBOW_VELOCITY = 3.15F;
    private static final float STANDARD_CROSSBOW_DAMAGE = 9.0F;

    public static final Scaling BOW_BASELINE = new Scaling(STANDARD_BOW_VELOCITY, STANDARD_BOW_DAMAGE);
    public static final Scaling CROSSBOW_BASELINE = new Scaling(STANDARD_CROSSBOW_VELOCITY, STANDARD_CROSSBOW_DAMAGE);

    public record Scaling(double velocity, double damage) { }

    public static Scaling scaling(ItemStack itemStack, double damage) {
        var item = itemStack.getItem();
        Scaling baseline;
        if (item instanceof BowItem) {
            baseline = BOW_BASELINE;
        } else if (item instanceof CrossbowItem) {
            baseline = CROSSBOW_BASELINE;
        } else {
            return new Scaling(1, 1);
        }

        double velocityMultiplier = 1;
        if (item instanceof CustomRangedWeapon rangedWeapon) {
            var customVelocity = rangedWeapon.getRangedWeaponConfig().velocity();
            if (customVelocity > 0) {
                velocityMultiplier = arrowVelocityMultiplier(baseline.velocity, customVelocity);
            }
        }
        var damageMultiplier = arrowDamageMultiplier(baseline.damage, damage, baseline.velocity, 0);
        return new Scaling(velocityMultiplier, damageMultiplier);
    }

    /**
     * Boosts arrow velocity
     * @param standardVelocity default arrow velocity
     * @param customVelocity non-default arrow velocity
     * @return value of customVelocity divided by standardVelocity
     */
    public static double arrowVelocityMultiplier(double standardVelocity, double customVelocity) {
        return customVelocity / standardVelocity;
    }

    /**
     * Boosts damage based on the RANGED_DAMAGE attribute
     * @param standardDamage default arrow damage
     * @param attributeDamage value of RANGED_DAMAGE attribute
     * @param standardVelocity default arrow velocity
     * @param customVelocity non-default arrow velocity
     * @return value of RANGED_DAMAGE divided by default arrow damage with counteraction of custom velocity damage boost
     */
    public static double arrowDamageMultiplier(double standardDamage, double attributeDamage, double standardVelocity, double customVelocity) {
        // Boost damage based on the attribute
        var multiplier = (attributeDamage / standardDamage);
        if (customVelocity > 0) {
            // Counteract the damage boost by caused by non-standard velocity
            multiplier *= (standardVelocity / customVelocity);
        }
        return multiplier;
    }
}
