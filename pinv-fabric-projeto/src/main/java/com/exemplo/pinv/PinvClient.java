package com.exemplo.pinv;

import com.exemplo.pinv.client.ClientData;
import com.exemplo.pinv.client.ConfigScreen;
import com.exemplo.pinv.client.HudOverlay;
import com.exemplo.pinv.client.Keys;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.text.Text;

public class PinvClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Config.load();
        Keys.register();

        HudRenderCallback.EVENT.register(HudOverlay::render);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (Keys.OPEN.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new ConfigScreen());
            }
            while (Keys.INVIS.wasPressed()) {
                Config.seeInvisible = !Config.seeInvisible;
                Config.save();
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("Ver invisíveis: " + (Config.seeInvisible ? "ON" : "OFF")), true);
                }
            }
        });
    }
}
