package com.kielson.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.kielson.KielsonsAPI.MOD_ID;

public class ModItems {

    public static final Item TEST_BOW = register("test_bow", new CustomBow(10, 3, 5, new Item.Settings()));
    public static final Item TEST_CROSSBOW = register("test_crossbow", new CustomBow(1, 0.5, 1, new Item.Settings()));

    public static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(MOD_ID, id), item);
    }

    public static void registerModItems() {int x = 1;}
}
