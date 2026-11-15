package com.exemplo.pinv;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class PinvMain implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(Net.REQUEST, (server, player, handler, buf, responseSender) -> {
            int n = Math.min(buf.readVarInt(), 20);
            UUID[] ids = new UUID[n];
            for (int i = 0; i < n; i++) ids[i] = buf.readUuid();

            server.execute(() -> {
                for (UUID id : ids) {
                    ServerPlayerEntity t = server.getPlayerManager().getPlayer(id);
                    if (t == null || t.getWorld() != player.getWorld() || t.distanceTo(player) > 128) continue;
                    PacketByteBuf out = PacketByteBufs.create();
                    out.writeUuid(id);
                    for (int i = 0; i < 41; i++) out.writeItemStack(t.getInventory().getStack(i));
                    ServerPlayNetworking.send(player, Net.CONTENT, out);
                }
            });
        });
    }
}
