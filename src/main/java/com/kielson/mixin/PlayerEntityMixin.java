package com.kielson.mixin;

import com.kielson.KielsonsAPIEntityAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
abstract class PlayerEntityMixin extends LivingEntity{
    @Unique private final Player player = (Player) (Object)this;
    @Shadow public float experienceProgress;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "createAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;", require = 1, allow = 1, at = @At("RETURN"))
    private static void KielsonsAPI$addPlayerAttributes(final CallbackInfoReturnable<AttributeSupplier.Builder> info) {
        info.getReturnValue()
                .add(KielsonsAPIEntityAttributes.EXPERIENCE)
                .add(KielsonsAPIEntityAttributes.ITEM_PICK_UP_RANGE);
    }

    @Inject(method = "giveExperiencePoints", at = @At(value = "HEAD"))
    private void KielsonsAPI$changeExperience(int experience, CallbackInfo ci) {
        experienceProgress = experienceProgress * (float) this.getAttributeValue(KielsonsAPIEntityAttributes.EXPERIENCE);
    }

    /**
     * @author DaFuqs
     */
    @ModifyVariable(method = "aiStep", at = @At("STORE"))
    private AABB KielsonsAPI$adjustCollectionRange(AABB original) {
        AttributeInstance instance = player.getAttribute(KielsonsAPIEntityAttributes.ITEM_PICK_UP_RANGE);
        if (instance != null) {
            double value = instance.getValue();
            if (original.getXsize() + value < 0) {
                Vec3 center = original.getCenter();
                return new AABB(center.x, center.y, center.z, center.x, center.y, center.z);
            }
            return original.inflate(value, value / 2, value);
        }
        return original;
    }
}
