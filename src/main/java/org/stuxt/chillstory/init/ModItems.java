package org.stuxt.chillstory.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.item.Item;
import org.stuxt.chillstory.MainClass;
import org.stuxt.chillstory.items.InteruniverseWatchItem;
import org.stuxt.chillstory.items.RiftItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MainClass.MODID);

    public static final RegistryObject<Item> INTERUNIVERSE_WATCH = ITEMS.register("interuniverse_watch", () ->  new InteruniverseWatchItem(new Item.Properties().setNoRepair().defaultDurability(2)));
    public static final RegistryObject<Item> RIFT_OVERWORLD = ITEMS.register("rift_overworld", () -> new RiftItem(new Item.Properties().durability(2), 0));
    public static final RegistryObject<Item> RIFT_NETHER = ITEMS.register("rift_nether", () -> new RiftItem(new Item.Properties().durability(2), 1));
}
