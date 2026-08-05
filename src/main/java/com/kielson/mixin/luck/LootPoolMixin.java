package com.kielson.mixin.luck;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LootPool.class)
public class LootPoolMixin {

    @Redirect(method = "addRandomItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/providers/number/NumberProvider;getInt(Lnet/minecraft/world/level/storage/loot/LootContext;)I"))
    private int Kielson$applyGlobalLuckToRolls(NumberProvider provider, LootContext context) {
        int baseRolls = provider.getInt(context);
        float luck = context.getLuck();
        if (luck == 0) {
            return baseRolls;
        }

        float extraChance = Math.abs(luck);
        int guaranteedExtraRolls = (int) extraChance;
        float fractionalChance = extraChance - guaranteedExtraRolls;
        if (context.getRandom().nextFloat() < fractionalChance) {
            guaranteedExtraRolls++;
        }

        if (luck < 0) {
            return Math.max(0, baseRolls - guaranteedExtraRolls);
        }
        return baseRolls + guaranteedExtraRolls;
    }
}