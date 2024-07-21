package com.kielson.util;


/**
 * Represents the configurable properties of ranged weapons.
 * @param pull_time - the number of ticks it takes to fully pull back the weapon
 * @param damage - the amount of damage the weapon deals
 * @param velocity - customized velocity of the projectile, only applied if greater than 0
 *                 Does not affect the projectile damage!
 */
public record RangedConfig(int pull_time, float damage, float velocity) {
    public static final RangedConfig EMPTY = new RangedConfig(0, 0, 0);
    public static final RangedConfig BOW = new RangedConfig(20, (float) ScalingUtil.BOW_BASELINE.damage(), 0);
    public static final RangedConfig CROSSBOW = new RangedConfig(25, (float) ScalingUtil.CROSSBOW_BASELINE.damage(), 0);
}
