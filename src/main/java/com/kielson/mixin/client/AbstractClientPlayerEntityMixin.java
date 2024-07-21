package com.kielson.mixin.client;

import com.kielson.item.CustomBow;
import com.kielson.util.CustomRangedWeapon;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
        if (item == Items.BOW) {
            if (CustomBow.instances.contains(itemStack.getItem())) {
                return true;
            }
        }
        return original.call(itemStack, item);
    }

    @ModifyConstant(method = "getFovMultiplier", constant = @Constant(floatValue = 20.0F))
    private float KielsonsAPI$getFovMultiplierForPullTime(float value) {
        var item = abstractClientPlayerEntity.getActiveItem().getItem();
        if (CustomBow.instances.contains(item)) {
            // Override hardcoded pull time
            return ((CustomRangedWeapon)item).getRangedWeaponConfig().pull_time();
        } else {
            return value;
        }
    }
}