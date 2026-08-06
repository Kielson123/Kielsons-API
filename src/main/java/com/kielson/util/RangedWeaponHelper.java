package com.kielson.util;

import com.kielson.KielsonsAPIAttributes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class RangedWeaponHelper {
    public static float getBowPower(int timeHeld, LivingEntity entity, double basePullTime) {
        double pullTime = basePullTime;
        if(entity.getAttribute(KielsonsAPIAttributes.PULL_TIME) != null) {
            pullTime = entity.getAttributeValue(KielsonsAPIAttributes.PULL_TIME);
        }
        float charge = (float) timeHeld / (20.0F * (float) pullTime);
        charge = (charge * charge + charge * 2.0F) / 3.0F;
        return Math.min(charge, 1.0F);
    }

    public static float getCrossbowCharge(ItemStack stack, LivingEntity user, float basePullTime) {
        if(user.getAttribute(KielsonsAPIAttributes.PULL_TIME) == null) return basePullTime;
        float modified = EnchantmentHelper.modifyCrossbowChargingTime(stack, user, (float) user.getAttributeValue(KielsonsAPIAttributes.PULL_TIME));
        return Mth.floor(modified * 20.0F);
    }

    public static void applyArrowDamage(LivingEntity shooter, AbstractArrow arrow, double speed, double baseDamage) {
        double damage = baseDamage;
        if(shooter.getAttribute(KielsonsAPIAttributes.PULL_TIME) != null){
            damage = shooter.getAttributeValue(KielsonsAPIAttributes.RANGED_DAMAGE);
        }
        float safeSpeed = Math.max((float) speed, 0.1F);
        arrow.setBaseDamage(damage / safeSpeed);
        arrow.setCritArrow(false);
    }
}
