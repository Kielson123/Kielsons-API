package com.kielson.mixin.luck;

import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootParams.Builder.class)
public abstract class LootParamsBuilderMixin {

    @Shadow
    public abstract LootParams.Builder withLuck(float luck);

    @Inject(method = "withParameter", at = @At("HEAD"))
    private <T> void Kielson$captureLuckFromParam(ContextKey<T> param, T value, CallbackInfoReturnable<LootParams.Builder> cir) {
        if (value instanceof Player player) {
            if (param == LootContextParams.THIS_ENTITY || param == LootContextParams.ATTACKING_ENTITY || param == LootContextParams.LAST_DAMAGE_PLAYER) {
                this.withLuck(player.getLuck());
            }
        }
    }

    @Inject(method = "withOptionalParameter", at = @At("HEAD"))
    private <T> void Kielson$captureLuckFromOptionalParam(ContextKey<T> param, T value, CallbackInfoReturnable<LootParams.Builder> cir) {
        if (value instanceof Player player) {
            if (param == LootContextParams.THIS_ENTITY || param == LootContextParams.ATTACKING_ENTITY || param == LootContextParams.LAST_DAMAGE_PLAYER) {
                this.withLuck(player.getLuck());
            }
        }
    }
}
