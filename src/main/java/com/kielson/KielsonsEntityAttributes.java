package com.kielson;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import static com.kielson.KielsonsAPI.MOD_ID;

public class KielsonsEntityAttributes {

    public static final RegistryEntry<EntityAttribute> HEALING_MULTIPLIER = register("healing_multiplier",
            new ClampedEntityAttribute("attribute.name.generic.healing_multiplier", 1, 0, 1024).setTracked(true));
    public static final RegistryEntry<EntityAttribute> EXPERIENCE = register("experience",
            new ClampedEntityAttribute("attribute.name.generic.experience", 1, 0,1024).setTracked(true));
    public static final RegistryEntry<EntityAttribute> LUNG_CAPACITY = register("lung_capacity",
            new ClampedEntityAttribute("attribute.name.generic.lung_capacity", 0, -2048, 2048).setTracked(true));
    public static final RegistryEntry<EntityAttribute> SWIMMING_SPEED = register("swimming_speed",
            new ClampedEntityAttribute("attribute.name.generic.swimming_speed", 0.5, 0, 1).setTracked(true));
    public static final RegistryEntry<EntityAttribute> MOB_DETECTION_RANGE = register("mob_detection_range",
            new ClampedEntityAttribute("attribute.name.generic.mob_detection_range", 0, -1024, 1024).setTracked(true).setCategory(EntityAttribute.Category.NEGATIVE));
    public static final RegistryEntry<EntityAttribute> ITEM_PICK_UP_RANGE = register("item_pick_up_range",
            new ClampedEntityAttribute("attribute.name.generic.item_pick_up_range", 0, -64, 64).setTracked(true));
    public static final RegistryEntry<EntityAttribute> RANGED_DAMAGE = register("ranged_damage",
            new ClampedEntityAttribute("attribute.name.generic.ranged_damage", 0, 0, 2048).setTracked(true));
    public static final RegistryEntry<EntityAttribute> RANGED_ACCURACY = register("ranged_accuracy",
            new ClampedEntityAttribute("attribute.name.generic.ranged_accuracy", 0, -1024, 1024).setTracked(true));
    public static final RegistryEntry<EntityAttribute> PULL_TIME = register("pull_time",
            new ClampedEntityAttribute("attribute.name.generic.pull_time", 0, 0, 2048).setTracked(true));


    private static RegistryEntry<EntityAttribute> register(String name, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(MOD_ID, name), attribute);
    }

    public static void registerEntityAttributes() {int x = 1;}
}
