package com.kielson.item;

import net.minecraft.world.item.ShieldItem;

public class CustomShield extends ShieldItem {

    public CustomShield(ShieldMaterial shieldMaterial, Properties properties) {
        super(shieldMaterial.applyShieldProperties(properties));
    }
}
