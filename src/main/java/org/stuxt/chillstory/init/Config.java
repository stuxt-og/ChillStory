package org.stuxt.chillstory.init;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.stuxt.chillstory.MainClass;

@Mod.EventBusSubscriber(modid = MainClass.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec SERVER_SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> IRON_BLOCKS_GIVEN;

    static {
        BUILDER.push("lore");
        IRON_BLOCKS_GIVEN = BUILDER
                .define("iron_blocks_given", false);

        BUILDER.pop();
        SERVER_SPEC = BUILDER.build();
    }
}
