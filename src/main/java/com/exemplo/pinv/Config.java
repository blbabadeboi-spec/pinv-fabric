package com.exemplo.pinv;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    public static boolean seeInvisible = false;
    public static double ghostAlpha = 0.4;
    public static boolean appleAlert = true;

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("pinv.json");
    }

    public static void load() {
        try {
            Path f = file();
            if (!Files.exists(f)) return;
            JsonObject o = JsonParser.parseString(Files.readString(f)).getAsJsonObject();
            if (o.has("seeInvisible")) seeInvisible = o.get("seeInvisible").getAsBoolean();
            if (o.has("ghostAlpha")) ghostAlpha = Math.max(0.15, Math.min(1.0, o.get("ghostAlpha").getAsDouble()));
            if (o.has("appleAlert")) appleAlert = o.get("appleAlert").getAsBoolean();
        } catch (Exception ignored) {
        }
    }

    public static void save() {
        try {
            JsonObject o = new JsonObject();
            o.addProperty("seeInvisible", seeInvisible);
            o.addProperty("ghostAlpha", ghostAlpha);
            o.addProperty("appleAlert", appleAlert);
            Files.writeString(file(), o.toString());
        } catch (Exception ignored) {
        }
    }
}
