package com.kielson.item;

import com.kielson.KielsonsAPIEntityAttributes;

import java.util.List;
import java.util.function.Predicate;

import com.kielson.util.RangedWeaponHelper;
import com.kielson.util.RangedWeaponStats;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import static com.kielson.KielsonsAPI.MOD_ID;

public class CustomBow extends BowItem {
    public static boolean IGNORE_MIXIN = false;
    private final RangedWeaponStats stats;

    public CustomBow(RangedWeaponStats stats, Properties settings) {
        super(applyCustomStats(settings, stats));
        this.stats = stats;
    }

    private static Properties applyCustomStats(Properties settings, RangedWeaponStats stats) {
        IGNORE_MIXIN = true;
        return settings.attributes(ItemAttributeModifiers.builder()
                .add(KielsonsAPIEntityAttributes.RANGED_DAMAGE,
                        new AttributeModifier(Identifier.fromNamespaceAndPath(MOD_ID, "custom_bow"), stats.damage(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HAND)
                .add(KielsonsAPIEntityAttributes.PULL_TIME,
                        new AttributeModifier(Identifier.fromNamespaceAndPath(MOD_ID, "custom_bow"), stats.pullTime(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HAND)
                .build());
    }

    @Override
    public boolean releaseUsing(@NonNull ItemStack stack, @NonNull Level level, @NonNull LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return false;
        ItemStack ammo = player.getProjectile(stack);
        if (ammo.isEmpty()) return false;

        int timeHeld = this.getUseDuration(stack, entity) - timeLeft;
        float power = RangedWeaponHelper.getBowPower(timeHeld, entity);
        if (power < 0.1F) return false;

        List<ItemStack> shots = draw(stack, ammo, player);

        if (!shots.isEmpty()) {
            if (level instanceof ServerLevel serverLevel) {
                this.shoot(serverLevel, player, player.getUsedItemHand(), stack, shots, power * (float) stats.velocity(), 1.0F, power == 1.0F, null);
            }
            level.playSound(
                    player,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ARROW_SHOOT,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F
            );
            player.awardStat(Stats.ITEM_USED.get(this));
        }

        return false;
    }

    @Override
    protected void shootProjectile(@NonNull LivingEntity shooter, @NonNull Projectile projectile, int index, float power, float uncertainty, float angle, @Nullable LivingEntity targetOverride) {
        super.shootProjectile(shooter, projectile, index, power, uncertainty, angle, targetOverride);

        if (projectile instanceof AbstractArrow arrow) {
            RangedWeaponHelper.applyArrowDamage(shooter, arrow, power);
        }
    }

    @Override
    public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public @NonNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }
}
