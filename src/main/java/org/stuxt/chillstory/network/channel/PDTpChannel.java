package org.stuxt.chillstory.network.channel;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.stuxt.chillstory.MainClass;
import org.stuxt.chillstory.network.packet.EntityKillPacket;
import org.stuxt.chillstory.network.packet.PDTpPacket;

public class PDTpChannel {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MainClass.MODID, "pdtp"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void configure(){
        INSTANCE.messageBuilder(PDTpPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(PDTpPacket::encode)
                .decoder(PDTpPacket::decode)
                .consumerMainThread(PDTpPacket::handle)
                .add();
    }
}