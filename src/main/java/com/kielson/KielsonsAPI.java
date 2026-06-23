package com.kielson;

import com.kielson.events.KielsonsAPIEvents;
import com.kielson.item.CustomShield;
import com.kielson.item.ShieldMaterial;
import com.kielson.util.ItemHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KielsonsAPI implements ModInitializer {
	public static final String MOD_ID = "kielsonsapi";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		KielsonsAPIEntityAttributes.initialize();
		KielsonsAPIComponents.initialize();
		KielsonsAPIEvents.initialize();
	}

	public static final Item CUSTOM_SHIELD = ItemHelper.registerItem(MOD_ID, "custom_shield", properties -> new CustomShield(ShieldMaterial.IRON, properties), new Item.Properties());

	public static boolean isEnhancedCombatLoaded(){
		return FabricLoader.getInstance().isModLoaded("kielsons_enhanced_combat");
	}
	public static boolean isBetterCombatLoaded(){
		return FabricLoader.getInstance().isModLoaded("bettercombat");
	}
}