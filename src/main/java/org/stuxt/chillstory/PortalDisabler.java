package org.stuxt.chillstory;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = MainClass.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PortalDisabler {
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        if (state.getBlock() == ForgeRegistries.BLOCKS.getValue(new ResourceLocation("theabyss", "the_abyss_portal"))) {
            Block frameBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("theabyss", "unstable_obsidian"));

            for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                if (level.getBlockState(nearbyPos).getBlock() == frameBlock) {
                    event.setCanceled(true);
                    ServerPlayer player = (ServerPlayer)event.getEntity();

                    if (player != null) {
                        player.sendSystemMessage(Component.literal("Этот мир может иметь только один портал..."));
                    }

                    break;
                }
            }
        }
    }
}