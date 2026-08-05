package com.kielson.mixin.ranged;

import com.kielson.KielsonsAPIEntityAttributes;
import com.kielson.item.CustomCrossbow;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static com.kielson.KielsonsAPI.MOD_ID;

@Mixin(CrossbowItem.class)
abstract class CrossbowItemMixin {
    @Unique private static final double PROJECTILE_DAMAGE = 9.0;
    @Unique private static final double PULL_TIME = 1.25;

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;<init>(Lnet/minecraft/world/item/Item$Properties;)V"))
    private static Item.Properties Kielson$addCustomAttributes(Item.Properties settings) {
        if (CustomCrossbow.IGNORE_MIXIN) {
            CustomCrossbow.IGNORE_MIXIN = false;
            return settings;
        }
        return settings.attributes(ItemAttributeModifiers.builder()
                .add(KielsonsAPIEntityAttributes.RANGED_DAMAGE, new AttributeModifier(Identifier.fromNamespaceAndPath(MOD_ID, "crossbow"), PROJECTILE_DAMAGE, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
                .add(KielsonsAPIEntityAttributes.PULL_TIME, new AttributeModifier(Identifier.fromNamespaceAndPath(MOD_ID, "crossbow"), PULL_TIME, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
                .build());
    }
}
