package com.kielson.mixin;

import com.kielson.KielsonsAPIEntityAttributes;
import com.kielson.util.BowInterface;
import com.kielson.util.ItemHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.kielson.KielsonsAPI.MOD_ID;

@Mixin(BowItem.class)
abstract class BowItemMixin extends ProjectileWeaponItem implements BowInterface {
    @Unique private static final double PROJECTILE_DAMAGE = 6.0;
    @Unique private static final double PULL_TIME = 1.0;
    @Unique private static final double PROJECTILE_VELOCITY = 3.0;


    BowItemMixin(Item.Properties settings) {
        super(settings);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;<init>(Lnet/minecraft/world/item/Item$Properties;)V"))
    private static Properties KielsonsAPI$addCustomAttributes(Properties settings){
        return settings.attributes(ItemAttributeModifiers.builder()
                .add(KielsonsAPIEntityAttributes.RANGED_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "bow"), PROJECTILE_DAMAGE, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
                .add(KielsonsAPIEntityAttributes.PULL_TIME, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MOD_ID, "bow"), PULL_TIME, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
                .build());
    }

    @Unique
    public float getCustomPullProgress(int useTicks, LivingEntity user, ItemStack itemStack) {
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
    @Unique private int ticks;
    @Unique private LivingEntity user;
    @Unique private ItemStack itemStack;

    @WrapOperation(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BowItem;getPowerForTime(I)F"))
    private float KielsonsAPI$applyCustomPullTime(int useTicks, Operation<Float> original, @Local(argsOnly = true) LivingEntity user, @Local(argsOnly = true) ItemStack itemStack) {
        this.ticks = useTicks;
        this.user = user;
        this.itemStack = itemStack;
        return getCustomPullProgress(useTicks, user, itemStack);
    }

    @ModifyArg(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BowItem;shoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;Ljava/util/List;FFZLnet/minecraft/world/entity/LivingEntity;)V"), index = 5)
    private float KielsonsAPI$applyCustomVelocity(float par6){
        return (float) (getCustomPullProgress(ticks, user, itemStack) * PROJECTILE_VELOCITY);
    }

    @Inject(method = "shootProjectile", at = @At(value = "RETURN"))
    private void KielsonsAPI$applyCustomDamage(LivingEntity shooter, Projectile projectile, int index, float speed, float divergence, float yaw, LivingEntity target, CallbackInfo ci) {
        if (projectile instanceof AbstractArrow persistentProjectile) {
            double damage = shooter.getAttributeValue(KielsonsAPIEntityAttributes.RANGED_DAMAGE) / PROJECTILE_VELOCITY;
            ItemStack handStack = shooter.getItemInHand(shooter.getUsedItemHand());
            if (handStack.getItem() instanceof BowItem && ItemHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).isPresent()){
                damage += (int) ((damage * 0.25) * (ItemHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).get() + 1));
            }
            persistentProjectile.setBaseDamage(damage);
        }
    }
}