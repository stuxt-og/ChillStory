package org.stuxt.chillstory.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.stuxt.chillstory.mechanics.EntityKillQueue;
import org.stuxt.chillstory.mechanics.bossbarplayer.BBSData;
import org.stuxt.chillstory.mechanics.overworld_timer.OverworldTimerGUI;

import java.util.function.Supplier;

public class EntityKillPacket {
    private int id;
    private String world;

    public EntityKillPacket(int id, String world) {
        this.id = id;
        this.world = world;
    }

    public static void encode(EntityKillPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.id);
        buf.writeUtf(packet.world);
    }

    public static EntityKillPacket decode(FriendlyByteBuf buf) {
        return new EntityKillPacket(buf.readInt(), buf.readUtf());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            EntityKillQueue.add(id, world);
        });

        ctx.get().setPacketHandled(true);
    }
}

