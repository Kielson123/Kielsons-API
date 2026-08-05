package com.kielson.mixin.entity_attributes;

import com.kielson.util.MobImmunityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin {
    @Shadow @Nullable public abstract LivingEntity getTarget();
    @Shadow public abstract void setTarget(@Nullable LivingEntity target);

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void Kielson$preventSetTarget(LivingEntity target, CallbackInfo ci) {
        if (target instanceof Player player) {
            Mob attacker = (Mob) (Object) this;
            if (MobImmunityHelper.shouldIgnorePlayer(attacker, player)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void Kielson$clearTargetMidFight(CallbackInfo ci) {
        Mob attacker = (Mob) (Object) this;
        LivingEntity target = this.getTarget();

        if (target instanceof Player player && MobImmunityHelper.shouldIgnorePlayer(attacker, player)) {
            this.setTarget(null);
        }
    }
}
