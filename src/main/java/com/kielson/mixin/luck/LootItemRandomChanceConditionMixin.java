package com.kielson.mixin.luck;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LootItemRandomChanceCondition.class)
public class LootItemRandomChanceConditionMixin {

    @Redirect(method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextFloat()F"))
    private float Kielson$skewRandomRoll(RandomSource random, LootContext context) {
        float roll = random.nextFloat();
        float luck = context.getLuck();

        if (luck > 0) {
            return roll / (1.0f + (luck * 0.50f));
        }

        return roll;
    }
}
