package com.kielson;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import static com.kielson.KielsonsAPI.MOD_ID;

public class KielsonsEntityAttributes {

    public static final RegistryEntry<EntityAttribute> HEALING_MULTIPLIER = register("healing_multiplier", 1, 0, 1024);
    public static final RegistryEntry<EntityAttribute> EXPERIENCE = register("experience", 1, 0,1024);
    public static final RegistryEntry<EntityAttribute> LUNG_CAPACITY = register("lung_capacity", 0, -2048, 2048);
    public static final RegistryEntry<EntityAttribute> SWIMMING_SPEED = register("swimming_speed", 0.5, 0, 1);
    public static final RegistryEntry<EntityAttribute> MOB_DETECTION_RANGE = register("mob_detection_range", 0, -1024, 1024);
    public static final RegistryEntry<EntityAttribute> ITEM_PICK_UP_RANGE = register("item_pick_up_range", 0, -64, 64);
    public static final RegistryEntry<EntityAttribute> RANGED_DAMAGE = register("ranged_damage", 1, 0, 1024);
    public static final RegistryEntry<EntityAttribute> RANGED_ACCURACY = register("ranged_accuracy", 0, -1024, 1024);
    public static final RegistryEntry<EntityAttribute> DRAW_SPEED = register("draw_speed", 0, -1024, 1024);

    private static RegistryEntry<EntityAttribute> register(String name, double base, double min, double max) {
        EntityAttribute attribute = new ClampedEntityAttribute("attribute." + MOD_ID + '.' + name, base, min, max).setTracked(true);
        return Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(MOD_ID, name), attribute);
    }

    public static void registerEntityAttributes() {int x = 1;}
}
