package com.kielson.mixin.entity_attributes;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(targets = "net.minecraft.world.item.component.ItemAttributeModifiers$Display$Default")
public class ItemAttributeModifiersDefaultDisplayMixin {

    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void Kielson$formatBooleanAttributes(Consumer<Component> consumer, @Nullable Player player, Holder<Attribute> attribute, AttributeModifier modifier, CallbackInfo ci) {
        String descriptionId = attribute.value().getDescriptionId();

        if (descriptionId.startsWith("attribute.name.") && descriptionId.endsWith("_immunity")) {
            consumer.accept(Component.literal(" ").append(Component.translatable(descriptionId).withStyle(ChatFormatting.BLUE)));

            ci.cancel();
        }
    }
}