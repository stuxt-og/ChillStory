package org.stuxt.chillstory.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.stuxt.chillstory.MainClass;
import org.stuxt.chillstory.entities.kain_npc.KainNPC;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MainClass.MODID);

    public static final RegistryObject<EntityType<KainNPC>> KAIN_NPC = ENTITIES.register("kain_npc",
            () -> EntityType.Builder.of(KainNPC::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F)
                    .build("kain_npc"));
}