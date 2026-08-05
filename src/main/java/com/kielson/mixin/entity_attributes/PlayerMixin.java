package com.kielson.mixin.entity_attributes;

import com.kielson.KielsonsAPIEntityAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PlayerMixin {
    @Unique private final Player player = (Player) (Object)this;
    @Shadow public float experienceProgress;

    @Inject(method = "createAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;", require = 1, allow = 1, at = @At("RETURN"))
    private static void Kielson$addPlayerAttributes(final CallbackInfoReturnable<AttributeSupplier.Builder> info) {
        info.getReturnValue()
                .add(KielsonsAPIEntityAttributes.EXPERIENCE_MULTIPLIER)
                .add(KielsonsAPIEntityAttributes.ITEM_PICK_UP_RANGE)
                .add(KielsonsAPIEntityAttributes.TRADE_DISCOUNT_MULTIPLIER)

                .add(KielsonsAPIEntityAttributes.CREEPER_IMMUNITY)
                .add(KielsonsAPIEntityAttributes.VOID_IMMUNITY)
                .add(KielsonsAPIEntityAttributes.WATER_MOB_IMMUNITY)
                .add(KielsonsAPIEntityAttributes.ILLAGER_IMMUNITY)
                .add(KielsonsAPIEntityAttributes.PIGLIN_IMMUNITY);
    }

    @Inject(method = "giveExperiencePoints", at = @At(value = "HEAD"))
    private void Kielson$changeExperience(int i, CallbackInfo ci) {
        if(player.getAttribute(KielsonsAPIEntityAttributes.EXPERIENCE_MULTIPLIER) != null) {
            experienceProgress = experienceProgress * (float) player.getAttributeValue(KielsonsAPIEntityAttributes.EXPERIENCE_MULTIPLIER);
        }
    }

    /**
     * @author DaFuqs
     */
    @ModifyVariable(method = "aiStep", at = @At("STORE"), name = "pickupArea")
    private AABB Kielson$adjustCollectionRange(AABB pickupArea) {
        AttributeInstance instance = player.getAttribute(KielsonsAPIEntityAttributes.ITEM_PICK_UP_RANGE);
        if (instance != null) {
            double value = instance.getValue();
            if (pickupArea.getXsize() + value < 0) {
                Vec3 center = pickupArea.getCenter();
                return new AABB(center.x, center.y, center.z, center.x, center.y, center.z);
            }
            return pickupArea.inflate(value, value / 2, value);
        }
        return pickupArea;
    }
}
