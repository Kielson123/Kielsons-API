package com.kielson;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

import static com.kielson.KielsonsAPI.MOD_ID;

public class KielsonsAPIComponents {
    public static final ComponentType<Boolean> TWO_HANDED = register(Identifier.of(MOD_ID, "two_handed"), builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));

    public static <T> ComponentType<T> register(Identifier id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, ((ComponentType.Builder)builderOperator.apply(ComponentType.builder())).build());
    }

    public static void initialize() {}
}
