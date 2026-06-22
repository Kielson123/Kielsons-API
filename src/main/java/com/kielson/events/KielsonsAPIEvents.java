package com.kielson.events;

import com.kielson.KielsonsAPIEntityAttributes;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;

public class KielsonsAPIEvents {
    public static final Event<Healed> ON_HEAL = EventFactory.createArrayBacked(Healed.class, callbacks -> (livingEntity, original) -> {
        float previous = original;
        for(Healed callback : callbacks) {
            previous = callback.onHeal(livingEntity, previous);
        }
        return previous;
    });

    @FunctionalInterface
    public interface Healed {
        float onHeal(final LivingEntity livingEntity, final float original);
    }

    public static void initialize() {
        KielsonsAPIEvents.ON_HEAL.register((livingEntity, original) -> {
            if(original == 0f || livingEntity.getAttribute(KielsonsAPIEntityAttributes.HEALING_MULTIPLIER) == null) return original;
            return (float) (original * livingEntity.getAttributeValue(KielsonsAPIEntityAttributes.HEALING_MULTIPLIER));
        });
    }
}
