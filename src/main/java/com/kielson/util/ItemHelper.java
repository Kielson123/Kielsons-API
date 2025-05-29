package com.kielson.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class ItemHelper {

    public static Optional<Integer> checkEnchantmentLevel(ItemStack itemStack, RegistryKey<Enchantment> enchantment){
        if (!itemStack.hasEnchantments()) return Optional.empty();
        Set<RegistryEntry<Enchantment>> enchantments = itemStack.getEnchantments().getEnchantments();
        int level = 0;
        for (int j = 0; j < enchantments.size(); j++){
            Optional<RegistryEntry<Enchantment>> optionalEnchantmentEntry = enchantments.stream().findFirst();
            if (optionalEnchantmentEntry.get().getKey().isPresent() && optionalEnchantmentEntry.get().getKey().get() == enchantment && EnchantmentHelper.getLevel(optionalEnchantmentEntry.get(), itemStack) > 0){
                level = EnchantmentHelper.getLevel(optionalEnchantmentEntry.get(), itemStack);
                break;
            }
        }
        return Optional.of(level);
    }

    public static Optional<Double> getAttributeValue(ItemStack itemStack, RegistryEntry<EntityAttribute> entityAttribute){
        List<AttributeModifiersComponent.Entry> attributeModifiers = Objects.requireNonNull(itemStack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS)).modifiers();
        double attributeValue = 0.0;
        for (AttributeModifiersComponent.Entry modifier : attributeModifiers) {
            if (modifier.attribute() == entityAttribute) {
                EntityAttributeModifier attributeModifier = modifier.modifier();
                attributeValue += attributeModifier.value();
            }
        }
        return attributeValue > 0 ? Optional.of(attributeValue) : Optional.empty();
    }

    public static Item registerItem(String modId, String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(modId, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static Item registerItem(String modId, String name, Item.Settings settings) {
        return registerItem(modId, name, Item::new, settings);
    }
}
