package com.kielson.util;

import com.kielson.KielsonsAPIEntityAttributes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class RangedWeaponHelper {
    public static float getBowPower(int timeHeld, LivingEntity entity) {
        double pullTime = entity.getAttributeValue(KielsonsAPIEntityAttributes.PULL_TIME);
        float charge = (float) timeHeld / (20.0F * (float) pullTime);
        charge = (charge * charge + charge * 2.0F) / 3.0F;
        return Math.min(charge, 1.0F);
    }

    public static float getCrossbowCharge(ItemStack stack, LivingEntity user, float basePullTime) {
        float modified = EnchantmentHelper.modifyCrossbowChargingTime(stack, user, (float) user.getAttributeValue(KielsonsAPIEntityAttributes.PULL_TIME));
        return Mth.floor(modified * 20.0F);
    }

    public static void applyArrowDamage(LivingEntity shooter, AbstractArrow arrow, double speed) {
        double damage = shooter.getAttributeValue(KielsonsAPIEntityAttributes.RANGED_DAMAGE);
        float safeSpeed = Math.max((float) speed, 0.1F);
        arrow.setBaseDamage(damage / safeSpeed);
        arrow.setCritArrow(false);
    }
}
