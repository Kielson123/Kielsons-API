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
    public static final Event<Tick> EVERY_SECOND = EventFactory.createArrayBacked(Tick.class, callbacks -> livingEntity -> {
        for(Tick callback : callbacks) {
            callback.everySecond(livingEntity);
        }
    });

    @FunctionalInterface
    public interface Healed {
        float onHeal(final LivingEntity livingEntity, final float original);
    }
    @FunctionalInterface
    public interface Tick {
        void everySecond(final LivingEntity livingEntity);
    }

    public static void initialize() {
        KielsonsAPIEvents.ON_HEAL.register((livingEntity, original) -> {
            if(original == 0f || livingEntity.getAttribute(KielsonsAPIEntityAttributes.HEALING_MULTIPLIER) == null) return original;
            return (float) (original * livingEntity.getAttributeValue(KielsonsAPIEntityAttributes.HEALING_MULTIPLIER));
        });
        KielsonsAPIEvents.EVERY_SECOND.register(livingEntity -> {
            if(livingEntity.getAttribute(KielsonsAPIEntityAttributes.PASSIVE_REGENERATION) != null){
                double attributeValue = livingEntity.getAttributeValue(KielsonsAPIEntityAttributes.PASSIVE_REGENERATION);
                if(attributeValue > 0){
                    livingEntity.heal((float) attributeValue);
                }
            }
        });
    }
}
