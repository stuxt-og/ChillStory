package org.stuxt.chillstory;

import com.mojang.logging.LogUtils;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import net.minecraftforge.api.distmarker.Dist;
import org.stuxt.chillstory.commands.RayCommand;
import org.stuxt.chillstory.entities.kain_npc.KainNPC;
import org.stuxt.chillstory.init.Config;
import org.stuxt.chillstory.init.KeyBindings;
import org.stuxt.chillstory.init.ModEntities;
import org.stuxt.chillstory.init.ModItems;
import org.stuxt.chillstory.mechanics.bossbarplayer.BBSData;
import org.stuxt.chillstory.network.channel.BBSDataUpdateChannel;
import org.stuxt.chillstory.mechanics.dialog_system.DialogManager;
import org.stuxt.chillstory.network.channel.EntityKillChannel;
import org.stuxt.chillstory.network.channel.OverworldTimerUpdateChannel;
import org.stuxt.chillstory.entities.kain_npc.KainNPCRenderer;
import org.stuxt.chillstory.network.channel.PDTpChannel;

@Mod(MainClass.MODID)
public class MainClass {
    public static final String MODID = "chillstory";

    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean isOnServer = false;

    public static BBSData bbsData_server = null;
    public static BBSData bbsData_client = null;

    public MainClass() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ServerEvents.class);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC, "chillstory.toml");

        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        //ModBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        //ModBlockEntities.BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEntities.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        RayCommand.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public class ServerEvents {
        // You can use SubscribeEvent and let the Event Bus discover methods to call
        @SubscribeEvent
        public static void onServerStarting(ServerStartingEvent event) {
            isOnServer = event.getServer().isDedicatedServer();

            bbsData_server = new BBSData();
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        BBSDataUpdateChannel.configure();
        OverworldTimerUpdateChannel.configure();
        EntityKillChannel.configure();
        PDTpChannel.configure();
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            bbsData_client = new BBSData();

            DialogManager.add("kain_npc.json", KainNPC::onEnd);
            DialogManager.add("iron_blocks.json", null);
        }

        @SubscribeEvent
        public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
            KeyBindings.register(event);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.KAIN_NPC.get(), KainNPCRenderer::new);
        }
    }
}
