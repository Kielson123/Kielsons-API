package com.kielson;

import com.kielson.events.KielsonsEvents;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KielsonsAPI implements ModInitializer {
	public static final String MOD_ID = "kielsonsapi";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		KielsonsEntityAttributes.registerEntityAttributes();

		KielsonsEvents.ON_HEAL.register((livingEntity, original) -> {
			if(original == 0f || livingEntity.getAttributeInstance(KielsonsEntityAttributes.HEALING) == null) return original;
			return (float) (original * livingEntity.getAttributeValue(KielsonsEntityAttributes.HEALING));
		});
	}
}