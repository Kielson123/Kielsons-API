package com.kielson.mixin.client;

import com.kielson.item.CustomRangedWeaponItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @WrapOperation(method = "getArmPose", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean KielsonsAPI$armPoseCrossbowHold(ItemStack itemStack, Item item, Operation<Boolean> original) {
        if (itemStack.getItem() instanceof CustomRangedWeaponItem rangedWeaponItem && rangedWeaponItem.getWeaponType() == CustomRangedWeaponItem.WeaponType.CROSSBOW) {
                return true;
        }
        return original.call(itemStack, item);
    }
}
