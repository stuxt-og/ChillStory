package org.stuxt.chillstory.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.stuxt.chillstory.mechanics.PlayerEvents;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

class Timer {
    private int ticksRemaining = 0;

    public void start(int ticks) {
        this.ticksRemaining = ticks;
    }

    public void tick() {
        if (ticksRemaining > 0) {
            ticksRemaining--;
        }
    }

    public boolean isFinished() {
        return ticksRemaining <= 0;
    }
}

public class RiftItem extends Item {
    private Timer timer = new Timer();
    private int dimension = 0;

    public RiftItem(Properties properties, int dimension) {
        super(properties);
        this.dimension = dimension;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player == null) {
            return new InteractionResultHolder<>(InteractionResult.PASS, ItemStack.EMPTY);
        }

        if (level.isClientSide) {
            return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
        }

        MinecraftServer server = player.getServer();
        if (server == null) {
            return new InteractionResultHolder<>(InteractionResult.PASS, null);
        }

        ServerLevel theabyss = server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("theabyss:the_abyss")));
        ServerLevel second = null;

        if (dimension == 0) {
            second = server.getLevel(Level.OVERWORLD);
        } else if (dimension == 1) {
            second = server.getLevel(Level.NETHER);
        }

        if (level.dimensionType() != theabyss.dimensionType() && level.dimensionType() != second.dimensionType()) {
            player.sendSystemMessage(Component.literal("Вы не в междумирье."));
            return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
        }

        if (!timer.isFinished()) {
            player.sendSystemMessage(Component.literal("Пожалуйста подождите немного."));
            return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
        }

        if (level.dimension() == theabyss.dimension()) {
            int x = (int) player.getX();
            int z = (int) player.getZ();
            int y = second.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

            if (y <= second.getMinBuildHeight()) {
                y = second.getSeaLevel();
            }

            while (y > second.getMinBuildHeight() && second.getBlockState(new BlockPos(x, y - 1, z)).isAir()) {
                y--;
            }

            while (y < second.getMaxBuildHeight() && !second.getBlockState(new BlockPos(x, y, z)).isAir()) {
                y++;
            }

            BlockPos safePos = new BlockPos(x, y - 1, z);
            if (!PlayerEvents.isSafeBlock(second, safePos)) {
                y++;
            }

            player.teleportTo(second, x + 0.5, y, z + 0.5,
                    Set.of(RelativeMovement.X_ROT, RelativeMovement.Y_ROT),
                    player.getYRot(), player.getXRot());
        }
        else if (level.dimension() == second.dimension()) {
            int x = (int) player.getX();
            int z = (int) player.getZ();
            int y = theabyss.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

            if (y <= theabyss.getMinBuildHeight()) {
                y = theabyss.getSeaLevel();
            }

            while (y > theabyss.getMinBuildHeight() && theabyss.getBlockState(new BlockPos(x, y - 1, z)).isAir()) {
                y--;
            }

            while (y < theabyss.getMaxBuildHeight() && !theabyss.getBlockState(new BlockPos(x, y, z)).isAir()) {
                y++;
            }

            BlockPos safePos = new BlockPos(x, y - 1, z);
            if (!PlayerEvents.isSafeBlock(theabyss, safePos)) {
                y++;
            }

            player.teleportTo(theabyss, x + 0.5, y, z + 0.5,
                    Set.of(RelativeMovement.X_ROT, RelativeMovement.Y_ROT),
                    player.getYRot(), player.getXRot());
        }

        ItemStack itemStack = player.getItemInHand(hand);

        int currentDamage = itemStack.getDamageValue();

        itemStack.setDamageValue(currentDamage + 1);

        if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
            player.broadcastBreakEvent(hand);
            player.getInventory().removeItem(itemStack);
        }

        timer.start(20);

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
    }

    public void updateTimer() {
        timer.tick();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String text = "Мултивёрс";
        ChatFormatting[] colors = {
                ChatFormatting.RED,
                ChatFormatting.GOLD,
                ChatFormatting.YELLOW,
                ChatFormatting.GREEN,
                ChatFormatting.BLUE,
                ChatFormatting.DARK_PURPLE
        };

        MutableComponent coloredText = null;

        if (dimension == 0) {
            coloredText = Component.literal("Привязан к Оверворлду").withStyle(ChatFormatting.BLUE);
        } else if (dimension == 1) {
            coloredText = Component.literal("Привязан к Незеру").withStyle(ChatFormatting.RED);
        }

        tooltip.add(coloredText);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
