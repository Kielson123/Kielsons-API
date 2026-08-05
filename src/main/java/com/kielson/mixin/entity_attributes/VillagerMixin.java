package com.kielson.mixin.entity_attributes;

import com.kielson.KielsonsAPIEntityAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
abstract public class VillagerMixin extends AbstractVillager {
    public VillagerMixin(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    @Inject(method = "updateSpecialPrices", at = @At("HEAD"))
    private void Kielson$addVillagerDiscountAttribute(Player player, CallbackInfo ci){
        if(player.getAttribute(KielsonsAPIEntityAttributes.TRADE_DISCOUNT_MULTIPLIER) == null) return;
        double discountValue = player.getAttributeValue(KielsonsAPIEntityAttributes.TRADE_DISCOUNT_MULTIPLIER);

        for(MerchantOffer offer : this.getOffers()){
            offer.addToSpecialPriceDiff((int) (offer.getBaseCostA().count() * discountValue) - offer.getBaseCostA().count());
        }
    }
}
