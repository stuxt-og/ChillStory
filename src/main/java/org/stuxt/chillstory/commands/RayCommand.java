package org.stuxt.chillstory.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.stuxt.chillstory.utils.HexUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RayCommand {
    // This method is called to register the command
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ray")
                .then(Commands.argument("nickname", StringArgumentType.string())
                        .then(Commands.argument("message", StringArgumentType.string())
                                .executes(context -> {
                                    String nickname = StringArgumentType.getString(context, "nickname");
                                    String message = StringArgumentType.getString(context, "message");

                                    Player sender = context.getSource().getPlayerOrException();

                                    ServerPlayer targetPlayer = sender.getServer().getPlayerList().getPlayerByName(nickname);

                                    if (targetPlayer != null) {
                                        targetPlayer.sendSystemMessage(HexUtils.handleTags("<#8B0000>[</#8B0000><#FF0000>неизвестный</#FF0000><#8B0000>]</#8B0000> " + message));

                                        sender.sendSystemMessage(Component.literal("Your message has been sent to " + nickname + "."));
                                    } else {
                                        sender.sendSystemMessage(Component.literal("Player not found!"));
                                    }

                                    return 1;
                                })
                        )
                )
        );
    }
}
