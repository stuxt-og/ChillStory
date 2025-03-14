package org.stuxt.chillstory.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.stuxt.chillstory.mechanics.overworld_timer.OverworldTimerGUI;

import java.util.function.Supplier;

public class OverworldTimerSyncPacket {
    private final int id;

    public OverworldTimerSyncPacket(int id) {
        this.id = id;
    }

    public OverworldTimerSyncPacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(id);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            OverworldTimerGUI.id = id;
        });

        ctx.get().setPacketHandled(true);
    }
}
