package org.stuxt.chillstory.mechanics.bossbarplayer;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;

// BossBarShowData
public class BBSData{
    public static class Container {
        public final String nick;
        public boolean enabled;

        public Container(String nick, boolean enabled) {
            this.nick = nick;
            this.enabled = enabled;
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeUtf(nick);
            buf.writeBoolean(enabled);
        }

        public static Container decode(FriendlyByteBuf buf) {
            return new Container(buf.readUtf(), buf.readBoolean());
        }
    }

    public final ArrayList<Container> showData = new ArrayList<>();
}
