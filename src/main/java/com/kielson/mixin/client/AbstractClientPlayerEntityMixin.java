package com.kielson.mixin.client;

import com.kielson.KielsonsAPIEntityAttributes;
import com.kielson.util.BowInterface;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(AbstractClientPlayer.class)
abstract class AbstractClientPlayerEntityMixin {
    @Unique private final AbstractClientPlayer abstractClientPlayerEntity = (AbstractClientPlayer)(Object) this;

    @Redirect(method = "getFieldOfViewModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean KielsonsAPI$getFovMultiplier(ItemStack instance, Item item){
        return item instanceof BowInterface && !(abstractClientPlayerEntity.isScoping());
    }

    @ModifyConstant(method = "getFieldOfViewModifier", constant = @Constant(floatValue = 20.0F))
    private float KielsonsAPI$getFovMultiplierForPullTime(float value) {
        return Math.clamp((float) (abstractClientPlayerEntity.getAttributeValue(KielsonsAPIEntityAttributes.PULL_TIME) * 20.0), 0.01f, Float.MAX_VALUE);
    }
}