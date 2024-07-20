package com.kielson.mixin.client;

import com.kielson.client.ModelPredicateHelper;
import com.kielson.item.CustomRangedWeaponItem;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "run", at = @At("HEAD"))
    private void KielsonsAPI$run_HEAD(CallbackInfo ci) {
        for (CustomRangedWeaponItem customRangedWeaponItem: CustomRangedWeaponItem.instances) {
            ModelPredicateHelper.registerRangedWeaponModelPredicates(customRangedWeaponItem);
        }
    }
}