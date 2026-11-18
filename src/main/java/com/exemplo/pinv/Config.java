package com.exemplo.pinv;

import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Config {
    public static boolean seeInvisible = false;
    public static double ghostAlpha = 0.4;
    public static boolean appleAlert = true;
    public static double bellX = 0.5;
    public static double bellY = 0.5;
    public static double bellScale = 3.0;
    public static boolean pinkEnabled = true;
    public static int pinkSeconds = 10;
    public static boolean noJumpDelay = false;

    private static File file() {
        File dir = new File(Minecraft.getMinecraft().mcDataDir, "config");
        dir.mkdirs();
        return new File(dir, "pinv.properties");
    }

    private static double num(Properties p, String k, double def, double lo, double hi) {
        try {
            double v = Double.parseDouble(p.getProperty(k, String.valueOf(def)));
            return Math.max(lo, Math.min(hi, v));
        } catch (Exception e) {
            return def;
        }
    }

    public static void load() {
        try {
            File f = file();
            if (!f.exists()) return;
            Properties p = new Properties();
            FileInputStream in = new FileInputStream(f);
            p.load(in);
            in.close();
            seeInvisible = Boolean.parseBoolean(p.getProperty("seeInvisible", "false"));
            appleAlert = Boolean.parseBoolean(p.getProperty("appleAlert", "true"));
            pinkEnabled = Boolean.parseBoolean(p.getProperty("pinkEnabled", "true"));
            noJumpDelay = Boolean.parseBoolean(p.getProperty("noJumpDelay", "false"));
            ghostAlpha = num(p, "ghostAlpha", 0.4, 0.15, 1.0);
            bellX = num(p, "bellX", 0.5, 0, 1);
            bellY = num(p, "bellY", 0.5, 0, 1);
            bellScale = num(p, "bellScale", 3.0, 1.0, 8.0);
            pinkSeconds = (int) num(p, "pinkSeconds", 10, 3, 60);
        } catch (Exception ignored) {
        }
    }

    public static void save() {
        try {
            Properties p = new Properties();
            p.setProperty("seeInvisible", String.valueOf(seeInvisible));
            p.setProperty("appleAlert", String.valueOf(appleAlert));
            p.setProperty("pinkEnabled", String.valueOf(pinkEnabled));
            p.setProperty("noJumpDelay", String.valueOf(noJumpDelay));
            p.setProperty("ghostAlpha", String.valueOf(ghostAlpha));
            p.setProperty("bellX", String.valueOf(bellX));
            p.setProperty("bellY", String.valueOf(bellY));
            p.setProperty("bellScale", String.valueOf(bellScale));
            p.setProperty("pinkSeconds", String.valueOf(pinkSeconds));
            FileOutputStream out = new FileOutputStream(file());
            p.store(out, "Pinv Tools");
            out.close();
        } catch (Exception ignored) {
        }
    }
}
