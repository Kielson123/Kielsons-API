package com.kielson.client;

import com.kielson.util.TooltipHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Optional;

import static com.kielson.KielsonsAPI.isBetterCombatLoaded;
import static com.kielson.KielsonsAPIComponents.TWO_HANDED;

public class KielsonsAPIClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            TooltipHelper.updateTooltipText(stack, lines);
            addTwoHandedTooltip(stack, lines);
        });
    }

    private void addTwoHandedTooltip(ItemStack stack, List<Component> lines){
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
}
