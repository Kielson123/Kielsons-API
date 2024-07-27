package com.kielson.client;

import com.kielson.KielsonsEntityAttributes;
import com.kielson.item.CustomBow;
import com.kielson.item.CustomCrossbow;
import com.kielson.util.RangedWeaponHelper;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class ModelPredicateHelper {

    public static void registerBowModelPredicates(CustomBow bow) {
        ModelPredicateProviderRegistry.register(bow, Identifier.of("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                assert RangedWeaponHelper.getAttributeValue(stack, KielsonsEntityAttributes.PULL_TIME).isPresent();
                return entity.getActiveItem() != stack ? 0.0F : (float) ((stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / RangedWeaponHelper.getAttributeValue(stack, KielsonsEntityAttributes.PULL_TIME).get());
            }
        });
        ModelPredicateProviderRegistry.register(bow, Identifier.of("pulling"), (stack, world, entity, seed) ->
                entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
    }

    public static void registerCrossbowModelPredicates(CustomCrossbow crossbow) {
        Identifier[] predicatesToCopy = new Identifier[] {Identifier.of("pull"), Identifier.of("pulling"), Identifier.of("charged"), Identifier.of("firework")};
        for (Identifier predicateId : predicatesToCopy) {
            ModelPredicateProviderRegistry.register(crossbow, predicateId, (stack, world, entity, seed) -> {
                assert stack.getItem() instanceof CrossbowItem;
                return Objects.requireNonNull(ModelPredicateProviderRegistry.get(stack, predicateId)).call(stack, world, entity, seed);
            });
        }
    }
}
