package com.kielson.mixin;

import com.kielson.KielsonsAPIEntityAttributes;
import com.kielson.item.CustomBow;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static com.kielson.KielsonsAPI.MOD_ID;

@Mixin(BowItem.class)
abstract class BowItemMixin extends ProjectileWeaponItem {
    @Unique private static final double PROJECTILE_DAMAGE = 6.0;
    @Unique private static final double PULL_TIME = 1.0;

    public BowItemMixin(Properties settings) {
        super(settings);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;<init>(Lnet/minecraft/world/item/Item$Properties;)V"))
    private static Properties KielsonsAPI$addCustomAttributes(Properties settings) {
        if (CustomBow.IGNORE_MIXIN) {
            CustomBow.IGNORE_MIXIN = false;
            return settings;
        }
        return settings.attributes(ItemAttributeModifiers.builder()
                .add(KielsonsAPIEntityAttributes.RANGED_DAMAGE, new AttributeModifier(Identifier.fromNamespaceAndPath(MOD_ID, "bow"), PROJECTILE_DAMAGE, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
                .add(KielsonsAPIEntityAttributes.PULL_TIME, new AttributeModifier(Identifier.fromNamespaceAndPath(MOD_ID, "bow"), PULL_TIME, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
                .build());
    }
}