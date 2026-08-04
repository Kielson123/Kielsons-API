package com.kielson.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.kielson.item.CustomShield;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;

import static com.kielson.KielsonsAPI.isBetterCombatLoaded;
import static com.kielson.KielsonsAPIComponents.TWO_HANDED;

public class TooltipHelper {

    public static void updateTooltipText(ItemStack itemStack, List<Component> lines) {
        addTwoHandedTooltip(itemStack, lines);

        if (itemStack.getItem() instanceof CustomShield) {
            addShieldTooltips(itemStack, lines);
        }

        if (itemStack.getItem() instanceof ProjectileWeaponItem){
            mergeAttributeLines(lines);
            fixRangedDamage(lines, itemStack);
            fixPullTime(lines, itemStack);
        }
    }

    private static void addTwoHandedTooltip(ItemStack stack, List<Component> lines){
        if (Boolean.TRUE.equals(stack.get(TWO_HANDED)) && !isBetterCombatLoaded()) {
            Optional<Integer> goodValue = Optional.empty();
            for (int i = 0; i < lines.size(); ++i) {
                Component line = lines.get(i);
                ComponentContents content = line.getContents();
                if (content instanceof TranslatableContents translatable) {
                    if (translatable.getKey().startsWith("item.modifiers") || translatable.getKey().startsWith("potion.whenDrank")) {
                        goodValue = Optional.of(i);
                        break;
                    }
                }
            }
            Component text = Component.empty().append(Component.translatable("item.modifiers.two_handed").withStyle(ChatFormatting.GRAY));
            if (goodValue.isEmpty()) lines.addLast(text);
            else lines.add(goodValue.get(), text);
        }
    }

    private static void addShieldTooltips(ItemStack stack, List<Component> lines){
        BlocksAttacks.DamageReduction damageReduction = Objects.requireNonNull(stack.get(DataComponents.BLOCKS_ATTACKS)).damageReductions().getFirst();
        String minBlockedDamage = String.valueOf(damageReduction.base()).replaceAll("[.]0+", "");
        String percBlockedDamage = String.valueOf(damageReduction.factor() * 100).replaceAll("[.]0+", "");
        String disabledCooldown = String.valueOf(Objects.requireNonNull(stack.get(DataComponents.BLOCKS_ATTACKS)).disableCooldownScale() * 5).replaceAll("[.]0+", "");

        lines.addLast(Component.literal(""));
        lines.addLast(Component.translatable("shield.whenUsed").withStyle(ChatFormatting.GRAY));
        lines.addLast(Component.literal(" ").append(Component.translatable("shield.minBlockedDamage", minBlockedDamage).withStyle(ChatFormatting.BLUE)));
        lines.addLast(Component.literal(" ").append(Component.translatable("shield.percBlockedDamage", percBlockedDamage).withStyle(ChatFormatting.BLUE)));
        lines.addLast(Component.literal(" ").append(Component.translatable("shield.disabledCooldown", disabledCooldown).withStyle(ChatFormatting.BLUE)));
    }

    private static void mergeAttributeLines(List<Component> tooltip) {
        List<Component> heldInHandLines = new ArrayList<>();
        List<Component> mainHandAttributes = new ArrayList<>();
        List<Component> offHandAttributes = new ArrayList<>();
        for (Component line : tooltip) {
            ComponentContents content = line.getContents();
            if (content instanceof TranslatableContents translatableText) {
                if (translatableText.getKey().startsWith("item.modifiers")) {
                    heldInHandLines.add(line);
                }
                if (translatableText.getKey().startsWith("attribute.modifier")) {
                    if (heldInHandLines.size() == 1) {
                        mainHandAttributes.add(line);
                    }
                    if (heldInHandLines.size() == 2) {
                        offHandAttributes.add(line);
                    }
                }
            }
        }
        if(heldInHandLines.size() == 2) {
            int mainHandLine = tooltip.indexOf(heldInHandLines.get(0));
            int offHandLine = tooltip.indexOf(heldInHandLines.get(1));
            tooltip.remove(mainHandLine);
            tooltip.add(mainHandLine, Component.translatable("item.modifiers.hand").withStyle(ChatFormatting.GRAY));
            tooltip.remove(offHandLine);
            for (Component offhandAttribute: offHandAttributes) {
                if(mainHandAttributes.contains(offhandAttribute)) {
                    tooltip.remove(tooltip.lastIndexOf(offhandAttribute));
                }
            }
            int lastIndex = tooltip.size() - 1;
            Component lastLine = tooltip.get(lastIndex);
            if (lastLine.getString().isEmpty()) {
                tooltip.remove(lastIndex);
            }
        }
    }

