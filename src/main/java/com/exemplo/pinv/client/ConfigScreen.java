package com.exemplo.pinv.client;

import com.exemplo.pinv.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class ConfigScreen extends Screen {
    private KeyBinding listening;
    private ButtonWidget invisBtn, invKeyBtn, appleBtn, appleKeyBtn, openKeyBtn;

    public ConfigScreen() {
        super(Text.literal("Invisible Viewer"));
    }

    @Override
    protected void init() {
        int x = 10, y = 10, w = 200, h = 20, g = 24;

        invisBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            Config.seeInvisible = !Config.seeInvisible;
            refresh();
        }).dimensions(x, y, w, h).build());
        y += g;

        addDrawableChild(new Slider(x, y, w, h, "Transparência", 0.15, 1.0, 2, Config.ghostAlpha, v -> Config.ghostAlpha = v));
        y += g;

        invKeyBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            listening = Keys.INVIS;
            refresh();
        }).dimensions(x, y, w, h).build());
        y += g;

        appleBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            Config.appleAlert = !Config.appleAlert;
            refresh();
        }).dimensions(x, y, w, h).build());
        y += g;

        appleKeyBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            listening = Keys.APPLE;
            refresh();
        }).dimensions(x, y, w, h).build());
        y += g;

        openKeyBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            listening = Keys.OPEN;
            refresh();
        }).dimensions(x, y, w, h).build());
        y += g;

        addDrawableChild(ButtonWidget.builder(Text.literal("Pronto"), b -> close())
                .dimensions(x, y, w, h).build());
        refresh();
    }

    private void refresh() {
        invisBtn.setMessage(Text.literal("Ver invisíveis: " + (Config.seeInvisible ? "ON" : "OFF")));
        invKeyBtn.setMessage(keyLabel("Tecla ver invisíveis", Keys.INVIS));
        appleBtn.setMessage(Text.literal("Alerta maçã dourada: " + (Config.appleAlert ? "ON" : "OFF")));
        appleKeyBtn.setMessage(keyLabel("Tecla alerta maçã", Keys.APPLE));
        openKeyBtn.setMessage(keyLabel("Tecla do menu", Keys.OPEN));
    }

    private Text keyLabel(String t, KeyBinding k) {
        return Text.literal(t + ": ").append(listening == k
                ? Text.literal("> aperte uma tecla <") : k.getBoundKeyLocalizedText());
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (listening != null) {
            listening.setBoundKey(key == GLFW.GLFW_KEY_ESCAPE
                    ? InputUtil.UNKNOWN_KEY : InputUtil.Type.KEYSYM.createFromCode(key));
            KeyBinding.updateKeysByCode();
            client.options.write();
            listening = null;
            refresh();
            return true;
        }
        if (Keys.OPEN.matchesKey(key, scan)) {
            close();
            return true;
        }
        return super.keyPressed(key, scan, mods);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void removed() {
        Config.save();
    }

    private static class Slider extends SliderWidget {
        private final String label;
        private final double min, max;
        private final int decimals;
        private final Consumer<Double> onChange;

        Slider(int x, int y, int w, int h, String label, double min, double max, int decimals,
               double cur, Consumer<Double> onChange) {
            super(x, y, w, h, Text.empty(), (cur - min) / (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            this.decimals = decimals;
            this.onChange = onChange;
            updateMessage();
        }

        private double val() {
            return min + value * (max - min);
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(label + ": " + String.format("%." + decimals + "f", val())));
        }

        @Override
        protected void applyValue() {
            onChange.accept(val());
        }
    }
}
