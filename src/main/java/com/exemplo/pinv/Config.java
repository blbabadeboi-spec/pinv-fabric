package com.exemplo.pinv;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    public static boolean showOverlay = true;
    public static boolean seeInvisible = false;
    public static double scale = 1.0;
    public static double posX = 0.02;
    public static double posY = 0.30;
    public static int maxPlayers = 3;
    public static int range = 32;
    public static double ghostAlpha = 0.4;

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("pinv.json");
    }

    public static void load() {
        try {
            Path f = file();
            if (!Files.exists(f)) return;
            JsonObject o = JsonParser.parseString(Files.readString(f)).getAsJsonObject();
            if (o.has("showOverlay")) showOverlay = o.get("showOverlay").getAsBoolean();
            if (o.has("seeInvisible")) seeInvisible = o.get("seeInvisible").getAsBoolean();
            if (o.has("scale")) scale = Math.max(0.5, Math.min(3.0, o.get("scale").getAsDouble()));
            if (o.has("posX")) posX = Math.max(0, Math.min(1, o.get("posX").getAsDouble()));
            if (o.has("posY")) posY = Math.max(0, Math.min(1, o.get("posY").getAsDouble()));
            if (o.has("maxPlayers")) maxPlayers = Math.max(1, Math.min(20, o.get("maxPlayers").getAsInt()));
            if (o.has("range")) range = Math.max(8, Math.min(128, o.get("range").getAsInt()));
            if (o.has("ghostAlpha")) ghostAlpha = Math.max(0.15, Math.min(1.0, o.get("ghostAlpha").getAsDouble()));
        } catch (Exception ignored) {
        }
    }

    public static void save() {
        try {
            JsonObject o = new JsonObject();
            o.addProperty("showOverlay", showOverlay);
            o.addProperty("seeInvisible", seeInvisible);
            o.addProperty("scale", scale);
            o.addProperty("posX", posX);
            o.addProperty("posY", posY);
            o.addProperty("maxPlayers", maxPlayers);
            o.addProperty("range", range);
            o.addProperty("ghostAlpha", ghostAlpha);
            Files.writeString(file(), o.toString());
        } catch (Exception ignored) {
        }
    }
}
