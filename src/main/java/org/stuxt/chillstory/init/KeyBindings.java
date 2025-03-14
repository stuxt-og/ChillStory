package org.stuxt.chillstory.init;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping DIALOG_KEY = new KeyMapping(
            "key.chillstory.dialog_key",
            GLFW.GLFW_KEY_LEFT_ALT,
            "category.chillstory.keys"
    );

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.DIALOG_KEY);
    }
}