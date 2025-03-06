package org.stuxt.chillstory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.NotImplementedException;

public class BossBar {
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        LocalPlayer player1 = Minecraft.getInstance().player;
        ServerPlayer player2 = null;
        String player2Nick = null;

        NickIterator iterator = BossBarDataManager.nickIterator();

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server != null) {
            while (iterator.hasNext()) {
                String buf = iterator.next();

                player2 = server.getPlayerList().getPlayerByName(buf);

                if (player2 != null && player1.level() == player2.level()) {
                    Vec3 player1Pos = player1.position();
                    Vec3 player2Pos = player2.position();

                    // 64*64
                    if (player1Pos.distanceToSqr(player2Pos) <= 4096) {
                        player2Nick = buf;
                        break;
                    }
                }
            }
        }

        if (player2Nick == null) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int width = event.getWindow().getGuiScaledWidth();

        int x = width / 2;
        int y = 0;

        int tWidth = BossBarDataManager.getTextureWidth(player2Nick);

        x = x > tWidth ? x - tWidth : tWidth - x;

        int tHeight = BossBarDataManager.getTextureHeight(player2Nick);

        guiGraphics.blit(new ResourceLocation(MainClass.MODID, BossBarDataManager.getTexturePath(player2Nick)),
                x, y,
                0, 0,
                tWidth, tHeight,
                tWidth, tHeight
        );
    }
}