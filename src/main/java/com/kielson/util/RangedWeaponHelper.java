package com.kielson.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Optional;
import java.util.Set;

public class RangedWeaponHelper {

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
}
