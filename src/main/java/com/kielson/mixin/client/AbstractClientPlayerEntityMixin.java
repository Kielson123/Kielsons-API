package com.kielson.mixin.client;

import com.kielson.KielsonsAPIEntityAttributes;
import com.kielson.util.BowInterface;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(AbstractClientPlayerEntity.class)
abstract class AbstractClientPlayerEntityMixin {
    @Unique private final AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)(Object) this;

    @Redirect(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean KielsonsAPI$getFovMultiplier(ItemStack instance, Item item){
        return item instanceof BowInterface && !(abstractClientPlayerEntity.isUsingSpyglass());
    }

    @ModifyConstant(method = "getFovMultiplier", constant = @Constant(floatValue = 20.0F))
    private float KielsonsAPI$getFovMultiplierForPullTime(float value) {
        return Math.clamp((float) (abstractClientPlayerEntity.getAttributeValue(KielsonsAPIEntityAttributes.PULL_TIME) * 20.0), 0.01f, Float.MAX_VALUE);
    }
}