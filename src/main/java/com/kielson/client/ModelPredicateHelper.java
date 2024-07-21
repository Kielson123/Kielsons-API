package com.kielson.client;

import com.kielson.item.CustomBow;
import com.kielson.item.CustomCrossbow;
import com.kielson.util.CustomRangedWeapon;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class ModelPredicateHelper {

    public static void registerBowModelPredicates(CustomBow bow) {
        // We cannot reuse what is already registered for Vanilla bow, because it uses hardcoded pull time values
        ModelPredicateProviderRegistry.register(bow, Identifier.of("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / ((float) ((CustomRangedWeapon)bow).getRangedWeaponConfig().pull_time());
            }
        });
        ModelPredicateProviderRegistry.register(bow, Identifier.of("pulling"), (stack, world, entity, seed) ->
                entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
    }

    public static void registerCrossbowModelPredicates(CustomCrossbow crossbow) {
        var predicatesToCopy = new Identifier[] {Identifier.of("pull"), Identifier.of("pulling"), Identifier.of("charged"), Identifier.of("firework")};
        for (var predicateId : predicatesToCopy) {
            ModelPredicateProviderRegistry.register(crossbow, predicateId, (stack, world, entity, seed) -> {
                assert stack.getItem() == Items.CROSSBOW;
                return Objects.requireNonNull(ModelPredicateProviderRegistry.get(stack, predicateId)).call(stack, world, entity, seed);
            });
        }
    }
}
