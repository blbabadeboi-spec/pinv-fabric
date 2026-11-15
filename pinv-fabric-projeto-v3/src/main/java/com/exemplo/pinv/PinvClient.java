package com.exemplo.pinv;

import com.exemplo.pinv.client.ClientData;
import com.exemplo.pinv.client.ConfigScreen;
import com.exemplo.pinv.client.HudOverlay;
import com.exemplo.pinv.client.Keys;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.List;
import java.util.UUID;

public class PinvClient implements ClientModInitializer {
    private static int tick;

    @Override
    public void onInitializeClient() {
        Config.load();
        Keys.register();

        HudRenderCallback.EVENT.register(HudOverlay::render);

        ClientPlayNetworking.registerGlobalReceiver(Net.CONTENT, (client, handler, buf, sender) -> {
            UUID id = buf.readUuid();
            ItemStack[] items = new ItemStack[41];
            for (int i = 0; i < 41; i++) items[i] = buf.readItemStack();
            client.execute(() -> ClientData.put(id, items));
        });

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

            // pede o inventario completo ao servidor 2x por segundo (so funciona se o servidor tiver o mod)
            if (client.player != null && client.world != null && ++tick % 10 == 0
                    && Config.showOverlay && ClientPlayNetworking.canSend(Net.REQUEST)) {
                List<AbstractClientPlayerEntity> list = ClientData.nearby(client);
                if (!list.isEmpty()) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeVarInt(list.size());
                    for (AbstractClientPlayerEntity p : list) buf.writeUuid(p.getUuid());
                    ClientPlayNetworking.send(Net.REQUEST, buf);
                }
            }
        });
    }
}
