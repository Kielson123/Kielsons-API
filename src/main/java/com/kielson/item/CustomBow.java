package com.kielson.item;

import com.kielson.KielsonsEntityAttributes;
import com.kielson.util.RangedWeaponHelper;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

import static com.kielson.KielsonsAPI.MOD_ID;

public class CustomBow extends BowItem {
    private static double rangedDamage;
    private static double pullTime;
    private static double projectileVelocity;

    public final static HashSet<CustomBow> instances = new HashSet<>();

    public CustomBow(double rangedDamage, double pullTime, double projectileVelocity, Settings settings) {
        super(settings.attributeModifiers(createAttributeModifiers()));
        instances.add(this);
        CustomBow.rangedDamage = rangedDamage;
        CustomBow.pullTime = pullTime;
        CustomBow.projectileVelocity = projectileVelocity;
    }

    private static AttributeModifiersComponent createAttributeModifiers() {
        return AttributeModifiersComponent.builder()
                .add(KielsonsEntityAttributes.RANGED_DAMAGE, new EntityAttributeModifier(Identifier.of(MOD_ID, "bow"), rangedDamage, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
                .add(KielsonsEntityAttributes.PULL_TIME, new EntityAttributeModifier(Identifier.of(MOD_ID, "bow"), pullTime, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
                .build();
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return;
        }
        ItemStack itemStack = playerEntity.getProjectileType(stack);
        if (itemStack.isEmpty()) {
            return;
        }
        int useTicks = getMaxUseTime(stack, user) - remainingUseTicks;
        float f = getPullProgress(useTicks, user, stack);
        if ((double)f < 0.1) {
            return;
        }
        List<ItemStack> list = load(stack, itemStack, playerEntity);
        if (world instanceof ServerWorld serverWorld) {
            if (!list.isEmpty()) {
                float speed = (float) (getPullProgress(useTicks, user, itemStack) * projectileVelocity);
                shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, speed, 1.0f, f == 1.0f, null);
            }
        }
        world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + f * 0.5f);
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        projectile.setVelocity(shooter, shooter.getPitch(), shooter.getYaw() + yaw, 0.0f, speed, divergence);
        if (projectile instanceof PersistentProjectileEntity persistentProjectile) {
            double damage = shooter.getAttributeValue(KielsonsEntityAttributes.RANGED_DAMAGE) / projectileVelocity;
            ItemStack handStack = shooter.getStackInHand(shooter.getActiveHand());
            if (handStack.getItem() instanceof BowItem && RangedWeaponHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).isPresent()){
                damage += (int) ((damage * 0.25) * (RangedWeaponHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).get() + 1));
            }
            persistentProjectile.setDamage(damage);
        }
    }

    public static float getPullProgress(int useTicks, LivingEntity user, ItemStack itemStack) {
        float pullTime = (float) user.getAttributeValue(KielsonsEntityAttributes.PULL_TIME);
        if (itemStack.getItem() instanceof BowItem && RangedWeaponHelper.checkEnchantmentLevel(itemStack, Enchantments.QUICK_CHARGE).isPresent()){
            pullTime -= 0.25f * RangedWeaponHelper.checkEnchantmentLevel(itemStack, Enchantments.QUICK_CHARGE).get();
        }
        pullTime *= 20.0f;
        float f = (float)useTicks / pullTime;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }
}
