package org.stuxt.chillstory.mechanics.dialog_system;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.stuxt.chillstory.MainClass;
import net.minecraft.server.packs.resources.Resource;
import org.stuxt.chillstory.utils.HexUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

class Dialog {
    public String entity;
    public ArrayList<Component> lines;
    public Consumer<Entity> onEnd;
    public int id = 0;
    boolean hasOwner = false;
}

@OnlyIn(Dist.CLIENT)
public class DialogManager {
    private static final ArrayList<Dialog> dialogs = new ArrayList<>();

    public static void add(String path, Consumer<Entity> onEnd) {
        ResourceLocation resourceLocation = new ResourceLocation(MainClass.MODID, "dialog/" + path);
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        Optional<Resource> buf = resourceManager.getResource(resourceLocation);

        if (buf.isEmpty()) { return; }

        Resource resource = buf.get();

        if (resource == null) return;

        try(InputStreamReader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            Dialog dialog = new Dialog();

            dialog.onEnd = onEnd;

            dialog.entity = jsonObject.get("owner").getAsString();

            JsonElement bufJ = jsonObject.get("hasOwner");

            if (bufJ == null) {
                dialog.hasOwner = true;
            } else {
                dialog.hasOwner = bufJ.getAsBoolean();
            }

            JsonArray arr = jsonObject.get("lines").getAsJsonArray();

            dialog.lines = new ArrayList<>();

            for (int i = 0; i < arr.size(); i++) {
                String cur = arr.get(i).getAsString();

                dialog.lines.add(HexUtils.handleTags(cur));
            }

            dialogs.add(dialog);

        } catch (IOException e) {
            return;
        }
    }

    public static boolean isForDialogs(String entity) {
        for (Dialog dialog : dialogs) {
            if (dialog.entity.equalsIgnoreCase(entity)) {
                return true;
            }
        }

        return false;
    }

    public static Component get(String entity, int id) {
        for (Dialog dialog : dialogs) {
            if (dialog.entity.equalsIgnoreCase(entity)) {
                if (id <= dialog.lines.size()) {
                    return dialog.lines.get(id);
                }
            }
        }

        return null;
    }

    public static boolean getHasOwner(String entity) {
        for (Dialog dialog : dialogs) {
            if (dialog.entity.equalsIgnoreCase(entity)) {
                return dialog.hasOwner;
            }
        }

        return false;
    }

    public static int getLinesSize(String entity) {
        for (Dialog dialog : dialogs) {
            if (dialog.entity.equalsIgnoreCase(entity)) {
                return dialog.lines.size();
            }
        }

        return -1;
    }

    public static void setID(String entity, int id) {
        for (Dialog dialog : dialogs) {
            if (dialog.entity.equalsIgnoreCase(entity)) {
                if (dialog.onEnd != null) {
                    dialog.id = id;
                }
            }
        }
    }

    public static int getID(String entity) {
        for (Dialog dialog : dialogs) {
            if (dialog.entity.equalsIgnoreCase(entity)) {
                if (dialog.onEnd != null) {
                    return dialog.id;
                }
            }
        }

        return -1;
    }

    public static void callOnEnd(String entity, Entity ent) {
        for (Dialog dialog : dialogs) {
            if (dialog.entity.equalsIgnoreCase(entity)) {
                if (dialog.onEnd != null) {
                    dialog.onEnd.accept(ent);
                }
            }
        }
    }
}
