package com.kielson.mixin;

import com.kielson.KielsonsEntityAttributes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static com.kielson.KielsonsAPI.MOD_ID;

@Mixin(CrossbowItem.class)
abstract class CrossbowItemMixin extends RangedWeaponItem {
    public CrossbowItemMixin(Settings settings) {
        super(settings);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/RangedWeaponItem;<init>(Lnet/minecraft/item/Item$Settings;)V"))
    private static Settings addCustomAttributes(Settings settings){
        return settings.attributeModifiers(createAttributeModifiers());
    }

    @Unique
    private static AttributeModifiersComponent createAttributeModifiers() {
        return AttributeModifiersComponent.builder()
                .add(KielsonsEntityAttributes.RANGED_DAMAGE, new EntityAttributeModifier(Identifier.of(MOD_ID, "crossbow"), 9.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
                .add(KielsonsEntityAttributes.PULL_TIME, new EntityAttributeModifier(Identifier.of(MOD_ID, "crossbow"), 1.25, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
                .build();
    }


    /*@Shadow @Final private static float DEFAULT_SPEED;
    private static final float STANDARD_VELOCITY = DEFAULT_SPEED;
    private static final float STANDARD_DAMAGE = 9;

    private RangedConfig config() {
        return ((CustomRangedWeapon) this).getRangedWeaponConfig();
    }

    public float getCustomPullProgress(int useTicks) {
        float pullTime = config().pull_time() > 0 ? config().pull_time() : 25;
        float f = (float)useTicks / pullTime;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Inject(method = "getPullTime", at = @At("HEAD"), cancellable = true)
    private static void applyCustomPullTime(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        var item = stack.getItem();
        if (item instanceof CustomRangedWeapon weapon) {
            var pullTime = weapon.getRangedWeaponConfig().pull_time();
            if (pullTime > 0) {
                cir.setReturnValue(pullTime);
                cir.cancel();
            }
        }
    }

    @Inject(method = "shoot", at = @At(value = "RETURN"))
    private void applyCustomVelocityAndDamage(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, LivingEntity target, CallbackInfo ci) {
        if (projectile instanceof PersistentProjectileEntity persistentProjectile) {
            if (config().velocity() > 0F) {
                // 3.0F is the default hardcoded velocity of bows
                persistentProjectile.setVelocity(projectile.getVelocity().multiply(config().velocity() / 3.0F));
            }
            var rangedDamage = shooter.getAttributeValue(KielsonsEntityAttributes.RANGED_DAMAGE);
            if (rangedDamage > 0) {
                var multiplier = ScalingUtil.arrowDamageMultiplier(STANDARD_DAMAGE, rangedDamage, STANDARD_VELOCITY, config().velocity());
                var finalDamage = persistentProjectile.getDamage() * multiplier;
                persistentProjectile.setDamage(finalDamage);
            }
        }
    }*/
}
