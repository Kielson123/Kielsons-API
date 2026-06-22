package com.kielson.item;


import com.kielson.KielsonsAPIEntityAttributes;
import com.kielson.util.RangedWeaponHelper;
import com.kielson.util.RangedWeaponStats;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import static com.kielson.KielsonsAPI.MOD_ID;

public class CustomCrossbow extends CrossbowItem {
    public static boolean IGNORE_MIXIN = false;
    private final RangedWeaponStats stats;

    public CustomCrossbow(RangedWeaponStats stats, Properties settings) {
        super(applyCustomStats(settings, stats));
        this.stats = stats;
    }

    private static Properties applyCustomStats(Properties settings, RangedWeaponStats stats) {
        IGNORE_MIXIN = true;
        return settings.attributes(ItemAttributeModifiers.builder()
                .add(KielsonsAPIEntityAttributes.RANGED_DAMAGE,
                        new AttributeModifier(Identifier.fromNamespaceAndPath(MOD_ID, "custom_crossbow"), stats.damage(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HAND)
                .add(KielsonsAPIEntityAttributes.PULL_TIME,
                        new AttributeModifier(Identifier.fromNamespaceAndPath(MOD_ID, "custom_crossbow"), stats.pullTime(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HAND)
                .build());
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, Player player, @NonNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ChargedProjectiles charged = stack.get(DataComponents.CHARGED_PROJECTILES);

        if (charged != null && !charged.isEmpty()) {
            this.performShooting(level, player, hand, stack, getShootingPower(charged), 1.0F, null);
            return InteractionResult.CONSUME;
        }
        if (!player.getProjectile(stack).isEmpty()) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onUseTick(Level level, @NonNull LivingEntity livingEntity, @NonNull ItemStack itemStack, int remainingUseTicks) {
        if (!level.isClientSide()) {
            int maxUseTime = itemStack.getUseDuration(livingEntity);
            int currentUseTicks = maxUseTime - remainingUseTicks;

            int customPullTime = (int) RangedWeaponHelper.getCrossbowCharge(itemStack, livingEntity, (float) this.stats.pullTime());

            if (currentUseTicks >= customPullTime && !CrossbowItem.isCharged(itemStack)) {
                CrossbowItem.tryLoadProjectiles(livingEntity, itemStack);

                SoundSource soundSource = livingEntity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
                level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                        SoundEvents.CROSSBOW_LOADING_END, soundSource, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.20F);
            }
        }
    }

    @Override
    public boolean releaseUsing(@NonNull ItemStack stack, @NonNull Level level, @NonNull LivingEntity entity, int timeLeft) {
        int timeHeld = this.getUseDuration(stack, entity) - timeLeft;
        float charge = (float) timeHeld / (float) getChargeDuration(stack, entity);
        return charge >= 1.0F && CrossbowItem.isCharged(stack);
    }

    @Override
    protected void shootProjectile(@NonNull LivingEntity shooter, @NonNull Projectile projectile, int index, float power, float uncertainty, float angle, @Nullable LivingEntity targetOverride) {
        super.shootProjectile(shooter, projectile, index, power, uncertainty, angle, targetOverride);

        if (projectile instanceof AbstractArrow arrow) {
            RangedWeaponHelper.applyArrowDamage(shooter, arrow, power);
        }
    }

    @Override
    protected @NonNull Projectile createProjectile(@NonNull Level level, @NonNull LivingEntity shooter, @NonNull ItemStack weapon, @NonNull ItemStack ammo, boolean crit) {
        Projectile proj = super.createProjectile(level, shooter, weapon, ammo, crit);

        if (proj instanceof AbstractArrow arrow) {
            arrow.setBaseDamage(shooter.getAttributeValue(KielsonsAPIEntityAttributes.RANGED_DAMAGE) / stats.velocity());
        }
        return proj;
    }

    @Override
    public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
        return ItemUseAnimation.CROSSBOW;
    }
}