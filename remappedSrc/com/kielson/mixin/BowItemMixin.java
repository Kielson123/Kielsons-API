package com.kielson.mixin;

import com.kielson.KielsonsEntityAttributes;
import com.kielson.util.BowInterface;
import com.kielson.util.RangedWeaponHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.kielson.KielsonsAPI.MOD_ID;

@Mixin(BowItem.class)
abstract class BowItemMixin extends RangedWeaponItem implements BowInterface {
    @Unique private static final double PROJECTILE_DAMAGE = 6.0;
    @Unique private static final double PULL_TIME = 1.0;
    @Unique private static final double PROJECTILE_VELOCITY = 3.0;


    BowItemMixin(Item.Settings settings) {
        super(settings);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/RangedWeaponItem;<init>(Lnet/minecraft/item/Item$Settings;)V"))
    private static net.minecraft.item.Item.Settings KielsonsAPI$addCustomAttributes(net.minecraft.item.Item.Settings settings){
        return settings.attributeModifiers(AttributeModifiersComponent.builder()
                .add(KielsonsEntityAttributes.RANGED_DAMAGE, new EntityAttributeModifier(Identifier.of(MOD_ID, "bow"), PROJECTILE_DAMAGE, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
                .add(KielsonsEntityAttributes.PULL_TIME, new EntityAttributeModifier(Identifier.of(MOD_ID, "bow"), PULL_TIME, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
                .build());
    }

    @Unique
    public float getCustomPullProgress(int useTicks, LivingEntity user, ItemStack itemStack) {
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
    @Unique private int ticks;
    @Unique private LivingEntity user;
    @Unique private ItemStack itemStack;

    @WrapOperation(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"))
    private float KielsonsAPI$applyCustomPullTime(int useTicks, Operation<Float> original, @Local(argsOnly = true) LivingEntity user, @Local(argsOnly = true) ItemStack itemStack) {
        this.ticks = useTicks;
        this.user = user;
        this.itemStack = itemStack;
        return getCustomPullProgress(useTicks, user, itemStack);
    }

    @ModifyArg(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;shootAll(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Ljava/util/List;FFZLnet/minecraft/entity/LivingEntity;)V"), index = 5)
    private float KielsonsAPI$applyCustomVelocity(float par6){
        return (float) (getCustomPullProgress(ticks, user, itemStack) * PROJECTILE_VELOCITY);
    }

    @Inject(method = "shoot", at = @At(value = "RETURN"))
    private void KielsonsAPI$applyCustomDamage(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, LivingEntity target, CallbackInfo ci) {
        if (projectile instanceof PersistentProjectileEntity persistentProjectile) {
            double damage = shooter.getAttributeValue(KielsonsEntityAttributes.RANGED_DAMAGE) / PROJECTILE_VELOCITY;
            ItemStack handStack = shooter.getStackInHand(shooter.getActiveHand());
            if (handStack.getItem() instanceof BowItem && RangedWeaponHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).isPresent()){
                damage += (int) ((damage * 0.25) * (RangedWeaponHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).get() + 1));
            }
            persistentProjectile.setDamage(damage);
        }
    }
}