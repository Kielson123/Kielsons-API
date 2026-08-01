package com.kielson.util;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.kielson.client.ShieldSpecialRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.object.equipment.ShieldModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import static com.kielson.KielsonsAPI.MOD_ID;

public class ItemHelper {

    public static Optional<Integer> checkEnchantmentLevel(ItemStack itemStack, ResourceKey<Enchantment> enchantment){
        if (!itemStack.isEnchanted()) return Optional.empty();
        Set<Holder<Enchantment>> enchantments = itemStack.getEnchantments().keySet();
        int level = 0;
        for (int j = 0; j < enchantments.size(); j++){
            Optional<Holder<Enchantment>> optionalEnchantmentEntry = enchantments.stream().findFirst();
            if (optionalEnchantmentEntry.get().unwrapKey().isPresent() && optionalEnchantmentEntry.get().unwrapKey().get() == enchantment && EnchantmentHelper.getItemEnchantmentLevel(optionalEnchantmentEntry.get(), itemStack) > 0){
                level = EnchantmentHelper.getItemEnchantmentLevel(optionalEnchantmentEntry.get(), itemStack);
                break;
            }
        }
        return Optional.of(level);
    }

    public static Optional<Double> getAttributeValue(ItemStack itemStack, Holder<Attribute> entityAttribute){
        List<ItemAttributeModifiers.Entry> attributeModifiers = Objects.requireNonNull(itemStack.get(DataComponents.ATTRIBUTE_MODIFIERS)).modifiers();
        double attributeValue = 0.0;
        for (ItemAttributeModifiers.Entry modifier : attributeModifiers) {
            if (modifier.attribute() == entityAttribute) {
                AttributeModifier attributeModifier = modifier.modifier();
                attributeValue += attributeModifier.amount();
            }
        }
        return attributeValue > 0 ? Optional.of(attributeValue) : Optional.empty();
    }

    public static Item registerItem(String modId, String name, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(modId, name));
        Item item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static Item registerItem(String modId, String name, Item.Properties settings) {
        return registerItem(modId, name, Item::new, settings);
    }

    public static void registerDynamicShield(String id) {
        ModelLayerLocation modelLayer = new ModelLayerLocation(Identifier.fromNamespaceAndPath(MOD_ID, id), "main");
        ModelLayerRegistry.registerModelLayer(modelLayer, ShieldModel::createLayer);
    }

    public static void generateShield(ItemModelGenerators itemModelGenerator, String namespace, String id, ShieldItem item) {
        id = id.replace(namespace + ":", "");
        Identifier vanillaShieldModelLocation = ModelLocationUtils.getModelLocation(Items.SHIELD);
        var modelLocation = Identifier.fromNamespaceAndPath(namespace, "item/" + id);
        var modelLayer = Identifier.fromNamespaceAndPath(namespace, id);

        ModelTemplate shieldTemplate = new ModelTemplate(Optional.of(vanillaShieldModelLocation), Optional.empty(), TextureSlot.PARTICLE);
        shieldTemplate.create(modelLocation, TextureMapping.singleSlot(TextureSlot.PARTICLE, new Material(ModelLocationUtils.getModelLocation(item))), itemModelGenerator.modelOutput);

        ModelTemplate blockingShieldTemplate = new ModelTemplate(Optional.of(vanillaShieldModelLocation.withSuffix("_blocking")), Optional.empty(), TextureSlot.PARTICLE);
        blockingShieldTemplate.create(modelLocation.withSuffix("_blocking"), TextureMapping.singleSlot(TextureSlot.PARTICLE, new Material(ModelLocationUtils.getModelLocation(item))), itemModelGenerator.modelOutput);

        var model = new ShieldSpecialRenderer.Unbaked(modelLayer, Identifier.fromNamespaceAndPath(namespace,  id + "_base"), Identifier.fromNamespaceAndPath(namespace, id + "_base_nopattern"));
        ItemModel.Unbaked normal = ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation(item), model);
        ItemModel.Unbaked blocking = ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation(item, "_blocking"), model);
        itemModelGenerator.itemModelOutput.accept(item, ItemModelUtils.conditional(ShieldSpecialRenderer.DEFAULT_TRANSFORMATION, ItemModelUtils.isUsingItem(), blocking, normal));
    }
}
