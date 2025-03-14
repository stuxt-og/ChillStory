package org.stuxt.chillstory.mechanics;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.stuxt.chillstory.MainClass;
import org.stuxt.chillstory.init.Config;
import org.stuxt.chillstory.init.KeyBindings;
import org.stuxt.chillstory.mechanics.dialog_system.DialogManager;

@Mod.EventBusSubscriber(modid = MainClass.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyBindingsEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();

        Level level = minecraft.level;
        if (level == null) return;

        Player player = minecraft.player;
        if (player == null) return;

        if (KeyBindings.DIALOG_KEY.consumeClick()) {
            Entity ent = null;
            String name = null;
            boolean hasOwner = Config.IRON_BLOCKS_GIVEN.get() && DialogManager.getHasOwner("iron_blocks");

            if (!hasOwner) {
                AABB area = new AABB(player.getX() - 256, player.getY() - 256, player.getZ() - 256,
                        player.getX() + 256, player.getY() + 256, player.getZ() + 256);

                for (Entity entity : player.level().getEntities(player, area)) {
                    if (player.level().dimensionType() == entity.level().dimensionType()) {
                        name = EntityType.getKey(entity.getType()).toString();

                        if (DialogManager.isForDialogs(name)) {
                            ent = entity;
                        }
                    }

                    if (ent != null) {
                        break;
                    }
                }

                if (ent == null) {
                    return;
                }
            } else {
                name = "iron_blocks";
            }

            int id = DialogManager.getID(name);

            int size = DialogManager.getLinesSize(name);

            if (id > size) {
                return;
            } else {
                player.sendSystemMessage(DialogManager.get(name, id));

                if (id == size - 1) {
                    DialogManager.callOnEnd(name, ent);
                }
            }

            DialogManager.setID(name, id + 1);
        }
    }
}
