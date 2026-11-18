package com.exemplo.pinv;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

/** Avisa quando a Regeneracao (maca dourada encantada) do proprio jogador chega a 3 segundos. */
public class AppleAlert {
    private static final int WARN_TICKS = 60;        // 3 segundos
    private static final int MIN_LONG_EFFECT = 400;  // maca encantada da 30s (600 ticks); a comum so 5s
    private static final int[] BEEPS = {0, 3, 10, 13, 20, 23};

    // sino desenhado em pixels (O = contorno, G = dourado, B = badalo)
    private static final String[] BELL = {
            "....OOO....",
            "...OGGGO...",
            "..OGGGGGO..",
            "..OGGGGGO..",
            "..OGGGGGO..",
            ".OGGGGGGGO.",
            ".OGGGGGGGO.",
            "OGGGGGGGGGO",
            "OOOOOOOOOOO",
            "....OBO....",
            "....OOO...."
    };

    private static int maxSeen;
    private static boolean showBell;
    private static int beepTick = -1;

    public static boolean isShowing() {
        return showBell;
    }

    public static void tick() {
        Minecraft mc = Minecraft.getMinecraft();

        if (beepTick >= 0) {
            for (int b : BEEPS) {
                if (b == beepTick) {
                    mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("note.pling"), 2.0F));
                }
            }
            beepTick++;
            if (beepTick > BEEPS[BEEPS.length - 1]) beepTick = -1;
        }

        if (mc.thePlayer == null || !Config.appleAlert) {
            maxSeen = 0;
            showBell = false;
            return;
        }

        PotionEffect e = mc.thePlayer.getActivePotionEffect(Potion.regeneration);
        if (e == null) {
            maxSeen = 0;
            showBell = false;
            return;
        }

        int d = e.getDuration();
        maxSeen = Math.max(maxSeen, d);
        boolean low = maxSeen >= MIN_LONG_EFFECT && d > 0 && d <= WARN_TICKS;
        if (low && !showBell) beepTick = 0;
        showBell = low;
    }

    /** Desenha o sino na posicao/tamanho configurados (HUD e previa do menu). */
    public static void drawBell(int sw, int sh) {
        float s = (float) Config.bellScale;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) (Config.bellX * sw) - 5.5F * s, (float) (Config.bellY * sh) - 5.5F * s, 0F);
        GlStateManager.scale(s, s, 1F);
        for (int y = 0; y < BELL.length; y++) {
            for (int x = 0; x < BELL[y].length(); x++) {
                char c = BELL[y].charAt(x);
                int col = c == 'O' ? 0xFF6B3F00 : c == 'G' ? 0xFFFFC400 : c == 'B' ? 0xFF3A2200 : 0;
                if (col != 0) Gui.drawRect(x, y, x + 1, y + 1, col);
            }
        }
        GlStateManager.popMatrix();
    }
}
