package com.kielson.item;

import com.kielson.KielsonsAPIEntityAttributes;
import com.kielson.util.CrossbowInterface;
import com.kielson.util.ItemHelper;
import com.mojang.serialization.Codec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import net.minecraft.world.InteractionResultHolder;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.kielson.KielsonsAPI.MOD_ID;

public class CustomCrossbow extends ProjectileWeaponItem implements CrossbowInterface {
    private boolean charged = false;
    private boolean loaded = false;
    private static final CrossbowItem.ChargingSounds DEFAULT_LOADING_SOUNDS = new CrossbowItem.ChargingSounds(
            Optional.of(SoundEvents.CROSSBOW_LOADING_START),
            Optional.of(SoundEvents.CROSSBOW_LOADING_MIDDLE),
            Optional.of(SoundEvents.CROSSBOW_LOADING_END)
    );
    private final double projectileVelocity;
    public final static HashSet<CustomCrossbow> instances = new HashSet<>();

    public CustomCrossbow(double rangedDamage, double pullTime, double projectileVelocity, Properties settings) {
        super(settings.attributes(ItemAttributeModifiers.builder()
                .add(KielsonsAPIEntityAttributes.RANGED_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "custom_crossbow"), rangedDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
                .add(KielsonsAPIEntityAttributes.PULL_TIME, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "custom_crossbow"), pullTime, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
                .build()));

        instances.add(this);
        this.projectileVelocity = projectileVelocity;
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
    public @NotNull Predicate<ItemStack> getSupportedHeldProjectiles() {
        return ARROW_OR_FIREWORK;
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        ChargedProjectiles chargedProjectilesComponent = itemStack.get(DataComponents.CHARGED_PROJECTILES);

        if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
            this.shootAll(world, user, hand, itemStack, getSpeed(chargedProjectilesComponent), 1.0F, null);
            return InteractionResultHolder.consume(itemStack);
        } else if (!user.getProjectile(itemStack).isEmpty()) {
            this.charged = false;
            this.loaded = false;
            user.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        } else {
            return InteractionResultHolder.fail(itemStack);
        }
    }

    private float getSpeed(ChargedProjectiles stack) {
        return stack.contains(Items.FIREWORK_ROCKET) ? (float) (projectileVelocity / 1.96875) : (float) projectileVelocity;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        int useTicks = this.getUseDuration(stack, user) - remainingUseTicks;
        if (getPullProgress(useTicks, stack, user) >= 1.0F && isCharged(stack)) {
            super.releaseUsing(stack, world, user, remainingUseTicks);
        }
    }

