package com.kielson.mixin.luck;

import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootPoolSingletonContainer.class)
public class LootPoolSingletonContainerMixin {

    @Shadow @Final @Mutable protected int quality;
    @Shadow @Final protected int weight;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void Kielson$boostQuality(CallbackInfo ci) {
        if (this.quality == 0) {

            if (this.weight == 1) {
                this.quality = 3;
            }

            else if (this.weight > 1 && this.weight <= 5) {
                this.quality = 1;
            }

            else if (this.weight >= 20) {
                this.quality = -2;
            }
        }
    }
}
