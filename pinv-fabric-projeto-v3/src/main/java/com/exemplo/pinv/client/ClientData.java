package com.exemplo.pinv.client;

import com.exemplo.pinv.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClientData {
    private record Entry(ItemStack[] items, long time) {}

    private static final Map<UUID, Entry> CACHE = new HashMap<>();

    public static void put(UUID id, ItemStack[] items) {
        CACHE.put(id, new Entry(items, System.currentTimeMillis()));
    }

    /** 1) dados do servidor (se ele tem o mod), 2) servidor integrado, 3) so o equipamento visivel. */
    public static ItemStack[] get(MinecraftClient mc, PlayerEntity p) {
        Entry e = CACHE.get(p.getUuid());
        if (e != null && System.currentTimeMillis() - e.time() < 3000) return e.items();

        IntegratedServer srv = mc.getServer();
        if (srv != null) {
            ServerPlayerEntity sp = srv.getPlayerManager().getPlayer(p.getUuid());
            if (sp != null) return fallback(sp);
        }
        return fallback(p);
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
