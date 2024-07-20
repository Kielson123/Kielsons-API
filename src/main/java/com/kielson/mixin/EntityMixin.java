package com.kielson.mixin;

import com.kielson.KielsonsEntityAttributes;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Entity.class)
abstract class EntityMixin {
    @Unique private final Entity entity = (Entity)(Object) this;

    @ModifyReturnValue(method = "getMaxAir", at = @At("RETURN"))
    public int KielsonsAPI$getMaxAir(int original) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return original;
        }
        if (livingEntity.getAttributes() == null) {
            return original;
        }
        EntityAttributeInstance lungCapacity = livingEntity.getAttributeInstance(KielsonsEntityAttributes.LUNG_CAPACITY);
        if (lungCapacity != null) {
            return MathHelper.clamp(original + (int) lungCapacity.getValue(), 1, Integer.MAX_VALUE);
        }
        return original;
    }
}
