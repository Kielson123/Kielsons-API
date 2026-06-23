package com.kielson.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.List;
import java.util.Optional;

public record ShieldMaterial(int durability, float baseBlockedDamage, float blockedDamagePercentage, float disabledCooldown, TagKey<Item> repairItems) {

    public static final ShieldMaterial IRON = new ShieldMaterial(30, 1.0F, 0.555F, 1F, ItemTags.REPAIRS_IRON_ARMOR);

    public Item.Properties applyShieldProperties(final Item.Properties properties){
        return properties
                .durability(durability)
                .repairable(repairItems)
                .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
                .delayedComponent(DataComponents.BLOCKS_ATTACKS, context -> new BlocksAttacks(
                        0.25F,
                        disabledCooldown / 5F,
                        List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), baseBlockedDamage, blockedDamagePercentage)),
                        new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                        Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                        Optional.of(SoundEvents.SHIELD_BLOCK),
                        Optional.of(SoundEvents.SHIELD_BREAK)));
    }
}
