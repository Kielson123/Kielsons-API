package com.kielson.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;

public class KielsonsEvents {
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
}
