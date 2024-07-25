package com.kielson.mixin;

import com.kielson.KielsonsEntityAttributes;
import com.kielson.util.ScalingUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.Set;

import static com.kielson.KielsonsAPI.MOD_ID;

@Mixin(BowItem.class)
abstract class BowItemMixin extends RangedWeaponItem{
    @Unique private static final double PROJECTILE_DAMAGE = 6.0;
    @Unique private static final double PULL_TIME = 1.0;
    @Unique private static final double PROJECTILE_VELOCITY = 3.0;


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
                .add(KielsonsEntityAttributes.RANGED_DAMAGE, new EntityAttributeModifier(Identifier.of(MOD_ID, "bow"), PROJECTILE_DAMAGE, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
                .add(KielsonsEntityAttributes.PULL_TIME, new EntityAttributeModifier(Identifier.of(MOD_ID, "bow"), PULL_TIME, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
                .build();
    }

    @Unique
    public float getCustomPullProgress(int useTicks, LivingEntity user) {
        float pullTime = (float) user.getAttributeValue(KielsonsEntityAttributes.PULL_TIME) * 20.0f;
        float f = (float)useTicks / pullTime;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }
    @Unique private int ticks;
    @Unique private LivingEntity user;

    @WrapOperation(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"))
    private float applyCustomPullTime(int useTicks, Operation<Float> original, @Local(argsOnly = true) LivingEntity user) {
        this.ticks = useTicks;
        this.user = user;
        return getCustomPullProgress(useTicks, user);
    }

    @ModifyArg(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;shootAll(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Ljava/util/List;FFZLnet/minecraft/entity/LivingEntity;)V"), index = 5)
    private float applyCustomVelocity(float par6){
        return (float) (getCustomPullProgress(ticks, user) * PROJECTILE_VELOCITY);
    }

    @Inject(method = "shoot", at = @At(value = "RETURN"))
    private void applyCustomDamage(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, LivingEntity target, CallbackInfo ci) {
        if (projectile instanceof PersistentProjectileEntity persistentProjectile) {
            double damage = shooter.getAttributeValue(KielsonsEntityAttributes.RANGED_DAMAGE) / PROJECTILE_VELOCITY;
            ItemStack handStack = shooter.getStackInHand(shooter.getActiveHand());
            if (handStack.getItem() instanceof BowItem && handStack.hasEnchantments()){
                Set<RegistryEntry<Enchantment>> enchantments = handStack.getEnchantments().getEnchantments();
                int powerLevel = 0;
                for (int i = 0; i < enchantments.size(); i++){
                    Optional<RegistryEntry<Enchantment>> optionalEnchantmentEntry = enchantments.stream().findFirst();
                    if (optionalEnchantmentEntry.get().getKey().isPresent() && optionalEnchantmentEntry.get().getKey().get() == Enchantments.POWER && EnchantmentHelper.getLevel(optionalEnchantmentEntry.get(), handStack) > 0){
                        powerLevel = EnchantmentHelper.getLevel(optionalEnchantmentEntry.get(), handStack);
                        break;
                    }
                }
                damage += (int)((damage * 0.25) * (powerLevel + 1));
            }
            persistentProjectile.setDamage(damage);
            System.out.println(damage);
        }
    }
}