    private static void fixRangedDamage(List<Component> tooltip, ItemStack stack) {
        String attributeTranslationKey = "attribute.name.generic.ranged_damage";
        for (int i = 0; i < tooltip.size(); i++) {
            Component line = tooltip.get(i);
            ComponentContents content = line.getContents();
            if (content instanceof TranslatableContents translatable) {
                boolean isAttributeLine = false;
                double attributeValue = 0.0;
                if (translatable.getKey().startsWith("attribute.modifier.plus.0")) {
                    for (Object arg : translatable.getArgs()) {
                        if (arg instanceof String string) {
                            try {
                                attributeValue = Double.parseDouble(string);
                                if (ItemHelper.checkEnchantmentLevel(stack, Enchantments.POWER).isPresent()){
                                    attributeValue += (attributeValue * 0.25) * (ItemHelper.checkEnchantmentLevel(stack, Enchantments.POWER).get() + 1);
                                }
                            } catch (Exception ignored) {
                            }
                        }
                        if (arg instanceof Component attributeText) {
                            if (attributeText.getContents() instanceof TranslatableContents attributeTranslatable) {
                                if (attributeTranslatable.getKey().startsWith(attributeTranslationKey)) {
                                    isAttributeLine = true;
                                }
                            }
                        }
                    }
                }
                if (isAttributeLine) {
                    Component greenAttributeLine = Component.literal(" ")
                            .append(Component.translatable("attribute.modifier.equals." + AttributeModifier.Operation.ADD_VALUE.id(),
                                    ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(attributeValue < 0 ? 0 : attributeValue), Component.translatable(attributeTranslationKey)))
                            .withStyle(ChatFormatting.DARK_GREEN);
                    tooltip.set(i, greenAttributeLine);
                }
            }
        }
    }

    private static void fixPullTime(List<Component> tooltip, ItemStack stack) {
        String attributeTranslationKey = "attribute.name.generic.pull_time";
        for (int i = 0; i < tooltip.size(); i++) {
            Component line = tooltip.get(i);
            ComponentContents content = line.getContents();
            if (content instanceof TranslatableContents translatable) {
                boolean isAttributeLine = false;
                double attributeValue = 0.0;
                if (translatable.getKey().startsWith("attribute.modifier.plus.0")) {
                    for (Object arg : translatable.getArgs()) {
                        if (arg instanceof String string) {
                            try {
                                attributeValue = Double.parseDouble(string);
                                if (ItemHelper.checkEnchantmentLevel(stack, Enchantments.QUICK_CHARGE).isPresent()){
                                    attributeValue -= 0.25 * ItemHelper.checkEnchantmentLevel(stack, Enchantments.QUICK_CHARGE).get();
                                }
                            } catch (Exception ignored) {

                            }
                        }
                        if (arg instanceof Component attributeText) {
                            if (attributeText.getContents() instanceof TranslatableContents attributeTranslatable) {
                                if (attributeTranslatable.getKey().startsWith(attributeTranslationKey)) {
                                    isAttributeLine = true;
                                }
                            }
                        }
                    }
                }
                if (isAttributeLine) {
                    Component greenAttributeLine = Component.literal(" ")
                            .append(Component.translatable("attribute.modifier.equals." + AttributeModifier.Operation.ADD_VALUE.id(),
                                    ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(attributeValue < 0 ? 0 : attributeValue), Component.translatable(attributeTranslationKey)))
                            .withStyle(ChatFormatting.DARK_GREEN);
                    tooltip.set(i, greenAttributeLine);
                }
            }
        }
    }
}
