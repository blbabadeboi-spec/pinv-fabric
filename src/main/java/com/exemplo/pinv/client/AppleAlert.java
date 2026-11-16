package com.exemplo.pinv.client;

import com.exemplo.pinv.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;

/** Avisa quando a Regeneracao (da maca dourada encantada) do proprio jogador chega a 3 segundos. */
public class AppleAlert {
    private static final int WARN_TICKS = 60;          // 3 segundos
    private static final int MIN_LONG_EFFECT = 300;    // so efeitos longos (maca encantada = 20s); ignora a maca comum (5s)
    private static final int[] BEEPS = {0, 3, 10, 13, 20, 23}; // pi-pi  pi-pi  pi-pi

    private static int maxSeen;
    private static boolean showBell;
    private static int beepTick = -1;

    public static void tick(MinecraftClient mc) {
        if (beepTick >= 0) {
            for (int b : BEEPS) {
                if (b == beepTick) {
                    mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0F, 1.0F));
                }
            }
            beepTick++;
            if (beepTick > BEEPS[BEEPS.length - 1]) beepTick = -1;
        }

        if (mc.player == null || !Config.appleAlert) {
            maxSeen = 0;
            showBell = false;
            return;
        }

        StatusEffectInstance e = mc.player.getStatusEffect(StatusEffects.REGENERATION);
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

    /** Desenha o sino na posicao e tamanho configurados (usado no HUD e na previa do menu). */
    public static void drawBell(DrawContext ctx, int w, int h) {
        float s = (float) Config.bellScale;
        MatrixStack m = ctx.getMatrices();
        m.push();
        m.translate((float) (Config.bellX * w) - 8f * s, (float) (Config.bellY * h) - 8f * s, 0);
        m.scale(s, s, 1f);
        ctx.drawItem(new ItemStack(Items.BELL), 0, 0);
        m.pop();
    }

    public static void renderHud(DrawContext ctx, float tickDelta) {
        if (!showBell) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden) return;
        drawBell(ctx, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
    }
}
