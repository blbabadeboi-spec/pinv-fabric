package com.exemplo.pinv;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class ConfigGui extends GuiScreen {
    private static final String[] TABS = {"Ver Invisíveis", "Alerta Maçã", "Rosa Regen", "No Jump Delay", "Menu"};
    private static int tab = 0;

    private KeyBinding listening;
    private boolean dragging;
    private double offX, offY;
    private GuiButton invisBtn, invKeyBtn, appleBtn, appleKeyBtn, pinkBtn, pinkKeyBtn, jumpBtn, jumpKeyBtn, openKeyBtn;

    private GuiButton add(GuiButton b) {
        buttonList.add(b);
        return b;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        invisBtn = invKeyBtn = appleBtn = appleKeyBtn = pinkBtn = pinkKeyBtn = jumpBtn = jumpKeyBtn = openKeyBtn = null;
        int w = 200, h = 20, g = 24, x = 10;

        int tx = 10;
        for (int i = 0; i < TABS.length; i++) {
            GuiButton t = new GuiButton(100 + i, tx, 8, 92, 20, TABS[i]);
            t.enabled = (i != tab);
            buttonList.add(t);
            tx += 94;
        }

        int y = 40;
        if (tab == 0) {
            invisBtn = add(new GuiButton(1, x, y, w, h, ""));
            y += g;
            add(new Slider(2, x, y, w, h, "Transparência", 0.15, 1.0, 2, Config.ghostAlpha, new Slider.Callback() {
                public void set(double v) { Config.ghostAlpha = v; }
            }));
            y += g;
            invKeyBtn = add(new GuiButton(3, x, y, w, h, ""));
        } else if (tab == 1) {
            appleBtn = add(new GuiButton(4, x, y, w, h, ""));
            y += g;
            appleKeyBtn = add(new GuiButton(5, x, y, w, h, ""));
            y += g;
            add(new Slider(6, x, y, w, h, "Tamanho do sino", 1.0, 8.0, 1, Config.bellScale, new Slider.Callback() {
                public void set(double v) { Config.bellScale = v; }
            }));
            y += g;
            add(new GuiButton(7, x, y, w, h, "Resetar posição do sino"));
        } else if (tab == 2) {
            pinkBtn = add(new GuiButton(8, x, y, w, h, ""));
            y += g;
            add(new Slider(9, x, y, w, h, "Tempo rosa (s)", 3, 60, 0, Config.pinkSeconds, new Slider.Callback() {
                public void set(double v) { Config.pinkSeconds = (int) Math.round(v); }
            }));
            y += g;
            pinkKeyBtn = add(new GuiButton(10, x, y, w, h, ""));
            y += g;
            add(new GuiButton(11, x, y, w, h, "Testar rosa (5 s, todos)"));
        } else if (tab == 3) {
            jumpBtn = add(new GuiButton(13, x, y, w, h, ""));
            y += g;
            jumpKeyBtn = add(new GuiButton(14, x, y, w, h, ""));
        } else {
            openKeyBtn = add(new GuiButton(12, x, y, w, h, ""));
        }

        add(new GuiButton(200, 10, height - 28, w, h, "Pronto"));
        refresh();
    }

    private void refresh() {
        if (invisBtn != null) invisBtn.displayString = "Ver invisíveis: " + (Config.seeInvisible ? "ON" : "OFF");
        if (invKeyBtn != null) invKeyBtn.displayString = keyLabel("Tecla ver invisíveis", Keys.INVIS);
        if (appleBtn != null) appleBtn.displayString = "Alerta maçã dourada: " + (Config.appleAlert ? "ON" : "OFF");
        if (appleKeyBtn != null) appleKeyBtn.displayString = keyLabel("Tecla alerta maçã", Keys.APPLE);
        if (pinkBtn != null) pinkBtn.displayString = "Rosa ao acabar regeneração: " + (Config.pinkEnabled ? "ON" : "OFF");
        if (pinkKeyBtn != null) pinkKeyBtn.displayString = keyLabel("Tecla rosa", Keys.PINK);
        if (jumpBtn != null) jumpBtn.displayString = "No Jump Delay: " + (Config.noJumpDelay ? "ON" : "OFF");
        if (jumpKeyBtn != null) jumpKeyBtn.displayString = keyLabel("Tecla No Jump Delay", Keys.JUMP);
        if (openKeyBtn != null) openKeyBtn.displayString = keyLabel("Tecla do menu", Keys.OPEN);
    }

    private String keyLabel(String t, KeyBinding k) {
        return t + ": " + (listening == k ? "> aperte uma tecla <" : GameSettings.getKeyDisplayString(k.getKeyCode()));
    }

    @Override
    protected void actionPerformed(GuiButton b) throws IOException {
        switch (b.id) {
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
                tab = b.id - 100;
                listening = null;
                initGui();
                return;
            case 1:
                Config.seeInvisible = !Config.seeInvisible;
                break;
            case 3:
                listening = Keys.INVIS;
                break;
            case 4:
                Config.appleAlert = !Config.appleAlert;
                break;
            case 5:
                listening = Keys.APPLE;
                break;
            case 7:
                Config.bellX = 0.5;
                Config.bellY = 0.5;
                Config.bellScale = 3.0;
                initGui();
                return;
            case 8:
                Config.pinkEnabled = !Config.pinkEnabled;
                break;
            case 10:
                listening = Keys.PINK;
                break;
            case 11:
                RegenTracker.testUntil = System.currentTimeMillis() + 5000L;
                break;
            case 13:
                Config.noJumpDelay = !Config.noJumpDelay;
                break;
            case 14:
                listening = Keys.JUMP;
                break;
            case 12:
                listening = Keys.OPEN;
                break;
            case 200:
                mc.displayGuiScreen(null);
                return;
            default:
                break;
        }
        refresh();
    }

    @Override
    protected void keyTyped(char c, int key) throws IOException {
        if (listening != null) {
            listening.setKeyCode(key == Keyboard.KEY_ESCAPE ? 0 : key);
            KeyBinding.resetKeyBindingArrayAndHash();
            mc.gameSettings.saveOptions();
            listening = null;
            refresh();
            return;
        }
        if (key == Keys.OPEN.getKeyCode()) {
            mc.displayGuiScreen(null);
            return;
        }
        super.keyTyped(c, key);
    }

    private boolean overButton(int mx, int my) {
        for (GuiButton b : buttonList) {
            if (mx >= b.xPosition && mx < b.xPosition + b.width && my >= b.yPosition && my < b.yPosition + b.height) {
                return true;
            }
        }
        return false;
    }

    // arrastar o sino com o mouse (aba Alerta Maçã)
    @Override
    protected void mouseClicked(int mx, int my, int btn) throws IOException {
        if (tab == 1 && btn == 0 && !overButton(mx, my)) {
            double size = 11 * Config.bellScale;
            double cx = Config.bellX * width, cy = Config.bellY * height;
            if (Math.abs(mx - cx) <= size / 2 && Math.abs(my - cy) <= size / 2) {
                dragging = true;
                offX = mx - cx;
                offY = my - cy;
                return;
            }
        }
        super.mouseClicked(mx, my, btn);
    }

    @Override
    protected void mouseClickMove(int mx, int my, int btn, long time) {
        if (dragging) {
            Config.bellX = Math.max(0.0, Math.min(1.0, (mx - offX) / width));
            Config.bellY = Math.max(0.0, Math.min(1.0, (my - offY) / height));
        } else {
            super.mouseClickMove(mx, my, btn, time);
        }
    }

    @Override
    protected void mouseReleased(int mx, int my, int state) {
        dragging = false;
        super.mouseReleased(mx, my, state);
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        if (tab == 1) {
            AppleAlert.drawBell(width, height);
            drawString(fontRendererObj, "Arraste o sino para mudar o lugar", 10, height - 42, 0xFFFFFF);
        }
        super.drawScreen(mx, my, pt);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        Config.save();
    }

    /** Slider simples feito so com a API do proprio Minecraft. */
    private static class Slider extends GuiButton {
        interface Callback {
            void set(double v);
        }

        private final String label;
        private final double min, max;
        private final int decimals;
        private final Callback cb;
        private double value;
        private boolean dragging;

        Slider(int id, int x, int y, int w, int h, String label, double min, double max, int decimals,
               double cur, Callback cb) {
            super(id, x, y, w, h, "");
            this.label = label;
            this.min = min;
            this.max = max;
            this.decimals = decimals;
            this.cb = cb;
            this.value = (cur - min) / (max - min);
            updateText();
        }

        private double val() {
            return min + value * (max - min);
        }

        private void updateText() {
            displayString = label + ": " + String.format("%." + decimals + "f", val());
        }

        private void setFromMouse(int mx) {
            value = Math.max(0.0, Math.min(1.0, (mx - (xPosition + 4)) / (double) (width - 8)));
            cb.set(val());
            updateText();
        }

        @Override
        protected int getHoverState(boolean mouseOver) {
            return 0;
        }

        @Override
        protected void mouseDragged(Minecraft mc, int mx, int my) {
            if (!visible) return;
            if (dragging) setFromMouse(mx);
            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1F, 1F, 1F, 1F);
            int px = xPosition + (int) (value * (width - 8));
            drawTexturedModalRect(px, yPosition, 0, 66, 4, 20);
            drawTexturedModalRect(px + 4, yPosition, 196, 66, 4, 20);
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mx, int my) {
            if (super.mousePressed(mc, mx, my)) {
                setFromMouse(mx);
                dragging = true;
                return true;
            }
            return false;
        }

        @Override
        public void mouseReleased(int mx, int my) {
            dragging = false;
        }
    }
}
