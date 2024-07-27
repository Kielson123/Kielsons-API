package com.kielson.mixin.client;

import com.kielson.KielsonsEntityAttributes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractClientPlayerEntity.class)
abstract class AbstractClientPlayerEntityMixin {
    @Unique private final AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)(Object) this;

    @WrapOperation(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean KielsonsAPI$getFovMultiplier(ItemStack itemStack, Item item, Operation<Boolean> original) {
        if (item instanceof BowItem) {
            return true;
        }
        return original.call(itemStack, item);
    }

    @ModifyConstant(method = "getFovMultiplier", constant = @Constant(floatValue = 20.0F))
    private float KielsonsAPI$getFovMultiplierForPullTime(float value) {
        var item = abstractClientPlayerEntity.getActiveItem().getItem();
        if (item instanceof RangedWeaponItem) {
            return (float) abstractClientPlayerEntity.getAttributeValue(KielsonsEntityAttributes.PULL_TIME) * 20.0f;
        } else {
            return value;
        }
    }
}