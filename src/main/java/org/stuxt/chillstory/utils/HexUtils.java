package org.stuxt.chillstory.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexUtils {
    public static int parseHexColor(String hexColor) {
        if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1);
        }

        if (hexColor.length() != 6) {
            return 0xFFFFFF;
        }

        try {
            return Integer.parseInt(hexColor, 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    public static MutableComponent handleTags(String message) {
        Pattern colorTagPattern = Pattern.compile("<([^>]+)>(.*?)</\\1>", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = colorTagPattern.matcher(message);

        MutableComponent formattedMessage = Component.literal("");

        int lastIndex = 0;
        while (matcher.find()) {
            String textBeforeTag = message.substring(lastIndex, matcher.start());
            if (!textBeforeTag.isEmpty()) {
                formattedMessage = formattedMessage.append(Component.literal(textBeforeTag)
                        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF))));
            }

            String colorTag = matcher.group(1);
            String textPart = matcher.group(2);

            int color = HexUtils.parseHexColor(colorTag);

            formattedMessage = formattedMessage.append(Component.literal(textPart)
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color))));

            lastIndex = matcher.end();
        }

        String remainingText = message.substring(lastIndex);
        if (!remainingText.isEmpty()) {
            formattedMessage = formattedMessage.append(Component.literal(remainingText)
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF))));
        }

        return formattedMessage;
    }
}
