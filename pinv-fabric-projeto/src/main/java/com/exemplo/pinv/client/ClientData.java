package com.exemplo.pinv.client;

import com.exemplo.pinv.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Comparator;
import java.util.List;

public class ClientData {
    /** Em singleplayer/LAN le o inventario completo do servidor integrado; em servidor remoto so o equipamento visivel. */
    public static ItemStack[] get(MinecraftClient mc, PlayerEntity p) {
        PlayerEntity src = p;
        IntegratedServer srv = mc.getServer();
        if (srv != null) {
            ServerPlayerEntity sp = srv.getPlayerManager().getPlayer(p.getUuid());
            if (sp != null) src = sp;
        }
        return fallback(src);
    }

    public static ItemStack[] fallback(PlayerEntity p) {
        ItemStack[] a = new ItemStack[41];
        for (int i = 0; i < 41; i++) a[i] = p.getInventory().getStack(i);
        return a;
    }

    public static List<AbstractClientPlayerEntity> nearby(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return List.of();
        double r = Config.range;
        return mc.world.getPlayers().stream()
                .filter(p -> p != mc.player && !p.isSpectator() && p.distanceTo(mc.player) <= r)
                .sorted(Comparator.comparingDouble(p -> p.squaredDistanceTo(mc.player)))
                .limit(Config.maxPlayers)
                .toList();
    }
}
