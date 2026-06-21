package com.kielson.item;

import com.kielson.KielsonsAPIEntityAttributes;
import com.kielson.util.BowInterface;
import com.kielson.util.ItemHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.UseAnim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import static com.kielson.KielsonsAPI.MOD_ID;

public class CustomBow extends ProjectileWeaponItem implements BowInterface {
    private final double projectileVelocity;
    private final double pullTime;

    public final static HashSet<CustomBow> instances = new HashSet<>();

    public CustomBow(double rangedDamage, double pullTime, double projectileVelocity, Properties settings) {
        super(settings.attributes(ItemAttributeModifiers.builder()
                .add(KielsonsAPIEntityAttributes.RANGED_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "custom_bow"), rangedDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
                .add(KielsonsAPIEntityAttributes.PULL_TIME, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "custom_bow"), pullTime, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
                .build()));

        instances.add(this);
        this.projectileVelocity = projectileVelocity;
        this.pullTime = pullTime;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof Player playerEntity)) {
            return;
        }

        ItemStack itemStack = playerEntity.getProjectile(stack);
        if (itemStack.isEmpty()) {
            return;
        }

        int useTicks = this.getUseDuration(stack, user) - remainingUseTicks;
        float f = getPullProgress(useTicks, user, itemStack);
        if (f < 0.1F) {
            return;
        }

        List<ItemStack> list = draw(stack, itemStack, playerEntity);
        if (world instanceof ServerLevel serverWorld && !list.isEmpty()) {
            float speed = (float) (getPullProgress(useTicks, user, itemStack) * projectileVelocity);
            shoot(serverWorld, playerEntity, playerEntity.getUsedItemHand(), stack, list, speed, 1.0F, f == 1.0F, null);
        }

        world.playSound(
                null,
                playerEntity.getX(),
                playerEntity.getY(),
                playerEntity.getZ(),
                SoundEvents.ARROW_SHOOT,
                SoundSource.PLAYERS,
                1.0F,
                1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F
        );
        playerEntity.awardStat(Stats.ITEM_USED.get(this));
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot() + yaw, 0.0f, speed, divergence);
        if (projectile instanceof AbstractArrow persistentProjectile) {
            double damage = shooter.getAttributeValue(KielsonsAPIEntityAttributes.RANGED_DAMAGE) / projectileVelocity;
            ItemStack handStack = shooter.getItemInHand(shooter.getUsedItemHand());
            if (handStack.getItem() instanceof BowInterface && ItemHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).isPresent()){
                damage += (int) ((damage * 0.25) * (ItemHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).get() + 1));
            }
            persistentProjectile.setBaseDamage(damage);
        }
    }

    public static float getPullProgress(int useTicks, LivingEntity user, ItemStack itemStack) {
        float pullTime = (float) user.getAttributeValue(KielsonsAPIEntityAttributes.PULL_TIME);
        if (itemStack.getItem() instanceof BowInterface && ItemHelper.checkEnchantmentLevel(itemStack, Enchantments.QUICK_CHARGE).isPresent()){
            pullTime -= 0.25f * ItemHelper.checkEnchantmentLevel(itemStack, Enchantments.QUICK_CHARGE).get();
        }
        pullTime *= 20.0f;
        float f = (float)useTicks / pullTime;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return (int) (72000 * pullTime);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        boolean bl = !user.getProjectile(itemStack).isEmpty();

        if (!user.hasInfiniteMaterials() && !bl) {
            return InteractionResultHolder.fail(itemStack);
        } else {
            user.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }
    }
}
