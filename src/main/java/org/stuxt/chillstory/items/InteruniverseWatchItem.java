package org.stuxt.chillstory.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.stuxt.chillstory.init.ModItems;
import org.stuxt.chillstory.MainClass;

import javax.annotation.Nullable;
import java.util.List;

class GetRiftTimer extends SavedData {
    private long remainingTime;

    public GetRiftTimer() {
        this.remainingTime = 0;
    }

    public GetRiftTimer(CompoundTag nbt) {
        this.remainingTime = nbt.getLong("remainingTime");
    }

    public void tick() {
        if (remainingTime > 0) {
            remainingTime--;
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putLong("remainingTime", this.remainingTime);

        return nbt;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long remainingTime, ServerPlayer player) {
        this.remainingTime = remainingTime;

        this.setDirty();
    }

    public void start(ServerPlayer player) {
        setRemainingTime(12000, player);
        this.setDirty();
    }

    public static GetRiftTimer get(ServerLevel level, String player) {
        return level.getDataStorage().computeIfAbsent(
                nbt -> new GetRiftTimer(nbt),
                GetRiftTimer::new,
                "timer_data_" + player
        );
    }
}

@Mod.EventBusSubscriber(modid = MainClass.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteruniverseWatchItem extends Item {
    private GetRiftTimer timer = new GetRiftTimer();

    public InteruniverseWatchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();

        Level level = context.getLevel();

        if (level.isClientSide) {
            return InteractionResult.PASS;
        }

        Block block = level.getBlockState(pos).getBlock();

        ServerPlayer player = (ServerPlayer)context.getPlayer();

        long remainingTime = timer.getRemainingTime();
        long minutes = remainingTime / 1200;
        long seconds = (remainingTime % 1200) / 20;

        if (block != null && block == ForgeRegistries.BLOCKS.getValue(new ResourceLocation("theabyss", "black_void"))) {
            if (remainingTime != 0) {
                player.sendSystemMessage(Component.literal("Пожалуйста подождите: " + minutes + ":" + seconds +"."));
                return InteractionResult.PASS;
            }

            Block type = level.getBlockState(pos.below()).getBlock();

            if(type != null) {
                if (context.getPlayer().isShiftKeyDown()) {
                    ItemStack item = ModItems.RIFT_NETHER.get().getDefaultInstance();

                    context.getPlayer().addItem(item);
                } else {
                    ItemStack item = ModItems.RIFT_OVERWORLD.get().getDefaultInstance();

                    context.getPlayer().addItem(item);
                }

                timer.start(player);
            }
        }

        return InteractionResult.SUCCESS;
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

        int[][] colorRGBs = new int[colors.length][3];
        for (int i = 0; i < colors.length; i++) {
            colorRGBs[i] = getRGBFromChatFormatting(colors[i]);
        }

        MutableComponent coloredText = Component.empty();
        for (int i = 0; i < text.length(); i++) {
            float ratio = (float) i / (text.length() - 1);

            int[] interpolatedColor = interpolateColorsCyclic(colorRGBs, ratio);

            coloredText.append(Component.literal(String.valueOf(text.charAt(i)))
                    .withStyle(Style.EMPTY.withColor(interpolatedColor[0] << 16 | interpolatedColor[1] << 8 | interpolatedColor[2])));
        }

        tooltip.add(coloredText);
        super.appendHoverText(stack, level, tooltip, flag);
    }

    private int[] interpolateColorsCyclic(int[][] colors, float ratio) {
        int segments = colors.length;
        float segmentSize = 1.0f / segments;
        int segment = (int) (ratio / segmentSize);
        float segmentRatio = (ratio - segment * segmentSize) / segmentSize;

        int[] startColor = colors[segment % segments];
        int[] endColor = colors[(segment + 1) % segments];

        int r = (int) (startColor[0] + (endColor[0] - startColor[0]) * segmentRatio);
        int g = (int) (startColor[1] + (endColor[1] - startColor[1]) * segmentRatio);
        int b = (int) (startColor[2] + (endColor[2] - startColor[2]) * segmentRatio);

        return new int[]{r, g, b};
    }

    private int[] getRGBFromChatFormatting(ChatFormatting color) {
        switch (color) {
            case RED: return new int[]{255, 0, 0};
            case GOLD: return new int[]{255, 170, 0};
            case YELLOW: return new int[]{255, 255, 0};
            case GREEN: return new int[]{0, 255, 0};
            case BLUE: return new int[]{0, 0, 255};
            case DARK_PURPLE: return new int[]{170, 0, 170};
            default: return new int[]{255, 255, 255};
        }
    }
}