package com.kielson.client;

import com.kielson.util.CustomRangedWeapon;
import com.kielson.util.TooltipUtil;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class TooltipHelper {

    public static void updateTooltipText(ItemStack itemStack, List<Text> lines) {
        if (itemStack.getItem() instanceof CustomRangedWeapon) {
            mergeAttributeLines(lines);
            replaceAttributeLines(lines);
        }
        TooltipUtil.addPullTime(itemStack, lines);
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

    private static void replaceAttributeLines(List<Text> tooltip) {
        String attributeTranslationKey = "attribute.kielsonsAPI.ranged_damage";
        for (int i = 0; i < tooltip.size(); i++) {
            Text line = tooltip.get(i);
            TextContent content = line.getContent();
            if (content instanceof TranslatableTextContent translatable) {
                boolean isProjectileAttributeLine = false;
                double attributeValue = 0.0;
                if (translatable.getKey().startsWith("attribute.modifier.plus.0")) {
                    for (Object arg : translatable.getArgs()) {
                        if (arg instanceof String string) {
                            try {
                                attributeValue = Double.parseDouble(string);
                            } catch (Exception ignored) {
                            }
                        }
                        if (arg instanceof Text attributeText) {
                            if (attributeText.getContent() instanceof TranslatableTextContent attributeTranslatable) {
                                if (attributeTranslatable.getKey().startsWith(attributeTranslationKey)) {
                                    isProjectileAttributeLine = true;
                                }
                            }
                        }
                    }
                }
                if (isProjectileAttributeLine && attributeValue > 0) {
                    Text greenAttributeLine = Text.literal(" ")
                            .append(Text.translatable("attribute.modifier.equals." + EntityAttributeModifier.Operation.ADD_VALUE.getId(),
                                    AttributeModifiersComponent.DECIMAL_FORMAT.format(attributeValue), Text.translatable(attributeTranslationKey)))
                            .formatted(Formatting.DARK_GREEN);
                    tooltip.set(i, greenAttributeLine);
                }
            }
        }
    }
}
