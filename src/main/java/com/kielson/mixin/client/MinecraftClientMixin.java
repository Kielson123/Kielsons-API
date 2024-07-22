package com.kielson.mixin.client;

import com.kielson.client.ModelPredicateHelper;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    /*@Inject(method = "run", at = @At("HEAD"))
    private void KielsonsAPI$registerModelPredicates(CallbackInfo ci) {
        for (var bow: CustomBow.instances) {
            ModelPredicateHelper.registerBowModelPredicates(bow);
        }
        for (var crossbow: CustomCrossbow.instances) {
            ModelPredicateHelper.registerCrossbowModelPredicates(crossbow);
        }
    }*/
}