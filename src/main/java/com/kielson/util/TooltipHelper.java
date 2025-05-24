package com.kielson.util;

import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class TooltipHelper {

    public static void updateTooltipText(ItemStack itemStack, List<Text> lines) {
        if(itemStack.getItem() instanceof RangedWeaponItem){
            mergeAttributeLines(lines);
            fixRangedDamage(lines, itemStack);
            fixPullTime(lines, itemStack);
        }
    }


    private static void mergeAttributeLines(List<Text> tooltip) {
        List<Text> heldInHandLines = new ArrayList<>();
        List<Text> mainHandAttributes = new ArrayList<>();
        List<Text> offHandAttributes = new ArrayList<>();
        for (Text line : tooltip) {
            TextContent content = line.getContent();
            if (content instanceof TranslatableTextContent translatableText) {
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
            tooltip.add(mainHandLine, Text.translatable("item.modifiers.hand").formatted(Formatting.GRAY));
            tooltip.remove(offHandLine);
            for (Text offhandAttribute: offHandAttributes) {
                if(mainHandAttributes.contains(offhandAttribute)) {
                    tooltip.remove(tooltip.lastIndexOf(offhandAttribute));
                }
            }
            int lastIndex = tooltip.size() - 1;
            Text lastLine = tooltip.get(lastIndex);
            if (lastLine.getString().isEmpty()) {
                tooltip.remove(lastIndex);
            }
        }
    }

    private static void fixRangedDamage(List<Text> tooltip, ItemStack stack) {
        String attributeTranslationKey = "attribute.name.generic.ranged_damage";
        for (int i = 0; i < tooltip.size(); i++) {
            Text line = tooltip.get(i);
            TextContent content = line.getContent();
            if (content instanceof TranslatableTextContent translatable) {
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
                        if (arg instanceof Text attributeText) {
                            if (attributeText.getContent() instanceof TranslatableTextContent attributeTranslatable) {
                                if (attributeTranslatable.getKey().startsWith(attributeTranslationKey)) {
                                    isAttributeLine = true;
                                }
                            }
                        }
                    }
                }
                if (isAttributeLine) {
                    Text greenAttributeLine = Text.literal(" ")
                            .append(Text.translatable("attribute.modifier.equals." + EntityAttributeModifier.Operation.ADD_VALUE.getId(),
                                    AttributeModifiersComponent.DECIMAL_FORMAT.format(attributeValue < 0 ? 0 : attributeValue), Text.translatable(attributeTranslationKey)))
                            .formatted(Formatting.DARK_GREEN);
                    tooltip.set(i, greenAttributeLine);
                }
            }
        }
    }

    private static void fixPullTime(List<Text> tooltip, ItemStack stack) {
        String attributeTranslationKey = "attribute.name.generic.pull_time";
        for (int i = 0; i < tooltip.size(); i++) {
            Text line = tooltip.get(i);
            TextContent content = line.getContent();
            if (content instanceof TranslatableTextContent translatable) {
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
                        if (arg instanceof Text attributeText) {
                            if (attributeText.getContent() instanceof TranslatableTextContent attributeTranslatable) {
                                if (attributeTranslatable.getKey().startsWith(attributeTranslationKey)) {
                                    isAttributeLine = true;
                                }
                            }
                        }
                    }
                }
                if (isAttributeLine) {
                    Text greenAttributeLine = Text.literal(" ")
                            .append(Text.translatable("attribute.modifier.equals." + EntityAttributeModifier.Operation.ADD_VALUE.getId(),
                                    AttributeModifiersComponent.DECIMAL_FORMAT.format(attributeValue < 0 ? 0 : attributeValue), Text.translatable(attributeTranslationKey)))
                            .formatted(Formatting.DARK_GREEN);
                    tooltip.set(i, greenAttributeLine);
                }
            }
        }
    }
}
