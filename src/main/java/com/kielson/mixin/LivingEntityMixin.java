package com.kielson.mixin;

import com.kielson.KielsonsAPIComponents;
import com.kielson.events.KielsonsAPIEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.kielson.KielsonsAPI.isBetterCombatLoaded;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin{
    @Unique private final LivingEntity livingEntity = (LivingEntity) (Object) this;
    @Unique private int tickSeconds;
    @Unique private int tickMinutes;


    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true)
    private float KielsonsAPI$heal(float heal) {
        return KielsonsAPIEvents.ON_HEAL.invoker().onHeal(livingEntity, heal);
    }

    @Inject(method = "getOffhandItem", at = @At("HEAD"), cancellable = true)
    private void Kielson$getOffHandStack(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack mainHandStack = livingEntity.getItemBySlot(EquipmentSlot.MAINHAND);
        Boolean mainHandStackComponent = mainHandStack.get(KielsonsAPIComponents.TWO_HANDED);
        if (Boolean.TRUE.equals(mainHandStackComponent) && !isBetterCombatLoaded()) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "getMainHandItem", at = @At("HEAD"), cancellable = true)
    private void Kielson$getMainHandStack(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack offHandStack = livingEntity.getItemBySlot(EquipmentSlot.OFFHAND);
        Boolean offHandStackComponent = offHandStack.get(KielsonsAPIComponents.TWO_HANDED);
        if (Boolean.TRUE.equals(offHandStackComponent) && !isBetterCombatLoaded()) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void Kielson$tick(CallbackInfo info) {
        if(this.tickSeconds < 20){
            this.tickSeconds++;
        }
        else{
            KielsonsAPIEvents.EVERY_SECOND.invoker().everySecond(livingEntity);
            this.tickSeconds = 0;
        }


        if(this.tickMinutes < 1200){
            this.tickMinutes++;
        }
        else{
            KielsonsAPIEvents.EVERY_MINUTE.invoker().everyMinute(livingEntity);
            this.tickMinutes = 0;
        }
    }
}
