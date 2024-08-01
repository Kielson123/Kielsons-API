package com.kielson.mixin;

import com.kielson.KielsonsAPIComponents;
import com.kielson.KielsonsEntityAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity{
    @Unique private final PlayerEntity player = (PlayerEntity) (Object)this;
    @Shadow public float experienceProgress;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "createPlayerAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;", require = 1, allow = 1, at = @At("RETURN"))
    private static void KielsonsAPI$addPlayerAttributes(final CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        info.getReturnValue()
                .add(KielsonsEntityAttributes.EXPERIENCE)
                .add(KielsonsEntityAttributes.ITEM_PICK_UP_RANGE)
                .add(KielsonsEntityAttributes.RANGED_ACCURACY);
    }

    @Inject(method = "addExperience", at = @At(value = "HEAD"))
    private void KielsonsAPI$changeExperience(int experience, CallbackInfo ci) {
        experienceProgress = experienceProgress * (float) this.getAttributeValue(KielsonsEntityAttributes.EXPERIENCE);
    }

    /**
     * @author DaFuqs
     */
    @ModifyVariable(method = "tickMovement", at = @At("STORE"))
    private Box KielsonsAPI$adjustCollectionRange(Box original) {
        EntityAttributeInstance instance = player.getAttributeInstance(KielsonsEntityAttributes.ITEM_PICK_UP_RANGE);
        if (instance != null) {
            double value = instance.getValue();
            if (original.getLengthX() + value < 0) {
                Vec3d center = original.getCenter();
                return new Box(center.x, center.y, center.z, center.x, center.y, center.z);
            }
            return original.expand(value, value / 2, value);
        }
        return original;
    }

    @Inject(method = "getEquippedStack", at = @At("HEAD"), cancellable = true)
    private void Kielson$getEquippedStack(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        if (slot.equals(EquipmentSlot.MAINHAND)) {
            ItemStack offHandStack = player.getInventory().offHand.getFirst();
            Boolean offHandStackComponent = offHandStack.get(KielsonsAPIComponents.TWO_HANDED);

            if (Boolean.TRUE.equals(offHandStackComponent)) {
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
        else if (slot.equals(EquipmentSlot.OFFHAND)) {
            ItemStack mainHandStack = player.getInventory().getMainHandStack();
            Boolean mainHandStackComponent = mainHandStack.get(KielsonsAPIComponents.TWO_HANDED);

            if (Boolean.TRUE.equals(mainHandStackComponent)) {
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
    }
}
