package org.stuxt.chillstory.init;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.stuxt.chillstory.MainClass;
import org.stuxt.chillstory.entities.kain_npc.KainNPC;

@Mod.EventBusSubscriber(modid = MainClass.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.KAIN_NPC.get(), KainNPC.createAttributes().build());
    }
}
