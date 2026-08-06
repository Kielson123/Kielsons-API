package com.kielson;

import com.kielson.events.KielsonsAPIEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KielsonsAPI implements ModInitializer {
	public static final String MOD_ID = "kielsonsapi";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		KielsonsAPIAttributes.initialize();
		KielsonsAPIComponents.initialize();
		KielsonsAPIEvents.initialize();
	}

	public static boolean isExpandedWeaponryLoaded(){
		return FabricLoader.getInstance().isModLoaded("expanded_weaponry");
	}

	public static boolean isRingsAndThingsLoaded(){
		return FabricLoader.getInstance().isModLoaded("rings_and_things");
	}
	public static boolean isBetterCombatLoaded(){
		return FabricLoader.getInstance().isModLoaded("bettercombat");
	}
}