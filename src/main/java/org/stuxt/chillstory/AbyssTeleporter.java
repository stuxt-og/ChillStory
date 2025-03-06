package org.stuxt.chillstory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber
public class AbyssTeleporter {
    private static void tpToAbyss(ServerPlayer player) {
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("theabyss:the_abyss"));
        ServerLevel currentWorld = player.serverLevel();

        Optional<Pair<BlockPos, Holder<Biome>>> optional = Optional.ofNullable(currentWorld.findClosestBiome3d(
                holder -> holder.is(ResourceKey.create(Registries.BIOME, new ResourceLocation("theabyss:blaru_forest"))),
                BlockPos.ZERO,
                3000,
                4,
                32
        ));

        BlockPos pos;
        if (optional.isPresent()) {
            pos = optional.get().getFirst();
        } else {
            pos = new BlockPos(0, currentWorld.getSeaLevel(), 0);
        }

        MinecraftServer server = player.server;
        ServerLevel targetWorld = server.getLevel(dimension);
        if (targetWorld != null) {
            int x = pos.getX();
            int z = pos.getZ();
            int y = targetWorld.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

            if (y <= targetWorld.getMinBuildHeight()) {
                y = targetWorld.getSeaLevel();
            }

            while (y > targetWorld.getMinBuildHeight() && targetWorld.getBlockState(new BlockPos(x, y - 1, z)).isAir()) {
                y--;
            }

            while (!targetWorld.getBlockState(new BlockPos(x, y, z)).isAir() && y < targetWorld.getMaxBuildHeight()) {
                y++;
            }

            player.teleportTo(targetWorld, x + 0.5, y, z + 0.5, player.getYRot(), player.getXRot());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel world = player.serverLevel();
        BlockPos respawnPos = player.getRespawnPosition();

        if (respawnPos != null) {
            Optional<Vec3> spawnLocation = Player.findRespawnPositionAndUseSpawnBlock(world, respawnPos, 0.0f, player.isRespawnForced(), true);

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
    }
}
