package com.kielson.util;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ItemHelper {

    public static Optional<Integer> checkEnchantmentLevel(ItemStack itemStack, ResourceKey<Enchantment> enchantment){
        if (!itemStack.isEnchanted()) return Optional.empty();
        Set<Holder<Enchantment>> enchantments = itemStack.getEnchantments().keySet();
        int level = 0;
        for (int j = 0; j < enchantments.size(); j++){
            Optional<Holder<Enchantment>> optionalEnchantmentEntry = enchantments.stream().findFirst();
            if (optionalEnchantmentEntry.get().unwrapKey().isPresent() && optionalEnchantmentEntry.get().unwrapKey().get() == enchantment && EnchantmentHelper.getItemEnchantmentLevel(optionalEnchantmentEntry.get(), itemStack) > 0){
                level = EnchantmentHelper.getItemEnchantmentLevel(optionalEnchantmentEntry.get(), itemStack);
                break;
            }
        }
        return Optional.of(level);
    }

    public static Optional<Double> getAttributeValue(ItemStack itemStack, Holder<Attribute> entityAttribute){
        List<ItemAttributeModifiers.Entry> attributeModifiers = Objects.requireNonNull(itemStack.get(DataComponents.ATTRIBUTE_MODIFIERS)).modifiers();
        double attributeValue = 0.0;
        for (ItemAttributeModifiers.Entry modifier : attributeModifiers) {
            if (modifier.attribute() == entityAttribute) {
                AttributeModifier attributeModifier = modifier.modifier();
                attributeValue += attributeModifier.amount();
            }
        }
        return attributeValue > 0 ? Optional.of(attributeValue) : Optional.empty();
    }

    public static Item registerItem(String modId, String name, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(modId, name));
        Item item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static Item registerItem(String modId, String name, Item.Properties settings) {
        return registerItem(modId, name, Item::new, settings);
    }
}
