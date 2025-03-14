package org.stuxt.chillstory.mechanics.bossbarplayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.Entity;
import org.stuxt.chillstory.MainClass;

import java.util.Iterator;

@Mod.EventBusSubscriber(modid = MainClass.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BossBarUpdater {
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if(MainClass.bbsData_client.showData.isEmpty()) {
            return;
        }

        LocalPlayer player1 = Minecraft.getInstance().player;
        String player2Nick = null;
        float maxHealth = 0;
        float health = 0;

        Iterator<BBSData.Container> iterator = BossBarDataManager.showDataIterator();

        // 64*64
        AABB area = new AABB(player1.getX() - 4096, player1.getY() - 4096, player1.getZ() - 4096,
                player1.getX() + 4096, player1.getY() + 4096, player1.getZ() + 4096);

        for (Entity entity : player1.level().getEntities(player1, area)) {
            if (entity instanceof Player player) {
                while (iterator.hasNext()) {
                    BBSData.Container buf = iterator.next();

                    if (!buf.enabled) continue;

                    if (player1.level().dimensionType() == player.level().dimensionType()) {
                        player2Nick = buf.nick;
                        maxHealth = player.getMaxHealth();
                        health = player.getHealth();

                        break;
                    }
                }
            }

            if (player2Nick != null) {
                break;
            }
        }

        if (player2Nick == null) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int width = event.getWindow().getGuiScaledWidth();

        int etWidth = BossBarDataManager.getEdgesTextureWidth(player2Nick);

        int etHeight = BossBarDataManager.getEdgesTextureHeight(player2Nick);

        int hbtWidth = BossBarDataManager.getHealthBarTextureWidth(player2Nick);

        int hbtHeight = BossBarDataManager.getHealthBarTextureHeight(player2Nick);

        int x = width / 2 - etWidth / 2;

        guiGraphics.blit(new ResourceLocation(MainClass.MODID, BossBarDataManager.getEdgesTexturePath(player2Nick)),
                x, 0,
                0, 0,
                etWidth, etHeight,
                etWidth, etHeight
        );

        guiGraphics.blit(new ResourceLocation(MainClass.MODID, BossBarDataManager.getHealthBarTexturePath(player2Nick)),
                x + BossBarDataManager.getHealthBarTextureRX(player2Nick), BossBarDataManager.getHealthBarTextureY(player2Nick),
                0, 0,
                hbtWidth - (int) (hbtWidth * (1 - (float)health / maxHealth)), hbtHeight,
                hbtWidth, hbtHeight
        );
    }
}