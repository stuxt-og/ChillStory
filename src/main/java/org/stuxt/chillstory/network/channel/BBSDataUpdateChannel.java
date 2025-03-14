package org.stuxt.chillstory.network.channel;

import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraft.resources.ResourceLocation;
import org.stuxt.chillstory.MainClass;
import org.stuxt.chillstory.network.packet.BBSDataSyncPacket;

public class BBSDataUpdateChannel {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MainClass.MODID, "bbu"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void configure(){
        INSTANCE.messageBuilder(BBSDataSyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(BBSDataSyncPacket::encode)
                .decoder(BBSDataSyncPacket::decode)
                .consumerMainThread(BBSDataSyncPacket::handle)
                .add();
    }
}
