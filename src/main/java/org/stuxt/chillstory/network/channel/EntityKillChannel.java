package org.stuxt.chillstory.network.channel;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.stuxt.chillstory.MainClass;
import org.stuxt.chillstory.network.packet.EntityKillPacket;
import org.stuxt.chillstory.network.packet.OverworldTimerSyncPacket;

public class EntityKillChannel {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MainClass.MODID, "ekc"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void configure(){
        INSTANCE.messageBuilder(EntityKillPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(EntityKillPacket::encode)
                .decoder(EntityKillPacket::decode)
                .consumerMainThread(EntityKillPacket::handle)
                .add();
    }
}