    private static boolean loadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        List<ItemStack> list = draw(crossbow, shooter.getProjectile(crossbow), shooter);
        if (!list.isEmpty()) {
            crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(list));
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCharged(ItemStack stack) {
        ChargedProjectiles chargedProjectilesComponent = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        return !chargedProjectilesComponent.isEmpty();
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        Vector3f vector3f;
        if (target != null) {
            double d = target.getX() - shooter.getX();
            double e = target.getZ() - shooter.getZ();
            double f = Math.sqrt(d * d + e * e);
            double g = target.getY(0.3333333333333333) - projectile.getY() + f * 0.2F;
            vector3f = CrossbowItem.getProjectileShotVector(shooter, new Vec3(d, g, e), yaw);
        } else {
            Vec3 vec3d = shooter.getUpVector(1.0F);
            Quaternionf quaternionf = new Quaternionf().setAngleAxis((double)(yaw * (float) (Math.PI / 180.0)), vec3d.x, vec3d.y, vec3d.z);
            Vec3 vec3d2 = shooter.getViewVector(1.0f);
            vector3f = vec3d2.toVector3f().rotate(quaternionf);
        }
        projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), speed, divergence);
        if (projectile instanceof AbstractArrow persistentProjectile) {
            double damage = shooter.getAttributeValue(KielsonsAPIEntityAttributes.RANGED_DAMAGE) / projectileVelocity;
            ItemStack handStack = shooter.getItemInHand(shooter.getUsedItemHand());
            if (handStack.getItem() instanceof CrossbowInterface && ItemHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).isPresent()){
                damage += (int) ((damage * 0.25) * (ItemHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).get() + 1));
            }
            persistentProjectile.setBaseDamage(damage);
        }
        shooter.playSound(SoundEvents.CROSSBOW_SHOOT);
    }

    @Override
    protected @NotNull Projectile createProjectile(Level world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        if (projectileStack.is(Items.FIREWORK_ROCKET)) {
            return new FireworkRocketEntity(world, projectileStack, shooter, shooter.getX(), shooter.getEyeY() - 0.15F, shooter.getZ(), true);
        } else {
            Projectile projectileEntity = super.createProjectile(world, shooter, weaponStack, projectileStack, critical);
            if (projectileEntity instanceof AbstractArrow persistentProjectileEntity) {
                persistentProjectileEntity.setSoundEvent(SoundEvents.CROSSBOW_HIT);
            }
            return projectileEntity;
        }
    }

    @Override
    protected int getDurabilityUse(ItemStack projectile) {
        return projectile.is(Items.FIREWORK_ROCKET) ? 3 : 1;
    }

    public void shootAll(Level world, LivingEntity shooter, InteractionHand hand, ItemStack stack, float speed, float divergence, @Nullable LivingEntity target) {
        if (world instanceof ServerLevel serverWorld) {
            ChargedProjectiles chargedProjectilesComponent = stack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
            if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
                this.shoot(serverWorld, shooter, hand, stack, chargedProjectilesComponent.getItems(), speed, divergence, shooter instanceof Player, target);
                if (shooter instanceof ServerPlayer serverPlayerEntity) {
                    CriteriaTriggers.SHOT_CROSSBOW.trigger(serverPlayerEntity, stack);
                    serverPlayerEntity.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }
            }
        }
    }

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClientSide()) {
            CrossbowItem.ChargingSounds loadingSounds = getLoadingSounds(stack);
            float f = (float)(stack.getUseDuration(user) - remainingUseTicks) / getPullTime(stack, user);
            if (f < 0.2F) {
                this.charged = false;
                this.loaded = false;
            }

            if (f >= 0.2F && !this.charged) {
                this.charged = true;
                loadingSounds.start()
                        .ifPresent(sound -> world.playSound(null, user.getX(), user.getY(), user.getZ(), (SoundEvent)sound.value(), SoundSource.PLAYERS, 0.5F, 1.0F));
            }

            if (f >= 0.5F && !this.loaded) {
                this.loaded = true;
                loadingSounds.mid()
                        .ifPresent(sound -> world.playSound(null, user.getX(), user.getY(), user.getZ(), (SoundEvent)sound.value(), SoundSource.PLAYERS, 0.5F, 1.0F));
            }

            if (f >= 1.0F && !isCharged(stack) && loadProjectiles(user, stack)) {
                loadingSounds.end()
                        .ifPresent(
                                sound -> world.playSound(
                                        null,
                                        user.getX(),
                                        user.getY(),
                                        user.getZ(),
                                        (SoundEvent)sound.value(),
                                        user.getSoundSource(),
                                        1.0F,
                                        1.0F / (world.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
                                )
                        );
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return getPullTime(stack, user) + 3;
    }

    public static int getPullTime(ItemStack stack, LivingEntity user) {
        float f = EnchantmentHelper.modifyCrossbowChargingTime(stack, user, (float) user.getAttributeValue(KielsonsAPIEntityAttributes.PULL_TIME));
        return Mth.floor(f * 20.0f);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }


    private static float getPullProgress(int useTicks, ItemStack stack, LivingEntity user) {
        float f = (float)useTicks / getPullTime(stack, user);
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return stack.is(this);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }

    public CrossbowItem.ChargingSounds getLoadingSounds(ItemStack stack) {
        return (CrossbowItem.ChargingSounds)EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS)
                .orElse(DEFAULT_LOADING_SOUNDS);
    }

    public static enum ChargeType implements StringRepresentable {
        NONE("none"),
        ARROW("arrow"),
        ROCKET("rocket");

        public static final Codec<CustomCrossbow.ChargeType> CODEC = StringRepresentable.fromEnum(CustomCrossbow.ChargeType::values);
        private final String name;

        private ChargeType(final String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}