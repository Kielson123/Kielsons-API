package com.kielson.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractClientPlayerEntity.class)
abstract class AbstractClientPlayerEntityMixin {
    @Unique private final AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)(Object) this;

    /*@WrapOperation(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
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
    }*/
}