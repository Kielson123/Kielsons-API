package com.kielson;

import static com.kielson.KielsonsAPI.MOD_ID;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class KielsonsAPIAttributes {

    public static final Holder<Attribute> HEALING_MULTIPLIER = registerRanged("healing_multiplier", 1, 0, 1024);
    public static final Holder<Attribute> PASSIVE_REGENERATION = registerRanged("passive_regeneration",0, 0, 1024);
    public static final Holder<Attribute> EXPERIENCE_MULTIPLIER = registerRanged("experience_multiplier",1, 0,1024);
    public static final Holder<Attribute> TRADE_DISCOUNT_MULTIPLIER = registerRanged("trade_discount_multiplier",1, 0,64, Attribute.Sentiment.NEGATIVE);
    public static final Holder<Attribute> SWIMMING_SPEED = registerRanged("swimming_speed",1, 0, 1024);
    public static final Holder<Attribute> ITEM_PICK_UP_RANGE = registerRanged("item_pick_up_range",0, -64, 64);
    public static final Holder<Attribute> RANGED_DAMAGE = registerRanged("ranged_damage",0, 0, 2048);
    public static final Holder<Attribute> PULL_TIME = registerRanged("pull_time",0, 0, 2048, Attribute.Sentiment.NEGATIVE);
    public static final Holder<Attribute> FREEZING_RESISTANCE = registerRanged("freezing_resistance",0, 0, 1);

    public static final Holder<Attribute> BLINDNESS_IMMUNITY = registerBoolean("blindness_immunity", false);
    public static final Holder<Attribute> DARKNESS_IMMUNITY = registerBoolean("darkness_immunity", false);
    public static final Holder<Attribute> WEAKNESS_IMMUNITY = registerBoolean("weakness_immunity", false);
    public static final Holder<Attribute> MINING_FATIGUE_IMMUNITY = registerBoolean("mining_fatigue_immunity", false);
    public static final Holder<Attribute> POISON_IMMUNITY = registerBoolean("poison_immunity", false);
    public static final Holder<Attribute> WITHER_IMMUNITY = registerBoolean("wither_immunity", false);
    public static final Holder<Attribute> CREEPER_IMMUNITY = registerBoolean("creeper_immunity", false);
    public static final Holder<Attribute> VOID_IMMUNITY = registerBoolean("void_immunity", false);
    public static final Holder<Attribute> LEVITATION_IMMUNITY = registerBoolean("levitation_immunity", false);
    public static final Holder<Attribute> SLOWNESS_IMMUNITY = registerBoolean("slowness_immunity", false);
    public static final Holder<Attribute> WATER_MOB_IMMUNITY = registerBoolean("water_mob_immunity", false);
    public static final Holder<Attribute> ILLAGER_IMMUNITY = registerBoolean("illager_immunity", false);
    public static final Holder<Attribute> PIGLIN_IMMUNITY = registerBoolean("piglin_immunity", false);


    private static Holder<Attribute> registerRanged(String name, double defaultValue, double minValue, double maxValue) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, Identifier.fromNamespaceAndPath(MOD_ID, name),
                new RangedAttribute("attribute.name." + name, defaultValue, minValue, maxValue).setSyncable(true));
    }

    private static Holder<Attribute> registerRanged(String name, double defaultValue, double minValue, double maxValue, Attribute.Sentiment sentiment) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, Identifier.fromNamespaceAndPath(MOD_ID, name),
                new RangedAttribute("attribute.name." + name, defaultValue, minValue, maxValue).setSyncable(true).setSentiment(sentiment));
    }

    private static Holder<Attribute> registerBoolean(String name, boolean defaultValue) {
        double defaultValueDouble = defaultValue ? 1.0 : 0.0;
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, Identifier.fromNamespaceAndPath(MOD_ID, name),
                new RangedAttribute("attribute.name." + name, defaultValueDouble, 0.0, 1.0).setSyncable(true));
    }

    private static Holder<Attribute> registerBoolean(String name, boolean defaultValue, Attribute.Sentiment sentiment) {
        double defaultValueDouble = defaultValue ? 1.0 : 0.0;
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, Identifier.fromNamespaceAndPath(MOD_ID, name),
                new RangedAttribute("attribute.name." + name, defaultValueDouble, 0.0, 1.0).setSyncable(true).setSentiment(sentiment));
    }

    public static void initialize() {}
}
