package com.kielson.util;

import com.kielson.KielsonsAPIEntityAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.player.Player;

public class MobImmunityHelper {

    public static boolean shouldIgnorePlayer(LivingEntity attacker, Player player) {
        if (player == null || attacker == null) return false;

        if (attacker instanceof Creeper && BooleanAttribute.isTrue(player, KielsonsAPIEntityAttributes.CREEPER_IMMUNITY)) return true;
        if (((attacker instanceof Guardian && !(attacker instanceof ElderGuardian)) || attacker instanceof Drowned)
                && BooleanAttribute.isTrue(player, KielsonsAPIEntityAttributes.WATER_MOB_IMMUNITY)) return true;
        if ((attacker instanceof AbstractIllager || attacker instanceof Ravager || attacker instanceof Vex)
                && BooleanAttribute.isTrue(player, KielsonsAPIEntityAttributes.ILLAGER_IMMUNITY)) return true;
        if ((attacker instanceof AbstractPiglin && !(attacker instanceof PiglinBrute))
                && BooleanAttribute.isTrue(player, KielsonsAPIEntityAttributes.PIGLIN_IMMUNITY)) return true;

        return false;
    }
}
