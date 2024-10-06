package com.kielson.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;

import java.util.Optional;

import static com.kielson.KielsonsAPIComponents.TWO_HANDED;

public class KielsonsAPIClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            TooltipHelper.updateTooltipText(stack, lines);
            if (Boolean.TRUE.equals(stack.get(TWO_HANDED))) {
                Optional<Integer> goodValue = Optional.empty();
                for (int i = 0; i < lines.size(); ++i) {
                    Text line = lines.get(i);
                    TextContent content = line.getContent();
                    if (content instanceof TranslatableTextContent translatable) {
                        if (translatable.getKey().startsWith("item.modifiers") || translatable.getKey().startsWith("potion.whenDrank")) {
                            goodValue = Optional.of(i);
                            break;
                        }
                    }
                }
                Text text = Text.empty().append(Text.translatable("item.modifiers.two_handed").formatted(Formatting.GRAY));
                if (goodValue.isEmpty()) lines.addLast(text);
                else lines.add(goodValue.get(), text);
            }
        });
    }
}
