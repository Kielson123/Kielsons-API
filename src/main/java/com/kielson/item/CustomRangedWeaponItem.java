package com.kielson.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.function.Predicate;

public class CustomRangedWeaponItem extends RangedWeaponItem {
    public final static HashSet<CustomRangedWeaponItem> instances = new HashSet<>();
    private final WeaponType weaponType;
    private final int pullTime;
    private final float projectileDamage;
    private final float projectileVelocity;

    public CustomRangedWeaponItem(WeaponType weaponType, int pullTime, float projectileDamage, float projectileVelocity, Settings settings) {
        super(settings);
        this.pullTime = pullTime;
        this.projectileDamage = projectileDamage;
        this.projectileVelocity = projectileVelocity;
        this.weaponType = weaponType;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return null;
    }

    @Override
    public int getRange() {
        return 0;
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {

    }

    public int getPullTime() {
        return pullTime;
    }

    public float getProjectileDamage() {
        return projectileDamage;
    }

    public float getProjectileVelocity() {
        return projectileVelocity;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public enum WeaponType { BOW, CROSSBOW }
}
