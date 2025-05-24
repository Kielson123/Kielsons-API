package com.kielson.mixin;

import com.kielson.KielsonsEntityAttributes;
import com.kielson.util.CrossbowInterface;
import com.kielson.util.ItemHelper;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.kielson.KielsonsAPI.MOD_ID;

@Mixin(CrossbowItem.class)
abstract class CrossbowItemMixin extends RangedWeaponItem implements CrossbowInterface {
    @Unique private static final double PROJECTILE_DAMAGE = 9.0;
    @Unique private static final double PULL_TIME = 1.25;
    @Unique private static final double PROJECTILE_VELOCITY = 3.15;

    public CrossbowItemMixin(Settings settings) {
        super(settings);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/RangedWeaponItem;<init>(Lnet/minecraft/item/Item$Settings;)V"))
    private static Settings KielsonsAPI$addCustomAttributes(Settings settings){
        return settings.attributeModifiers(AttributeModifiersComponent.builder()
                .add(KielsonsEntityAttributes.RANGED_DAMAGE, new EntityAttributeModifier(Identifier.of(MOD_ID, "crossbow"), PROJECTILE_DAMAGE, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
                .add(KielsonsEntityAttributes.PULL_TIME, new EntityAttributeModifier(Identifier.of(MOD_ID, "crossbow"), PULL_TIME, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
                .build());
    }

    @ModifyArg(method = "getPullTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getCrossbowChargeTime(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;F)F"), index = 2)
    private static float KielsonsAPI$applyCustomPullTime(float baseCrossbowChargeTime, @Local(argsOnly = true) LivingEntity user) {
        return (float) user.getAttributeValue(KielsonsEntityAttributes.PULL_TIME);
    }

    @Inject(method = "shoot", at = @At(value = "RETURN"))
    private void KielsonsAPI$applyCustomDamage(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, LivingEntity target, CallbackInfo ci) {
        if (projectile instanceof PersistentProjectileEntity persistentProjectile) {
            double damage = shooter.getAttributeValue(KielsonsEntityAttributes.RANGED_DAMAGE) / PROJECTILE_VELOCITY;
            ItemStack handStack = shooter.getStackInHand(shooter.getActiveHand());
            if (handStack.getItem() instanceof CrossbowItem && ItemHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).isPresent()){
                damage += (int) ((damage * 0.25) * (ItemHelper.checkEnchantmentLevel(handStack, Enchantments.POWER).get() + 1));
            }
            persistentProjectile.setDamage(damage);
        }
    }

    @Inject(method = "getSpeed", at = @At("HEAD"), cancellable = true)
    private static void KielsonsAPI$applyCustomVelocity(ChargedProjectilesComponent stack, CallbackInfoReturnable<Float> cir){
        if (stack.contains(Items.FIREWORK_ROCKET)) {
            cir.setReturnValue((float) (PROJECTILE_VELOCITY / 1.96875));
        }
        cir.setReturnValue((float) PROJECTILE_VELOCITY);
    }
}
