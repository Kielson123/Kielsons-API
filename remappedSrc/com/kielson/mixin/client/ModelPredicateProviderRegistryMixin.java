package com.kielson.mixin.client;

import com.kielson.KielsonsEntityAttributes;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModelPredicateProviderRegistry.class)
public class ModelPredicateProviderRegistryMixin {

    static {
        ModelPredicateProviderRegistry.register(Items.BOW, Identifier.ofVanilla("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getActiveItem() != stack ? 0.0F :
                        (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / Math.clamp((float) (entity.getAttributeValue(KielsonsEntityAttributes.PULL_TIME) * 20.0), 0.01f, Float.MAX_VALUE);
            }
        });
    }
}
