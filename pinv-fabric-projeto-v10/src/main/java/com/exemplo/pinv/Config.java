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
    public static double bellX = 0.5;      // posicao do centro do sino (0..1 da largura)
    public static double bellY = 0.5;      // posicao do centro do sino (0..1 da altura)
    public static double bellScale = 3.0;  // tamanho do sino
    public static boolean pinkEnabled = true;
    public static int pinkSeconds = 10;

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("pinv.json");
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    public static void load() {
        try {
            Path f = file();
            if (!Files.exists(f)) return;
            JsonObject o = JsonParser.parseString(Files.readString(f)).getAsJsonObject();
            if (o.has("seeInvisible")) seeInvisible = o.get("seeInvisible").getAsBoolean();
            if (o.has("ghostAlpha")) ghostAlpha = clamp(o.get("ghostAlpha").getAsDouble(), 0.15, 1.0);
            if (o.has("appleAlert")) appleAlert = o.get("appleAlert").getAsBoolean();
            if (o.has("bellX")) bellX = clamp(o.get("bellX").getAsDouble(), 0, 1);
            if (o.has("bellY")) bellY = clamp(o.get("bellY").getAsDouble(), 0, 1);
            if (o.has("bellScale")) bellScale = clamp(o.get("bellScale").getAsDouble(), 1.0, 6.0);
            if (o.has("pinkEnabled")) pinkEnabled = o.get("pinkEnabled").getAsBoolean();
            if (o.has("pinkSeconds")) pinkSeconds = (int) clamp(o.get("pinkSeconds").getAsInt(), 3, 60);
        } catch (Exception ignored) {
        }
    }

    public static void save() {
        try {
            JsonObject o = new JsonObject();
            o.addProperty("seeInvisible", seeInvisible);
            o.addProperty("ghostAlpha", ghostAlpha);
            o.addProperty("appleAlert", appleAlert);
            o.addProperty("bellX", bellX);
            o.addProperty("bellY", bellY);
            o.addProperty("bellScale", bellScale);
            o.addProperty("pinkEnabled", pinkEnabled);
            o.addProperty("pinkSeconds", pinkSeconds);
            Files.writeString(file(), o.toString());
        } catch (Exception ignored) {
        }
    }
}
