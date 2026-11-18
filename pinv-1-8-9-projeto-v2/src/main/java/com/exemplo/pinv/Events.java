package com.exemplo.pinv;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class Events {
    private final Minecraft mc = Minecraft.getMinecraft();
    private EntityPlayer revived;
    private boolean jumpFailed;

    private void msg(String text) {
        if (mc.ingameGUI != null) mc.ingameGUI.setRecordPlaying(new ChatComponentText(text), false);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        AppleAlert.tick();
        RegenTracker.tick();

        // No Jump Delay: zera o tempo de espera entre pulos (campo jumpTicks do jogador)
        if (Config.noJumpDelay && mc.thePlayer != null && !jumpFailed) {
            try {
                ReflectionHelper.setPrivateValue(EntityLivingBase.class, mc.thePlayer, Integer.valueOf(0), "jumpTicks", "field_70773_bE");
            } catch (Exception ex) {
                jumpFailed = true;
                msg("No Jump Delay nao funcionou nesta versao");
            }
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent e) {
        while (Keys.OPEN.isPressed()) {
            if (mc.currentScreen == null) mc.displayGuiScreen(new ConfigGui());
        }
        while (Keys.INVIS.isPressed()) {
            Config.seeInvisible = !Config.seeInvisible;
            Config.save();
            msg("Ver invisiveis: " + (Config.seeInvisible ? "ON" : "OFF"));
        }
        while (Keys.APPLE.isPressed()) {
            Config.appleAlert = !Config.appleAlert;
            Config.save();
            msg("Alerta da maca: " + (Config.appleAlert ? "ON" : "OFF"));
        }
        while (Keys.JUMP.isPressed()) {
            Config.noJumpDelay = !Config.noJumpDelay;
            Config.save();
            msg("No Jump Delay: " + (Config.noJumpDelay ? "ON" : "OFF"));
        }
        while (Keys.PINK.isPressed()) {
            Config.pinkEnabled = !Config.pinkEnabled;
            Config.save();
            msg("Rosa (fim da regeneracao): " + (Config.pinkEnabled ? "ON" : "OFF"));
        }
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post e) {
        if (e.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!AppleAlert.isShowing() || mc.gameSettings.hideGUI) return;
        ScaledResolution sr = e.resolution;
        AppleAlert.drawBell(sr.getScaledWidth(), sr.getScaledHeight());
    }

    // Ver invisiveis (translucidos) e rosa: ajusta a cor/alpha antes de desenhar o player
    @SubscribeEvent
    public void onPre(RenderPlayerEvent.Pre e) {
        EntityPlayer p = e.entityPlayer;
        float a = 1F;

        if (p != mc.thePlayer && Config.seeInvisible && p.isInvisible()) {
            p.setInvisible(false);
            revived = p;
            a = (float) Config.ghostAlpha;
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
        }

        boolean pink = RegenTracker.isPink(p);
        if (pink || a < 1F) {
            GlStateManager.color(1F, pink ? 0.35F : 1F, pink ? 0.7F : 1F, a);
        }
    }

    @SubscribeEvent
    public void onPost(RenderPlayerEvent.Post e) {
        if (revived != null && revived == e.entityPlayer) {
            revived.setInvisible(true);
            revived = null;
            GlStateManager.disableBlend();
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
    }
}
