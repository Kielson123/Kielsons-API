package com.kielson.item;

import net.minecraft.item.CrossbowItem;

import java.util.HashSet;

public class CustomCrossbow extends CrossbowItem {

    public final static HashSet<CustomCrossbow> instances = new HashSet<>();

    public CustomCrossbow(Settings settings) {
        super(settings);
        instances.add(this);
    }
}
