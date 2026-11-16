package com.exemplo.pinv.client;

import com.exemplo.pinv.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Percebe quando a Regeneracao de outro player acaba e marca ele como "rosa" por alguns segundos. */
public class RegenTracker {
    /** Entidade sendo desenhada agora (preenchida pelo mixin do renderer). */
    public static Entity current;

    private static final Set<UUID> hadRegen = new HashSet<>();
    private static final Map<UUID, Long> pinkUntil = new HashMap<>();

    public static void tick(MinecraftClient mc) {
        if (mc.world == null || mc.player == null || !Config.pinkEnabled) {
            hadRegen.clear();
            pinkUntil.clear();
            return;
        }
        long now = System.currentTimeMillis();
        Set<UUID> present = new HashSet<>();
        for (AbstractClientPlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player) continue;
            UUID id = p.getUuid();
            present.add(id);
            boolean has = p.hasStatusEffect(StatusEffects.REGENERATION);
            if (has) {
                hadRegen.add(id);
                pinkUntil.remove(id);
            } else if (hadRegen.remove(id)) {
                pinkUntil.put(id, now + Config.pinkSeconds * 1000L);
            }
        }
        hadRegen.retainAll(present);
        pinkUntil.values().removeIf(t -> t < now);
    }

    public static boolean isPink(Entity e) {
        if (e == null || !Config.pinkEnabled) return false;
        Long t = pinkUntil.get(e.getUuid());
        return t != null && t > System.currentTimeMillis();
    }
}
