package com.kielson.mixin.client;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    /*@WrapOperation(method = "getArmPose", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean KielsonsAPI$armPoseCrossbowHold(ItemStack itemStack, Item item, Operation<Boolean> original) {
        if (item == Items.CROSSBOW) {
            if (CustomCrossbow.instances.contains(itemStack.getItem())) {
                return true;
            }
        }
        return original.call(itemStack, item);
    }*/
}
