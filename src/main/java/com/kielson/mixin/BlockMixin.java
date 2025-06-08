package com.kielson.mixin;

import com.kielson.KielsonsAPIEntityAttributes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
abstract class BlockMixin {
    @Unique private PlayerEntity breakingPlayer;

    @ModifyArg(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V"))
    private int KielsonsAPI$modifyExperience(int originalXP) {
        if(breakingPlayer == null) {
            return originalXP;
        }
        EntityAttributeInstance attributeInstance = breakingPlayer.getAttributeInstance(KielsonsAPIEntityAttributes.EXPERIENCE);
        if (attributeInstance == null) {
            return originalXP;
        }
        return(int) (originalXP * attributeInstance.getValue());
    }

    @Inject(method = "afterBreak", at = @At("HEAD"))
    public void KielsonsAPI$saveBreakingPlayer(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack, CallbackInfo callbackInfo) {
        breakingPlayer = player;
    }
}
