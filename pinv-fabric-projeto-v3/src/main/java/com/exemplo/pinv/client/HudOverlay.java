package com.exemplo.pinv.client;

import com.exemplo.pinv.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;

public class HudOverlay {
    public static void render(DrawContext ctx, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.options.hudHidden
                || !Config.showOverlay || mc.currentScreen instanceof ConfigScreen) return;

        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();
        float s = (float) Config.scale;
        int pw = Math.round(PanelRenderer.W * s), ph = Math.round(PanelRenderer.H * s);
        int startX = PanelRenderer.px(w, s), startY = PanelRenderer.py(h, s);
        int x = startX, y = startY;

        for (AbstractClientPlayerEntity p : ClientData.nearby(mc)) {
            if (y != startY && y + ph > h) {
                y = startY;
                x += pw + 4;
            }
            PanelRenderer.draw(ctx, mc.textRenderer, x, y, s, p.getGameProfile().getName(), ClientData.get(mc, p));
            y += ph + 4;
        }
    }
}
