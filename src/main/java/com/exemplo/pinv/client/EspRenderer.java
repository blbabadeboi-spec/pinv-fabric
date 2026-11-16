package com.exemplo.pinv.client;

import com.exemplo.pinv.Config;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.OptionalDouble;

/** Caixa colorida em volta dos outros players, visível através da parede. */
public final class EspRenderer {
    private EspRenderer() {}

    public static void register() {
        WorldRenderEvents.LAST.register(ctx -> {
            if (!Config.espEnabled) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world == null || mc.player == null) return;

            float r = ((Config.espColor >> 16) & 255) / 255f;
            float g = ((Config.espColor >> 8) & 255) / 255f;
            float b = (Config.espColor & 255) / 255f;

            Vec3d cam = ctx.camera().getPos();
            MatrixStack matrices = ctx.matrixStack();
            matrices.push();
            matrices.translate(-cam.x, -cam.y, -cam.z);
            Matrix4f mat = matrices.peek().getPositionMatrix();

            VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEffectVertexConsumers();
            VertexConsumer fill = immediate.getBuffer(PinvRenderLayers.fill());
            VertexConsumer lines = immediate.getBuffer(PinvRenderLayers.lines());

            float tick = ctx.tickDelta();
            for (PlayerEntity p : mc.world.getPlayers()) {
                if (p == mc.player || p.isSpectator()) continue;
                double x = MathHelper.lerp(tick, p.lastRenderX, p.getX());
                double y = MathHelper.lerp(tick, p.lastRenderY, p.getY());
                double z = MathHelper.lerp(tick, p.lastRenderZ, p.getZ());
                Box box = p.getBoundingBox().offset(x - p.getX(), y - p.getY(), z - p.getZ());
                fillBox(fill, mat, box, r, g, b, 0.22f);
                WorldRenderer.drawBox(matrices, lines, box, r, g, b, 1f);
            }

            immediate.draw();
            matrices.pop();
        });
    }

    private static void fillBox(VertexConsumer vc, Matrix4f m, Box box, float r, float g, float b, float a) {
        float x1 = (float) box.minX, y1 = (float) box.minY, z1 = (float) box.minZ;
        float x2 = (float) box.maxX, y2 = (float) box.maxY, z2 = (float) box.maxZ;
        quad(vc, m, r, g, b, a, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2);
        quad(vc, m, r, g, b, a, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1);
        quad(vc, m, r, g, b, a, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1);
        quad(vc, m, r, g, b, a, x2, y1, z2, x2, y2, z2, x1, y2, z2, x1, y1, z2);
        quad(vc, m, r, g, b, a, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1);
        quad(vc, m, r, g, b, a, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2);
    }

    private static void quad(VertexConsumer vc, Matrix4f m, float r, float g, float b, float a,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4) {
        vc.vertex(m, x1, y1, z1).color(r, g, b, a).next();
        vc.vertex(m, x2, y2, z2).color(r, g, b, a).next();
        vc.vertex(m, x3, y3, z3).color(r, g, b, a).next();
        vc.vertex(m, x4, y4, z4).color(r, g, b, a).next();
    }
}

/** RenderLayer.of e MultiPhaseParameters são protected; só uma subclasse acessa. */
final class PinvRenderLayers extends RenderLayer {
    private static RenderLayer fill;
    private static RenderLayer lines;

    private PinvRenderLayers() {
        super("pinv_unused", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, 256, false, false, () -> {}, () -> {});
    }

    static RenderLayer fill() {
        if (fill == null) {
            fill = of(
                    "pinv_esp_fill",
                    VertexFormats.POSITION_COLOR,
                    VertexFormat.DrawMode.QUADS,
                    256,
                    false,
                    true,
                    MultiPhaseParameters.builder()
                            .program(COLOR_PROGRAM)
                            .transparency(TRANSLUCENT_TRANSPARENCY)
                            .depthTest(ALWAYS_DEPTH_TEST)
                            .cull(DISABLE_CULLING)
                            .writeMaskState(COLOR_MASK)
                            .build(false));
        }
        return fill;
    }

    static RenderLayer lines() {
        if (lines == null) {
            lines = of(
                    "pinv_esp_lines",
                    VertexFormats.LINES,
                    VertexFormat.DrawMode.LINES,
                    256,
                    false,
                    false,
                    MultiPhaseParameters.builder()
                            .program(LINES_PROGRAM)
                            .lineWidth(new LineWidth(OptionalDouble.of(2.0)))
                            .layering(VIEW_OFFSET_Z_LAYERING)
                            .transparency(TRANSLUCENT_TRANSPARENCY)
                            .depthTest(ALWAYS_DEPTH_TEST)
                            .cull(DISABLE_CULLING)
                            .writeMaskState(COLOR_MASK)
                            .build(false));
        }
        return lines;
    }
}
