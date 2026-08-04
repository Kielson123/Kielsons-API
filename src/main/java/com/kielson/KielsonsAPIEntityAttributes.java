package com.kielson;

import static com.kielson.KielsonsAPI.MOD_ID;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class KielsonsAPIEntityAttributes {

    public static final Holder<Attribute> HEALING_MULTIPLIER = register("healing_multiplier",
            new RangedAttribute("attribute.name.generic.healing_multiplier", 1, 0, 1024).setSyncable(true));
    public static final Holder<Attribute> PASSIVE_REGENERATION = register("passive_regeneration",
            new RangedAttribute("attribute.name.generic.passive_regeneration", 0, 0, 1024).setSyncable(true));
    public static final Holder<Attribute> EXPERIENCE = register("experience",
            new RangedAttribute("attribute.name.generic.experience", 1, 0,1024).setSyncable(true));
    public static final Holder<Attribute> SWIMMING_SPEED = register("swimming_speed",
            new RangedAttribute("attribute.name.generic.swimming_speed", 0.5, 0, 1).setSyncable(true));
    public static final Holder<Attribute> ITEM_PICK_UP_RANGE = register("item_pick_up_range",
            new RangedAttribute("attribute.name.generic.item_pick_up_range", 0, -64, 64).setSyncable(true));
    public static final Holder<Attribute> RANGED_DAMAGE = register("ranged_damage",
            new RangedAttribute("attribute.name.generic.ranged_damage", 0, 0, 2048).setSyncable(true));
    public static final Holder<Attribute> PULL_TIME = register("pull_time",
            new RangedAttribute("attribute.name.generic.pull_time", 0, 0, 2048).setSyncable(true));


    private static Holder<Attribute> register(String name, Attribute attribute) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, Identifier.fromNamespaceAndPath(MOD_ID, name), attribute);
    }

    public static void initialize() {}
}
