package com.exemplo.pinv.client;

import com.exemplo.pinv.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class ConfigScreen extends Screen {
    private static final String[] TABS = {"Ver Invisíveis", "Alerta Maçã", "Rosa Regen", "Menu"};
    private static int tab = 0;

    private KeyBinding listening;
    private boolean dragging;
    private double dragOffX, dragOffY;
    private ButtonWidget invisBtn, invKeyBtn, appleBtn, appleKeyBtn, pinkBtn, pinkKeyBtn, openKeyBtn;

    public ConfigScreen() {
        super(Text.literal("Pinv Tools"));
    }

    @Override
    protected void init() {
        int w = 200, h = 20, g = 24;
        int x = 10;

        // abas no topo, uma por funcao
        int tx = 10;
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            ButtonWidget t = ButtonWidget.builder(Text.literal(TABS[i]), b -> {
                tab = idx;
                listening = null;
                clearAndInit();
            }).dimensions(tx, 8, 100, 20).build();
            t.active = (i != tab);
            addDrawableChild(t);
            tx += 104;
        }

        int y = 40;
        if (tab == 0) {
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
        } else if (tab == 1) {
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
            addDrawableChild(new Slider(x, y, w, h, "Tamanho do sino", 1.0, 6.0, 1, Config.bellScale, v -> Config.bellScale = v));
            y += g;
            addDrawableChild(ButtonWidget.builder(Text.literal("Resetar posição do sino"), b -> {
                Config.bellX = 0.5;
                Config.bellY = 0.5;
                Config.bellScale = 3.0;
                clearAndInit();
            }).dimensions(x, y, w, h).build());
        } else if (tab == 2) {
            pinkBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
                Config.pinkEnabled = !Config.pinkEnabled;
                refresh();
            }).dimensions(x, y, w, h).build());
            y += g;
            addDrawableChild(new Slider(x, y, w, h, "Tempo rosa (s)", 3, 60, 0, Config.pinkSeconds, v -> Config.pinkSeconds = (int) Math.round(v)));
            y += g;
            pinkKeyBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
                listening = Keys.PINK;
                refresh();
            }).dimensions(x, y, w, h).build());
        } else {
            openKeyBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
                listening = Keys.OPEN;
                refresh();
            }).dimensions(x, y, w, h).build());
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Pronto"), b -> close())
                .dimensions(10, height - 28, w, h).build());
        refresh();
    }

    private void refresh() {
        if (invisBtn != null) invisBtn.setMessage(Text.literal("Ver invisíveis: " + (Config.seeInvisible ? "ON" : "OFF")));
        if (invKeyBtn != null) invKeyBtn.setMessage(keyLabel("Tecla ver invisíveis", Keys.INVIS));
        if (appleBtn != null) appleBtn.setMessage(Text.literal("Alerta maçã dourada: " + (Config.appleAlert ? "ON" : "OFF")));
        if (appleKeyBtn != null) appleKeyBtn.setMessage(keyLabel("Tecla alerta maçã", Keys.APPLE));
        if (pinkBtn != null) pinkBtn.setMessage(Text.literal("Rosa ao acabar regeneração: " + (Config.pinkEnabled ? "ON" : "OFF")));
        if (pinkKeyBtn != null) pinkKeyBtn.setMessage(keyLabel("Tecla rosa", Keys.PINK));
        if (openKeyBtn != null) openKeyBtn.setMessage(keyLabel("Tecla do menu", Keys.OPEN));
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

    // arrastar o sino com o mouse (aba Alerta Maçã)
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (super.mouseClicked(mx, my, btn)) return true;
        if (tab == 1 && btn == 0) {
            double size = 16 * Config.bellScale;
            double cx = Config.bellX * width, cy = Config.bellY * height;
            if (Math.abs(mx - cx) <= size / 2 && Math.abs(my - cy) <= size / 2) {
                dragging = true;
                dragOffX = mx - cx;
                dragOffY = my - cy;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (dragging) {
            Config.bellX = MathHelper.clamp((mx - dragOffX) / width, 0.0, 1.0);
            Config.bellY = MathHelper.clamp((my - dragOffY) / height, 0.0, 1.0);
            return true;
        }
        return super.mouseDragged(mx, my, btn, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        dragging = false;
        return super.mouseReleased(mx, my, btn);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        if (tab == 1) {
            AppleAlert.drawBell(ctx, width, height);
            ctx.drawText(textRenderer, "Arraste o sino para mudar o lugar", 10, height - 42, 0xFFFFFF, true);
        }
        super.render(ctx, mx, my, delta);
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
