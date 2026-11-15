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
    private KeyBinding listening;
    private boolean dragging;
    private int dragOffX, dragOffY;
    private ButtonWidget overlayBtn, invisBtn, invKeyBtn, openKeyBtn;

    public ConfigScreen() {
        super(Text.literal("Player Inventory Viewer"));
    }

    @Override
    protected void init() {
        int x = 10, y = 10, w = 190, h = 20, g = 24;

        overlayBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            Config.showOverlay = !Config.showOverlay;
            refresh();
        }).dimensions(x, y, w, h).build());
        y += g;

        addDrawableChild(new Slider(x, y, w, h, "Tamanho", 0.5, 3.0, 1, Config.scale, v -> Config.scale = v));
        y += g;
        addDrawableChild(new Slider(x, y, w, h, "Jogadores", 1, 8, 0, Config.maxPlayers, v -> Config.maxPlayers = (int) Math.round(v)));
        y += g;
        addDrawableChild(new Slider(x, y, w, h, "Alcance", 8, 128, 0, Config.range, v -> Config.range = (int) Math.round(v)));
        y += g;

        invisBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            Config.seeInvisible = !Config.seeInvisible;
            refresh();
        }).dimensions(x, y, w, h).build());
        y += g;

        invKeyBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            listening = Keys.INVIS;
            refresh();
        }).dimensions(x, y, w, h).build());
        y += g;

        openKeyBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            listening = Keys.OPEN;
            refresh();
        }).dimensions(x, y, w, h).build());
        y += g;

        addDrawableChild(ButtonWidget.builder(Text.literal("Resetar posição"), b -> {
            Config.posX = 0.02;
            Config.posY = 0.30;
        }).dimensions(x, y, w, h).build());
        y += g;

        addDrawableChild(ButtonWidget.builder(Text.literal("Pronto"), b -> close())
                .dimensions(x, y, w, h).build());
        refresh();
    }

    private void refresh() {
        overlayBtn.setMessage(Text.literal("Mostrar inventários: " + (Config.showOverlay ? "ON" : "OFF")));
        invisBtn.setMessage(Text.literal("Ver invisíveis: " + (Config.seeInvisible ? "ON" : "OFF")));
        invKeyBtn.setMessage(keyLabel("Tecla ver invisíveis", Keys.INVIS));
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
    public boolean mouseClicked(double mx, double my, int btn) {
        if (super.mouseClicked(mx, my, btn)) return true;
        float s = (float) Config.scale;
        int x = PanelRenderer.px(width, s), y = PanelRenderer.py(height, s);
        if (btn == 0 && mx >= x && mx <= x + PanelRenderer.W * s && my >= y && my <= y + PanelRenderer.H * s) {
            dragging = true;
            dragOffX = (int) mx - x;
            dragOffY = (int) my - y;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (dragging) {
            float s = (float) Config.scale;
            double fw = width - PanelRenderer.W * s, fh = height - PanelRenderer.H * s;
            if (fw > 0) Config.posX = MathHelper.clamp((mx - dragOffX) / fw, 0.0, 1.0);
            if (fh > 0) Config.posY = MathHelper.clamp((my - dragOffY) / fh, 0.0, 1.0);
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
        float s = (float) Config.scale;
        PanelRenderer.draw(ctx, textRenderer, PanelRenderer.px(width, s), PanelRenderer.py(height, s), s,
                "Nick do jogador (prévia)", ClientData.fallback(client.player));
        ctx.drawText(textRenderer, "Arraste o painel para mudar o lugar", 10, height - 14, 0xFFFFFF, true);
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
