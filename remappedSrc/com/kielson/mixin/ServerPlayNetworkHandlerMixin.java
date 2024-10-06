package com.kielson.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
abstract class ServerPlayNetworkHandlerMixin {

    @Redirect(method = "onPlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    public ItemStack Kielson$getStackInHand(ServerPlayerEntity instance, Hand hand) {
        ItemStack result = null;
        switch (hand) {
            case MAIN_HAND -> result = instance.getInventory().getMainHandStack();
            case OFF_HAND -> result = instance.getInventory().offHand.get(0);
        }
        return result;
    }
}
