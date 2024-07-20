package com.kielson.mixin.client;

import com.kielson.item.CustomRangedWeaponItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        if (itemStack.getItem() instanceof CustomRangedWeaponItem) {
            return true;
        }
        return original.call(itemStack, item);
    }

    @ModifyConstant(method = "getFovMultiplier", constant = @Constant(floatValue = 20.0F))
    private float KielsonsAPI$getFovMultiplier(float value) {
        ItemStack itemStack = abstractClientPlayerEntity.getActiveItem();
        if (itemStack.getItem() instanceof CustomRangedWeaponItem rangedWeaponItem) {
            // Override hardcoded pull time
            return rangedWeaponItem.getPullTime();
        } else {
            return value;
        }
    }
}