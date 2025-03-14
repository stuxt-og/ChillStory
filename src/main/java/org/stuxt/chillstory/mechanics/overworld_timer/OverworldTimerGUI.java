package org.stuxt.chillstory.mechanics.overworld_timer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.stuxt.chillstory.MainClass;

@Mod.EventBusSubscriber(modid = MainClass.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OverworldTimerGUI {
    public static int id = 0;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        GuiGraphics guiGraphics = event.getGuiGraphics();

        if (id < 6) {
            guiGraphics.blit(new ResourceLocation(MainClass.MODID, "textures/gui/sanity.png"),
                    guiGraphics.guiWidth() - 42, guiGraphics.guiHeight() - 42,
                    id * 32, 0,
                    32, 32,
                    192, 96);
        } else if (id < 12) {
            guiGraphics.blit(new ResourceLocation(MainClass.MODID, "textures/gui/sanity.png"),
                    guiGraphics.guiWidth() - 42, guiGraphics.guiHeight() - 42,
                    (id - 6) * 32, 32,
                    32, 32,
                    192, 96);
        } else if (id > 12) {
            guiGraphics.blit(new ResourceLocation(MainClass.MODID, "textures/gui/sanity.png"),
                    guiGraphics.guiWidth() - 42, guiGraphics.guiHeight() - 42,
                    (id - 12) * 32, 64,
                    32, 32,
                    192, 96);
        }
    }
}
