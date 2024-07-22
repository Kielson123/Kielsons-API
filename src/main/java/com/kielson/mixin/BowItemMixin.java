package com.kielson.mixin;

import com.kielson.KielsonsEntityAttributes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static com.kielson.KielsonsAPI.MOD_ID;

@Mixin(BowItem.class)
abstract class BowItemMixin extends RangedWeaponItem {

    BowItemMixin(Item.Settings settings) {
        super(settings);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/RangedWeaponItem;<init>(Lnet/minecraft/item/Item$Settings;)V"))
    private static Settings addCustomAttributes(Settings settings){
        return settings.attributeModifiers(createAttributeModifiers());
    }

    @Unique
    private static AttributeModifiersComponent createAttributeModifiers() {
        return AttributeModifiersComponent.builder()
                .add(KielsonsEntityAttributes.RANGED_DAMAGE, new EntityAttributeModifier(Identifier.of(MOD_ID, "bow"), 6.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
                .add(KielsonsEntityAttributes.DRAW_TIME, new EntityAttributeModifier(Identifier.of(MOD_ID, "bow"), 20.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
                .build();
    }

    /*private RangedConfig config() {
        return ((CustomRangedWeapon) this).getRangedWeaponConfig();
    }

    @Unique
    public float getCustomPullProgress(int useTicks) {
        float pullTime = config().pull_time() > 0 ? config().pull_time() : 20;
        float f = (float)useTicks / pullTime;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }
    @WrapOperation(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"))
    private float applyCustomPullTime(int ticks, Operation<Float> original) {
        if (config().pull_time() > 0) {
            return getCustomPullProgress(ticks);
        } else {
            return original.call(ticks);
        }
    }

    @Unique private static final float STANDARD_DAMAGE = 6;
    @Unique private static final float STANDARD_VELOCITY = 3.0F;

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
