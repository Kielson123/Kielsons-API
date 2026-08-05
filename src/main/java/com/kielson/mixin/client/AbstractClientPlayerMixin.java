package com.kielson.mixin.client;

import com.kielson.KielsonsAPIEntityAttributes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayer.class)
abstract class AbstractClientPlayerMixin{
    @Unique private final AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)(Object) this;

    @Redirect(method = "getFieldOfViewModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private boolean Kielson$getFovMultiplier(ItemStack instance, Object o){
        return o instanceof ProjectileWeaponItem && !(abstractClientPlayer.isScoping());
    }

    @ModifyConstant(method = "getFieldOfViewModifier", constant = @Constant(floatValue = 20.0F))
    private float Kielson$getFovMultiplierForPullTime(float value) {
        if(abstractClientPlayer.getAttribute(KielsonsAPIEntityAttributes.PULL_TIME) != null) {
            return Math.clamp((float) (abstractClientPlayer.getAttributeValue(KielsonsAPIEntityAttributes.PULL_TIME) * 20.0), 0.01f, Float.MAX_VALUE);
        }
        return value;
    }
}