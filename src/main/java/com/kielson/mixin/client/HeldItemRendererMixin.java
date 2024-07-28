package com.kielson.mixin.client;

import com.kielson.item.CustomCrossbow;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HeldItemRenderer.class)
abstract class HeldItemRendererMixin {

    @WrapOperation(method = "getHandRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean KielsonsAPI$getHandRenderType(ItemStack itemStack, Item item, Operation<Boolean> original) {
        if (item instanceof CustomCrossbow) {
                return true;
        }
        return original.call(itemStack, item);
    }

    @WrapOperation(method = "getUsingItemHandRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean KielsonsAPI$getUsingItemHandRenderType(ItemStack itemStack, Item item, Operation<Boolean> original) {
        if (item instanceof CustomCrossbow) {
                return true;
        }
        return original.call(itemStack, item);
    }

    @WrapOperation(method = "isChargedCrossbow", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean KielsonsAPI$isChargedCrossbow(ItemStack itemStack, Item item, Operation<Boolean> original) {
        if (item instanceof CustomCrossbow) {
            return true;
        }
        return original.call(itemStack, item);
    }

    @WrapOperation(method = "renderFirstPersonItem", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean KielsonsAPI$renderFirstPersonItem(ItemStack itemStack, Item item, Operation<Boolean> original) {
        if (item instanceof CustomCrossbow) {
            return true;
        }
        return original.call(itemStack, item);
    }
}
