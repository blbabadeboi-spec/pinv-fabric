package com.exemplo.pinv.client;

import com.exemplo.pinv.Config;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class PanelRenderer {
    public static final int W = 170, H = 112;

    public static int px(int screenW, float s) {
        return Math.round((float) (Config.posX * Math.max(0, screenW - W * s)));
    }

    public static int py(int screenH, float s) {
        return Math.round((float) (Config.posY * Math.max(0, screenH - H * s)));
    }

    private static int[] cell(int i) {
        if (i < 9) return new int[]{3, i};
        if (i < 36) return new int[]{(i - 9) / 9, (i - 9) % 9};
        if (i < 40) return new int[]{4, 39 - i};
        return new int[]{4, 8};
    }

    public static void draw(DrawContext ctx, TextRenderer font, int x, int y, float s, String name, ItemStack[] items) {
        MatrixStack m = ctx.getMatrices();
        m.push();
        m.translate(x, y, 0);
        m.scale(s, s, 1f);

        ctx.fill(0, 0, W, H, 0xCC101018);
        ctx.drawBorder(0, 0, W, H, 0xFF6A6AFF);
        ctx.drawText(font, name, 4, 3, 0xFFFFFF, true);

        for (int i = 0; i < 41; i++) {
            int[] rc = cell(i);
            int sx = 4 + rc[1] * 18, sy = 15 + rc[0] * 18;
            ctx.fill(sx, sy, sx + 18, sy + 18, 0x55FFFFFF);
            ctx.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xCC202020);
            ItemStack st = i < items.length ? items[i] : ItemStack.EMPTY;
            if (st != null && !st.isEmpty()) {
                ctx.drawItem(st, sx + 1, sy + 1);
                ctx.drawItemInSlot(font, st, sx + 1, sy + 1);
            }
        }
        m.pop();
    }
}
