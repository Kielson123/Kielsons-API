package com.kielson.mixin.client;

import com.kielson.item.CustomBow;
import com.kielson.item.CustomCrossbow;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemInHandRenderer.class)
abstract class ItemInHandRendererMixin {

    @ModifyExpressionValue(method = "evaluateWhichHandsToRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z", ordinal = 0))
    private static boolean KielsonsAPI$addReferencesToCustomBow(boolean original, @Local(ordinal = 0) ItemStack mainHandItem, @Local(ordinal = 1) ItemStack offhandItem){
        return mainHandItem.getItem() instanceof BowItem || offhandItem.getItem() instanceof BowItem;
    }

    @ModifyExpressionValue(method = "evaluateWhichHandsToRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z", ordinal = 2))
    private static boolean KielsonsAPI$addReferencesToCustomCrossbow(boolean original, @Local(ordinal = 0) ItemStack mainHandItem, @Local(ordinal = 1) ItemStack offhandItem){
        return mainHandItem.getItem() instanceof CrossbowItem || offhandItem.getItem() instanceof CrossbowItem;
    }

    @ModifyExpressionValue(method = "selectionUsingItemWhileHoldingBowLike", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private static boolean KielsonsAPI$addReferences(boolean original, @Local(ordinal = 0) ItemStack usedItemStack){
        return !(usedItemStack.getItem() instanceof ProjectileWeaponItem);
    }

    @ModifyExpressionValue(method = "isChargedCrossbow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private static boolean KielsonsAPI$addReferencesToChargedCrossbow(boolean original, @Local(ordinal = 0, argsOnly = true) ItemStack item){
        return item.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(item);
    }

    @ModifyExpressionValue(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private static boolean KielsonsAPI$addReferencesTo(boolean original, @Local(ordinal = 0, argsOnly = true) ItemStack itemStack){
        return itemStack.getItem() instanceof CrossbowItem;
    }

    @Redirect(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CrossbowItem;getChargeDuration(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)I"))
    private static int KielsonsAPI$changeCrossbowPullTime(ItemStack crossbow, LivingEntity user){
        return CustomCrossbow.getChargeDuration(crossbow, user);
    }
}