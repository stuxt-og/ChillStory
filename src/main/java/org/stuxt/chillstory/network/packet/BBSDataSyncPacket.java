package org.stuxt.chillstory.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.stuxt.chillstory.MainClass;
import org.stuxt.chillstory.mechanics.bossbarplayer.BBSData;

import java.util.Iterator;
import java.util.function.Supplier;

public class BBSDataSyncPacket {
    private final BBSData.Container container;

    public BBSDataSyncPacket(BBSData.Container container) {
        this.container = container;
    }

    public static void encode(BBSDataSyncPacket message, FriendlyByteBuf buf) {
        message.container.encode(buf);
    }

    public static BBSDataSyncPacket decode(FriendlyByteBuf buf) {
        return new BBSDataSyncPacket(BBSData.Container.decode(buf));
    }

    public static void handle(BBSDataSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            MainClass.bbsData_client.showData.add(message.container);

            Iterator<BBSData.Container> iterator = MainClass.bbsData_client.showData.iterator();

            while (iterator.hasNext()) {
                BBSData.Container buf = iterator.next();

                if (buf.nick.equalsIgnoreCase(message.container.nick)) {
                    buf.enabled = message.container.enabled;
                    break;
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
