package com.knkevin.model_tools.packets;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Register and build the different types of packets.
 */
public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("model_tools", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        int index = -1;
        INSTANCE.messageBuilder(PlaceModelPacket.class, ++index, NetworkDirection.PLAY_TO_SERVER).encoder(PlaceModelPacket::encode).decoder(PlaceModelPacket::decode).consumerMainThread(PlaceModelPacket::handle).add();
        INSTANCE.messageBuilder(UndoModelPacket.class, ++index, NetworkDirection.PLAY_TO_SERVER).encoder(UndoModelPacket::encode).decoder(UndoModelPacket::decode).consumerMainThread(UndoModelPacket::handle).add();
    }
}
