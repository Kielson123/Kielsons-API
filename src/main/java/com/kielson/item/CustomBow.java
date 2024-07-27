package com.kielson.item;

import net.minecraft.item.BowItem;

import java.util.HashSet;

public class CustomBow extends BowItem {

    public final static HashSet<CustomBow> instances = new HashSet<>();

    public CustomBow(Settings settings) {
        super(settings);
        instances.add(this);
    }


}
