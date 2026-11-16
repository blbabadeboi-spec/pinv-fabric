package com.exemplo.pinv.client;

import com.exemplo.pinv.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * O servidor NAO manda a lista de efeitos dos outros players, so as particulas.
 * Entao o mod observa o player COMENDO a maca dourada: quando ele termina de comer,
 * conta a duracao da regeneracao (20s na encantada, 5s na comum). Quando acaba, ele fica rosa.
 */
public class RegenTracker {
    /** Entidade sendo desenhada agora (preenchida pelo mixin do renderer). */
    public static Entity current;
    /** Botao "Testar" do menu: pinta todos os players ate esse instante. */
    public static long testUntil = 0;

    private record Eating(int regenTicks, int lastLeft) {}

    private static final Map<UUID, Eating> eating = new HashMap<>();
    private static final Map<UUID, Long> regenEnd = new HashMap<>();
    private static final Map<UUID, Long> pinkUntil = new HashMap<>();

    public static void tick(MinecraftClient mc) {
        if (mc.world == null || mc.player == null || !Config.pinkEnabled) {
            eating.clear();
            regenEnd.clear();
            pinkUntil.clear();
            return;
        }
        long now = System.currentTimeMillis();
        Set<UUID> present = new HashSet<>();

        for (AbstractClientPlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player) continue;
            UUID id = p.getUuid();
            present.add(id);

            boolean using = p.isUsingItem();
            ItemStack active = using ? p.getActiveItem() : ItemStack.EMPTY;
            boolean ench = using && active.isOf(Items.ENCHANTED_GOLDEN_APPLE);
            boolean gold = using && active.isOf(Items.GOLDEN_APPLE);
            Eating e = eating.get(id);

            if (ench || gold) {
                eating.put(id, new Eating(ench ? 400 : 100, p.getItemUseTimeLeft()));
            } else if (e != null) {
                // parou de usar: se estava quase no fim, ele comeu a maca
                if (e.lastLeft() <= 8) {
                    regenEnd.put(id, now + e.regenTicks() * 50L);
                    pinkUntil.remove(id);
                }
                eating.remove(id);
            }

            Long end = regenEnd.get(id);
            if (end != null && now >= end) {
                regenEnd.remove(id);
                pinkUntil.put(id, now + Config.pinkSeconds * 1000L);
            }
        }

        eating.keySet().retainAll(present);
        regenEnd.keySet().retainAll(present);
        pinkUntil.keySet().retainAll(present);
        pinkUntil.values().removeIf(t -> t < now);
    }

    public static boolean isPink(Entity e) {
        if (!(e instanceof PlayerEntity)) return false;
        long now = System.currentTimeMillis();
        if (testUntil > now) return true;
        if (!Config.pinkEnabled) return false;
        Long t = pinkUntil.get(e.getUuid());
        return t != null && t > now;
    }
}
