package com.kielson;

import com.kielson.events.KielsonsEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KielsonsAPI implements ModInitializer {
	public static final String MOD_ID = "kielsonsapi";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final boolean isEnhancedCombatLoaded = FabricLoader.getInstance().isModLoaded("kielsons_enhanced_combat");

	@Override
	public void onInitialize() {
		KielsonsEntityAttributes.initialize();
		KielsonsAPIComponents.initialize();

		KielsonsEvents.ON_HEAL.register((livingEntity, original) -> {
			if(original == 0f || livingEntity.getAttributeInstance(KielsonsEntityAttributes.HEALING_MULTIPLIER) == null) return original;
			return (float) (original * livingEntity.getAttributeValue(KielsonsEntityAttributes.HEALING_MULTIPLIER));
		});
	}

	public static boolean isKielsonsEnhancedCombatLoaded(){
		return isEnhancedCombatLoaded;
	}
}