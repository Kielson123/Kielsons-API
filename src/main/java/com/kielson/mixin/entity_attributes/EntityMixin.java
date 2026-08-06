package com.kielson.mixin.entity_attributes;

import com.kielson.KielsonsAPIAttributes;
import com.kielson.util.BooleanAttribute;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "getPercentFrozen", at = @At("RETURN"), cancellable = true)
    private void Kielson$scaleFrozenOverlay(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof LivingEntity living) {
            AttributeInstance resistanceInstance = living.getAttribute(KielsonsAPIAttributes.FREEZING_RESISTANCE);

            if (resistanceInstance != null) {
                float resistance = (float) resistanceInstance.getValue();
                cir.setReturnValue(cir.getReturnValue() * (1.0f - resistance));
            }
        }
    }

    @Shadow public abstract double getY();
    @Shadow public abstract Level level();

    @Inject(method = "checkBelowWorld", at = @At("HEAD"), cancellable = true)
    private void ringsAndThings$voidImmunityTeleport(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (this.getY() < this.level().getMinY() - 64) {

            if (entity instanceof Player player && !player.level().isClientSide()) {

                if (BooleanAttribute.isTrue(player, KielsonsAPIAttributes.VOID_IMMUNITY)) {
                    ServerLevel serverLevel = (ServerLevel) player.level();
                    BlockPos startPos = player.blockPosition();
                    BlockPos safePos = null;

                    int maxSearchRadius = 64;
                    searchLoop:
                    for (int r = 0; r <= maxSearchRadius; r++) {
                        for (int x = -r; x <= r; x++) {
                            for (int z = -r; z <= r; z++) {
                                if (Math.abs(x) == r || Math.abs(z) == r) {
                                    int checkX = startPos.getX() + x;
                                    int checkZ = startPos.getZ() + z;

                                    int y = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, checkX, checkZ);

                                    if (y > serverLevel.getMinY()) {
                                        safePos = new BlockPos(checkX, y, checkZ);
                                        break searchLoop;
                                    }
                                }
                            }
                        }
                    }

                    if (safePos == null) {
                        safePos = new BlockPos(startPos.getX(), 100, startPos.getZ());

                        serverLevel.setBlockAndUpdate(safePos.below(), Blocks.GLASS.defaultBlockState());
                    }

                    player.teleportTo(serverLevel, safePos.getX() + 0.5, safePos.getY() + 0.1, safePos.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot(), true);

                    player.setDeltaMovement(0, 0, 0);
                    player.fallDistance = 0.0f;

                    ci.cancel();
                }
            }
        }
    }
}
