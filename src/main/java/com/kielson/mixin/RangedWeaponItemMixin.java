package com.kielson.mixin;

import com.kielson.KielsonsEntityAttributes;
import com.kielson.util.CustomRangedWeapon;
import com.kielson.util.RangedConfig;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RangedWeaponItem.class)
abstract class RangedWeaponItemMixin extends Item implements CustomRangedWeapon {
    private AttributeModifiersComponent attributeModifiers = null;
    private RangedConfig rangedWeaponConfig = RangedConfig.EMPTY;

    RangedWeaponItemMixin(Settings settings) {
        super(settings);
    }

    public AttributeModifiersComponent getAttributeModifiers() {
        return this.attributeModifiers != null ? this.attributeModifiers : super.getAttributeModifiers();
    }

    public RangedConfig getRangedWeaponConfig() {
        return this.rangedWeaponConfig;
    }

    public void setRangedWeaponConfig(RangedConfig config) {
        this.rangedWeaponConfig = config;
        AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
        var damage = config.damage();
        if (damage > 0) {
            builder.add(KielsonsEntityAttributes.RANGED_DAMAGE, new EntityAttributeModifier(Identifier.of("ranged_damage_modifier") , damage, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND);
        }
        this.attributeModifiers = builder.build();
    }
}
