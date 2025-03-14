package org.stuxt.chillstory.mechanics;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.yezon.theabyss.network.TheabyssModVariables;
import net.minecraft.world.item.Items;
import org.stuxt.chillstory.MainClass;
import org.stuxt.chillstory.init.Config;
import org.stuxt.chillstory.init.ModItems;
import org.stuxt.chillstory.items.InteruniverseWatchItem;
import org.stuxt.chillstory.items.RiftItem;
import org.stuxt.chillstory.mechanics.overworld_timer.OverworldTimer;
import org.stuxt.chillstory.network.channel.PDTpChannel;
import org.stuxt.chillstory.network.packet.PDTpPacket;

import java.util.Optional;
import java.util.Set;

@Mod.EventBusSubscriber(modid = MainClass.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            MinecraftServer server = event.getServer();
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);

            boolean ironGiven = Config.IRON_BLOCKS_GIVEN.get();

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.getScoreboardName().equalsIgnoreCase("roleplay09_")) {
                    player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).ifPresent((capability) -> {
                        capability.Mana = 100.0F;
                        capability.syncPlayerVariables(player);
                    });
                }

                Inventory inv = player.getInventory();

                ItemStack iron1 = null;
                ItemStack iron2 = null;
                ItemStack iron3 = null;
                ItemStack iron4 = null;
                ItemStack iron5 = null;

                for (int i = 0; i < 36; i++) {
                    ItemStack stack = inv.getItem(i);
                    if (!stack.isEmpty()) {
                        if (stack.getItem().getClass() == RiftItem.class) {
                            ((RiftItem) stack.getItem()).updateTimer();
                        }

                        ItemStack cur = stack;

                        Item curItem = cur.getItem();

                        if (!ironGiven && curItem == Items.IRON_INGOT && stack.getCount() == 64) {
                            if (iron1 == null) {
                                iron1 = cur;
                            } else if (iron2 == null) {
                                iron2 = cur;
                            } else if (iron3 == null) {
                                iron3 = cur;
                            } else if (iron4 == null) {
                                iron4 = cur;
                            } else if (iron5 == null) {
                                iron5 = cur;
                            }
                        }

                        if (curItem == ModItems.INTERUNIVERSE_WATCH.get()) {
                            ((InteruniverseWatchItem)curItem).updateTimer();
                        }
                    }

                    if (!ironGiven && iron1 != null && iron2 != null && iron3 != null && iron4 != null && iron5 != null) {
                        inv.removeItem(iron1);
                        inv.removeItem(iron3);
                        inv.removeItem(iron4);
                        inv.removeItem(iron5);

                        iron1 = Items.IRON_BLOCK.getDefaultInstance();
                        iron1.setCount(35);

                        inv.add(iron1);

                        iron2.setCount(5);

                        ironGiven = true;

                        Config.IRON_BLOCKS_GIVEN.set(true);
                    }
                }

                OverworldTimer timer = OverworldTimer.get(overworld, player.getScoreboardName());

                ResourceKey<Level> level = player.level().dimension();

                long left = timer.getRemainingTime();

                if (level == Level.OVERWORLD) {
                    if (left > 0) {
                        timer.setRemainingTime(--left, player);
                    } else if (left == 0) {
                            player.addEffect(new MobEffectInstance(
                                    MobEffects.BLINDNESS,
                                    40,
                                    1,
                                    true,
                                    true
                            ), null);
                            player.addEffect(new MobEffectInstance(
                                    MobEffects.POISON,
                                    40,
                                    5,
                                    true,
                                    true
                            ), null);

                        }
                    } else {
                    if (left < 17999) {
                        timer.setRemainingTime(++left, player);
                    }
                }

                if (player.level().dimension() == ResourceKey.create(Registries.DIMENSION, new ResourceLocation("theabyss:the_abyss"))) {
                    player.addEffect(new MobEffectInstance(
                            MobEffects.NIGHT_VISION,
                            100,
                            1,
                            true,
                            true
                    ), null);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChat(ServerChatEvent event) {
        String msg = event.getMessage().getString();
        ServerPlayer player = event.getPlayer();
        ServerLevel level = player.serverLevel();

        ResourceKey<Level> pocket = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("theabyss:pocket_dimension"));

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        String playerName = player.getName().getString();

        if (msg.equalsIgnoreCase("прунус") && player.level().dimension() != pocket) {
            double pocketX = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                    .orElse(new TheabyssModVariables.PlayerVariables())).PocketPlayerX;
            double pocketZ = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                    .orElse(new TheabyssModVariables.PlayerVariables())).PocketPlayerZ;

            level.getServer().getCommands().performPrefixedCommand(
                    new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, level, 4, "", Component.literal(""), level.getServer(), null),
                    "execute in theabyss:pocket_dimension run tp " + playerName + " " + Math.round(pocketX) + " 82 " + Math.round(pocketZ)
            );

        } else if (msg.equalsIgnoreCase("малус") && player.level().dimension() == pocket) {
            // Вихід у Overworld
            double saveX = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                    .orElse(new TheabyssModVariables.PlayerVariables())).SaveX;
            double saveY = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                    .orElse(new TheabyssModVariables.PlayerVariables())).SaveY;
            double saveZ = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                    .orElse(new TheabyssModVariables.PlayerVariables())).SaveZ;

            level.getServer().getCommands().performPrefixedCommand(
                    new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, level, 4, "", Component.literal(""), level.getServer(), null),
                    "execute in minecraft:overworld run tp " + playerName + " " + Math.round(saveX) + " " + Math.round(saveY) + " " + Math.round(saveZ)
            );
        }
    }

    public static boolean isSafeBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        return !(state.getBlock() == net.minecraft.world.level.block.Blocks.LAVA ||
                state.getBlock() == net.minecraft.world.level.block.Blocks.FIRE ||
                state.getBlock() == net.minecraft.world.level.block.Blocks.CACTUS ||
                state.getBlock() == Blocks.MAGMA_BLOCK ||
                state.getFluidState().isSource());
    }

