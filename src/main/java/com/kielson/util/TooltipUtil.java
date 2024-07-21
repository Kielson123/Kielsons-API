package com.kielson.util;

import com.ibm.icu.text.DecimalFormat;
import com.kielson.KielsonsEntityAttributes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Objects;

public class TooltipUtil {

    public static void addPullTime(ItemStack itemStack, List<Text> lines) {
        int pullTime = readablePullTime(itemStack);
        if (pullTime > 0) {
            int lastAttributeLine = getLastAttributeLine(lines);

            if (lastAttributeLine > 0) {
                lines.add(lastAttributeLine + 1,
                        Text.literal(" ").append(
                                Text.translatable("item.kielsonsAPI.pull_time", formattedNumber(pullTime / 20F))
                                        .formatted(Formatting.DARK_GREEN)
                        )
                );
            }
        }
    }

    private static int getLastAttributeLine(List<Text> lines) {
        int lastAttributeLine = -1;
        String attributePrefix = "attribute.modifier";
        String handPrefix = "item.modifiers";
        for (int i = 0; i < lines.size(); i++) {
            Text line = lines.get(i);
            TextContent content = line.getContent();
            if (content instanceof TranslatableTextContent translatableText) {
                String key = translatableText.getKey();
                if (key.startsWith(attributePrefix) || key.startsWith(handPrefix)) {
                    lastAttributeLine = i;
                }
            }
        }
        return lastAttributeLine;
    }

    private static int readablePullTime(ItemStack itemStack) {
        Item item = itemStack.getItem();
        double pullTime = 0;
        if (item instanceof CustomRangedWeapon customBow) {
            pullTime = customBow.getRangedWeaponConfig().pull_time();
        }
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && pullTime > 0) {
            double haste = player.getAttributeValue(KielsonsEntityAttributes.DRAW_TIME);
            pullTime /= (haste / player.getAttributeBaseValue(KielsonsEntityAttributes.DRAW_TIME));
        }
        return (int) pullTime;
    }

    private static String formattedNumber(float number) {
        DecimalFormat formatter = new DecimalFormat();
        formatter.setMaximumFractionDigits(1);
        return formatter.format(number);
    }
}