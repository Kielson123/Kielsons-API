package com.kielson.util;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class BooleanAttribute {
    public static final double FALSE = 0.0;
    public static final double TRUE = 1.0;

    public static boolean isTrue(LivingEntity entity, Holder<Attribute> attributeHolder) {
        if (entity == null) return false;
        AttributeInstance instance = entity.getAttribute(attributeHolder);
        return instance != null && instance.getValue() >= 1.0;
    }
}
