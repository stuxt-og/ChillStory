package org.stuxt.chillstory.init;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.stuxt.chillstory.MainClass;
import org.stuxt.chillstory.mechanics.bossbarplayer.BBSData;
import org.stuxt.chillstory.network.packet.BBSDataSyncPacket;
import org.stuxt.chillstory.network.channel.BBSDataUpdateChannel;

import java.util.Iterator;

@Mod.EventBusSubscriber
public class CommandsRegisterer {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("player_bossbar")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("name", StringArgumentType.string())
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(context -> {
                                    if (context.getSource().getServer() == null) {
                                        return -1;
                                    }

                                    String name = StringArgumentType.getString(context, "name");
                                    boolean value = BoolArgumentType.getBool(context, "value");

                                    Iterator<BBSData.Container> iterator = MainClass.bbsData_server.showData.iterator();

                                    boolean found = false;

                                    while (iterator.hasNext()) {
                                        BBSData.Container buf = iterator.next();

                                        if (buf.nick.equalsIgnoreCase(name)) {
                                            buf.enabled = value;

                                            found = true;
                                        }
                                    }

                                    if (!found) {
                                        context.getSource().sendFailure(Component.literal("Не найден боссбар для этого игрока."));
                                        return -1;
                                    }

                                    BBSDataSyncPacket packet = new BBSDataSyncPacket(MainClass.bbsData_server.showData.get(MainClass.bbsData_server.showData.size() - 1));
                                    for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                                        BBSDataUpdateChannel.INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                                    }

                                    context.getSource().sendSuccess(() -> Component.literal("Успешно выполнено."), true);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }
}
