package com.kielson.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.minecraft.registry.entry.RegistryEntry;

public class CrossbowMechanics {
    public static class PullTime {
        public static final Provider defaultProvider = (originalPullTime, crossbow) -> {
            int quickCharge = EnchantmentHelper.getLevel(, crossbow);
            return originalPullTime - (int) (originalPullTime * 0.2) * quickCharge;
        };
        public static Provider modifier = defaultProvider;
        public interface Provider {
            int getPullTime(int originalPullTime, ItemStack crossbow);
        }
    }
}