//    @SubscribeEvent(priority = EventPriority.HIGHEST)
//    public static void onPlayerChat(ServerChatEvent event) {
//        String msg = event.getMessage().getString();
//        ServerPlayer player = event.getPlayer();
//        ServerLevel level = player.serverLevel();
//
//        ResourceKey<Level> pocket = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("theabyss:pocket_dimension"));
//
//        double x = player.getX();
//        double y = player.getY();
//        double z = player.getZ();
//        String playerName = player.getName().getString();
//
//        if (msg.equalsIgnoreCase("прунус") && player.level().dimension() != pocket) {
//            double pocketX = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
//                    .orElse(new TheabyssModVariables.PlayerVariables())).PocketPlayerX;
//            double pocketZ = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
//                    .orElse(new TheabyssModVariables.PlayerVariables())).PocketPlayerZ;
//
//            level.getServer().getCommands().performPrefixedCommand(
//                    new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, level, 4, "", Component.literal(""), level.getServer(), null),
//                    "execute in theabyss:pocket_dimension run tp " + playerName + " " + Math.round(pocketX) + " 82 " + Math.round(pocketZ)
//            );
//
//        } else if (msg.equalsIgnoreCase("малус") && player.level().dimension() == pocket) {
//            // Вихід у Overworld
//            double saveX = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
//                    .orElse(new TheabyssModVariables.PlayerVariables())).SaveX;
//            double saveY = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
//                    .orElse(new TheabyssModVariables.PlayerVariables())).SaveY;
//            double saveZ = ((TheabyssModVariables.PlayerVariables) player.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null)
//                    .orElse(new TheabyssModVariables.PlayerVariables())).SaveZ;
//
//            level.getServer().getCommands().performPrefixedCommand(
//                    new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, level, 4, "", Component.literal(""), level.getServer(), null),
//                    "execute in minecraft:overworld run tp " + playerName + " " + Math.round(saveX) + " " + Math.round(saveY) + " " + Math.round(saveZ)
//            );
//        }
//    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerLevel overlevel = player.getServer().getLevel(Level.OVERWORLD);
            OverworldTimer timer = OverworldTimer.get(overlevel, player.getScoreboardName());

//            if (event.getTo().location().toString().equals("theabyss:the_abyss")) {
//                player.displayClientMessage(Component.literal("Междумирье"), true);
//                player.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("Одновременно везде и нигде...")));
//            }
        }
    }

    private static void tpToAbyss(ServerPlayer player) {
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("theabyss:the_abyss"));
        ServerLevel currentlevel = player.serverLevel();

        BlockPos pos = new BlockPos(0, currentlevel.getSeaLevel(), 0);

        MinecraftServer server = player.server;
        ServerLevel targetlevel = server.getLevel(dimension);

        if (targetlevel != null) {
            int x = (int)pos.getX();
            int z = (int)pos.getZ();
            int y = targetlevel.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

            if (y <= targetlevel.getMinBuildHeight()) {
                y = targetlevel.getSeaLevel();
            }

            while (y > targetlevel.getMinBuildHeight() && targetlevel.getBlockState(new BlockPos(x, y - 1, z)).isAir()) {
                y--;
            }

            while (y < targetlevel.getMaxBuildHeight() && !targetlevel.getBlockState(new BlockPos(x, y, z)).isAir()) {
                y++;
            }

            BlockPos safePos = new BlockPos(x, y - 1, z);
            if (!PlayerEvents.isSafeBlock(targetlevel, safePos)) {
                y++;
            }

            player.teleportTo(targetlevel, x + 0.5, y, z + 0.5,
                    Set.of(RelativeMovement.X_ROT, RelativeMovement.Y_ROT),
                    player.getYRot(), player.getXRot());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel level = player.serverLevel();
        BlockPos respawnPos = player.getRespawnPosition();

        if (respawnPos != null) {
            Optional<Vec3> spawnLocation = Player.findRespawnPositionAndUseSpawnBlock(level, respawnPos, 0.0f, player.isRespawnForced(), true);

            if (spawnLocation.isPresent()) {
                return;
            }
        }

        tpToAbyss((ServerPlayer)event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        CompoundTag data = player.getPersistentData();

        if (!data.contains("the_abyss_first_join")) {
            data.putBoolean("the_abyss_first_join", true);

            tpToAbyss(player);
        }

        if (!data.contains("kit_start")) {
            data.putBoolean("kit_start", true);

            player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("theabyss:aberythe_sword")), 1));
            player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("theabyss:aberythe_armor_helmet")), 1));
            player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("theabyss:aberythe_armor_chestplate")), 1));
            player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("theabyss:aberythe_armor_leggings")), 1));
            player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("theabyss:aberythe_armor_boots")), 1));
            player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("theabyss:aberythe_pickaxe")), 1));
            player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("theabyss:cooked_deer_beef")), 128));
            player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("theabyss:totem_of_abyss")), 1));

            if (player.getName().getString().equalsIgnoreCase("roleplay09_")) {
                player.addItem(new ItemStack(ModItems.INTERUNIVERSE_WATCH.get(), 1));
            }
        }
    }
}
