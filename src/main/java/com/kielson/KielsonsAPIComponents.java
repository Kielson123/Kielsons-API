package com.kielson;

import com.mojang.serialization.Codec;
import java.util.function.UnaryOperator;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

import static com.kielson.KielsonsAPI.MOD_ID;

public class KielsonsAPIComponents {
    public static final DataComponentType<Boolean> TWO_HANDED = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(MOD_ID, "two_handed"),
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );

    public static void initialize() {}
}
