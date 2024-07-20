package com.kielson.client;

import com.kielson.item.CustomRangedWeaponItem;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class ModelPredicateHelper {

    public static void registerRangedWeaponModelPredicates(CustomRangedWeaponItem rangedWeapon) {
        if (rangedWeapon.getWeaponType() == CustomRangedWeaponItem.WeaponType.BOW) {
            // We cannot reuse what is already registered for Vanilla bow, because it uses hardcoded pull time values
            ModelPredicateProviderRegistry.register(rangedWeapon, Identifier.of("pull"), (stack, world, entity, seed) -> {
                if (entity == null) {
                    return 0.0F;
                } else {
                    return entity.getActiveItem() != stack ? 0.0F : (float) (stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / ((float) rangedWeapon.getPullTime());
                }
            });
            ModelPredicateProviderRegistry.register(rangedWeapon, Identifier.of("pulling"), (stack, world, entity, seed) ->
                    entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
            return;
        }

        if (rangedWeapon.getWeaponType() == CustomRangedWeaponItem.WeaponType.CROSSBOW) {
            Identifier[] predicatesToCopy = new Identifier[]{Identifier.of("pull"), Identifier.of("pulling"), Identifier.of("charged"), Identifier.of("firework")};
            for (Identifier predicateId : predicatesToCopy) {
                ModelPredicateProviderRegistry.register(rangedWeapon, predicateId, (stack, world, entity, seed) ->
                        Objects.requireNonNull(ModelPredicateProviderRegistry.get(stack, predicateId)).call(stack, world, entity, seed));
            }
            return;
        }
    }
}
