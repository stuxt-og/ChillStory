package org.stuxt.chillstory.mechanics;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EntityEvents {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (EntityKillQueue.hasNext()) {
            EntityKillQueue.Container next = EntityKillQueue.next();

            event.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(next.getWorld()))).getEntity(next.getID()).kill();

            EntityKillQueue.remove(next);
        }
    }
}
