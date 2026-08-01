package com.kielson.client;

import com.kielson.util.TooltipHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.object.equipment.ShieldModel;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Optional;

import static com.kielson.KielsonsAPI.MOD_ID;
import static com.kielson.KielsonsAPI.isBetterCombatLoaded;
import static com.kielson.KielsonsAPIComponents.TWO_HANDED;

public class KielsonsAPIClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            TooltipHelper.updateTooltipText(stack, lines);
        });
    }
}
