package com.kielson.mixin.entity_attributes;

import com.kielson.util.MobImmunityHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetingConditions.class)
public class TargetingConditionsMixin {

    @Inject(method = "test", at = @At("RETURN"), cancellable = true)
    private void Kielson$preventSeeingPlayerAsTarget(ServerLevel level, LivingEntity targeter, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && targeter != null && target instanceof Player player) {
            if (MobImmunityHelper.shouldIgnorePlayer(targeter, player)) {
                cir.setReturnValue(false);
            }
        }
    }
}