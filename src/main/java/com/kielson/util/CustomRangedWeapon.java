package com.kielson.util;

public interface CustomRangedWeapon {
    RangedConfig getRangedWeaponConfig();
    void setRangedWeaponConfig(RangedConfig config);
    default void configure(RangedConfig config) {
        setRangedWeaponConfig(config);
    }
}
