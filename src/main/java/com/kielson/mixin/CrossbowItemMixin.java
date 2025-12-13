package com.kielson.mixin;

import com.kielson.KielsonsAPIEntityAttributes;
import com.kielson.util.CrossbowInterface;
import com.kielson.util.ItemHelper;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.kielson.KielsonsAPI.MOD_ID;

@Mixin(CrossbowItem.class)
abstract class CrossbowItemMixin extends ProjectileWeaponItem implements CrossbowInterface {
    @Unique private static final double PROJECTILE_DAMAGE = 9.0;
    @Unique private static final double PULL_TIME = 1.25;
    @Unique private static final double PROJECTILE_VELOCITY = 3.15;

    public CrossbowItemMixin(Properties settings) {
        super(settings);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;<init>(Lnet/minecraft/world/item/Item$Properties;)V"))
    private static Properties KielsonsAPI$addCustomAttributes(Properties settings){
        return settings.attributes(ItemAttributeModifiers.builder()
                .add(KielsonsAPIEntityAttributes.RANGED_DAMAGE, new AttributeModifier(Identifier.fromNamespaceAndPath(MOD_ID, "crossbow"), PROJECTILE_DAMAGE, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
                .add(KielsonsAPIEntityAttributes.PULL_TIME, new AttributeModifier(Identifier.fromNamespaceAndPath(MOD_ID, "crossbow"), PULL_TIME, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
                .build());
    }

    @ModifyArg(method = "getChargeDuration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;modifyCrossbowChargingTime(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;F)F"), index = 2)
    private static float KielsonsAPI$applyCustomPullTime(float baseCrossbowChargeTime, @Local(argsOnly = true) LivingEntity user) {
        return (float) user.getAttributeValue(KielsonsAPIEntityAttributes.PULL_TIME);
    }

    @Inject(method = "shootProjectile", at = @At(value = "RETURN"))
    private void KielsonsAPI$applyCustomDamage(LivingEntity shooter, Projectile projectile, int index, float speed, float divergence, float yaw, LivingEntity target, CallbackInfo ci) {
        if (projectile instanceof AbstractArrow persistentProjectile) {
            double damage = shooter.getAttributeValue(KielsonsAPIEntityAttributes.RANGED_DAMAGE) / PROJECTILE_VELOCITY;
            ItemStack handStack = shooter.getItemInHand(shooter.getUsedItemHand());
            if (handStack.getItem() instanceof CrossbowItem && ItemHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).isPresent()){
                damage += (int) ((damage * 0.25) * (ItemHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).get() + 1));
            }
            persistentProjectile.setBaseDamage(damage);
        }
    }

    @Inject(method = "getShootingPower", at = @At("HEAD"), cancellable = true)
    private static void KielsonsAPI$applyCustomVelocity(ChargedProjectiles stack, CallbackInfoReturnable<Float> cir){
        if (stack.contains(Items.FIREWORK_ROCKET)) {
            cir.setReturnValue((float) (PROJECTILE_VELOCITY / 1.96875));
        }
        cir.setReturnValue((float) PROJECTILE_VELOCITY);
    }
}
