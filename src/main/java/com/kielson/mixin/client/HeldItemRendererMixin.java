package com.kielson.mixin.client;

import com.kielson.item.CustomBow;
import com.kielson.item.CustomCrossbow;
import com.kielson.util.BowInterface;
import com.kielson.util.CrossbowInterface;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInHandRenderer.class)
abstract class HeldItemRendererMixin {

    @WrapOperation(method = "evaluateWhichHandsToRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private static boolean KielsonsAPI$getHandRenderType(ItemStack itemStack, Item item, Operation<Boolean> original) {
        if (item instanceof BowInterface) {
            return true;
        }
        return original.call(itemStack, item);
    }

    @WrapOperation(method = "selectionUsingItemWhileHoldingBowLike", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private static boolean KielsonsAPI$getUsingItemHandRenderType(ItemStack itemStack, Item item, Operation<Boolean> original) {
        if (item instanceof BowInterface) {
            return true;
        }
        return original.call(itemStack, item);
    }

    @WrapOperation(method = "isChargedCrossbow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private static boolean KielsonsAPI$isChargedCrossbow(ItemStack itemStack, Item item, Operation<Boolean> original) {
        if (item instanceof CrossbowInterface) {
            return true;
        }
        return original.call(itemStack, item);
    }

    @WrapOperation(method = "renderArmWithItem", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean KielsonsAPI$renderFirstPersonItem(ItemStack itemStack, Item item, Operation<Boolean> original) {
        if (item instanceof CustomCrossbow) {
            return true;
        }
        return original.call(itemStack, item);
    }
}