package org.stuxt.chillstory.mechanics.overworld_timer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.PacketDistributor;
import org.stuxt.chillstory.network.channel.OverworldTimerUpdateChannel;
import org.stuxt.chillstory.network.packet.OverworldTimerSyncPacket;

public class OverworldTimer extends SavedData {
    private long remainingTime;
    private String player;
    private boolean started;

    public OverworldTimer() {
        this.remainingTime = 18000;
        this.started = false;
    }

    public OverworldTimer(CompoundTag nbt) {
        this.remainingTime = nbt.getLong("remainingTime");
        this.player = nbt.getString("player");
        this.started = nbt.getBoolean("started");
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putLong("remainingTime", this.remainingTime);
        nbt.putString("player", this.player);
        nbt.putBoolean("started", this.started);

        return nbt;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long remainingTime, ServerPlayer player) {
        this.remainingTime = remainingTime;

        OverworldTimerUpdateChannel.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new OverworldTimerSyncPacket(17 - ((int)remainingTime) / 1000));
        this.setDirty();
    }

    public void setPlayer(String player) {
        this.player = player;
        this.setDirty();
    }

    public static OverworldTimer get(ServerLevel level, String player) {
        return level.getDataStorage().computeIfAbsent(
                nbt -> new OverworldTimer(nbt),
                OverworldTimer::new,
                "timer_data_" + player
        );
    }
}