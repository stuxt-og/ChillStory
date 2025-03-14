package org.stuxt.chillstory.network.packet;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.yezon.theabyss.network.TheabyssModVariables;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class PDTpPacket {
    private String nick;
    private boolean to;

    public PDTpPacket(String nick, boolean to) {
        this.nick = nick;
        this.to = to;
    }

    public static void encode(PDTpPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.nick);
        buf.writeBoolean(packet.to);
    }

    public static PDTpPacket decode(FriendlyByteBuf buf) {
        return new PDTpPacket(buf.readUtf(), buf.readBoolean());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

            ServerPlayer player = server.getPlayerList().getPlayerByName(nick);

            ServerLevel level = player.serverLevel();

            ResourceKey<Level> pocket = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("theabyss:pocket_dimension"));

            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();

            if (to && player.level().dimension() != pocket) {
                double pocketX = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                        .orElse(new TheabyssModVariables.PlayerVariables())).PocketPlayerX;
                double pocketZ = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                        .orElse(new TheabyssModVariables.PlayerVariables())).PocketPlayerZ;

                level.getServer().getCommands().performPrefixedCommand(
                        new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, level, 4, "", Component.literal(""), level.getServer(), null),
                        "execute in theabyss:pocket_dimension run tp " + nick + " " + Math.round(pocketX) + " 82 " + Math.round(pocketZ)
                );
            } else if (!to && player.level().dimension() == pocket) {
                double saveX = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                        .orElse(new TheabyssModVariables.PlayerVariables())).SaveX;
                double saveY = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                        .orElse(new TheabyssModVariables.PlayerVariables())).SaveY;
                double saveZ = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                        .orElse(new TheabyssModVariables.PlayerVariables())).SaveZ;

                level.getServer().getCommands().performPrefixedCommand(
                        new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, level, 4, "", Component.literal(""), level.getServer(), null),
                        "execute in minecraft:overworld run tp " + nick + " " + Math.round(saveX) + " " + Math.round(saveY) + " " + Math.round(saveZ)
                );
            }
        });

        ctx.get().setPacketHandled(true);
    }
}

