package com.exemplo.pinv;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * O servidor nao manda a lista de efeitos dos outros players. Entao o mod observa o player COMENDO
 * a maca dourada: ao terminar, conta a duracao da regeneracao (30s encantada, 5s comum).
 * Quando acaba, o corpo dele fica rosa por alguns segundos.
 */
public class RegenTracker {
    /** Botao "Testar" do menu: pinta todos os players ate esse instante. */
    public static long testUntil = 0;

    private static final Map<UUID, Integer> eatTicks = new HashMap<UUID, Integer>();
    private static final Map<UUID, Integer> eatRegen = new HashMap<UUID, Integer>();
    private static final Map<UUID, Long> regenEnd = new HashMap<UUID, Long>();
    private static final Map<UUID, Long> pinkUntil = new HashMap<UUID, Long>();

    public static void tick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null || !Config.pinkEnabled) {
            eatTicks.clear();
            eatRegen.clear();
            regenEnd.clear();
            pinkUntil.clear();
            return;
        }
        long now = System.currentTimeMillis();
        Set<UUID> present = new HashSet<UUID>();

        for (Object o : mc.theWorld.playerEntities) {
            EntityPlayer p = (EntityPlayer) o;
            if (p == mc.thePlayer) continue;
            UUID id = p.getUniqueID();
            present.add(id);

            ItemStack held = p.getHeldItem();
            boolean apple = p.isEating() && held != null && held.getItem() == Items.golden_apple;

            if (apple) {
                Integer t = eatTicks.get(id);
                eatTicks.put(id, t == null ? 1 : t + 1);
                eatRegen.put(id, held.getMetadata() == 1 ? 600 : 100);
            } else {
                Integer t = eatTicks.remove(id);
                Integer regen = eatRegen.remove(id);
                // comeu ate o fim (a animacao dura ~32 ticks)
                if (t != null && regen != null && t >= 26) {
                    regenEnd.put(id, now + regen * 50L);
                    pinkUntil.remove(id);
                }
            }

            Long end = regenEnd.get(id);
            if (end != null && now >= end) {
                regenEnd.remove(id);
                pinkUntil.put(id, now + Config.pinkSeconds * 1000L);
            }
        }

        eatTicks.keySet().retainAll(present);
        eatRegen.keySet().retainAll(present);
        regenEnd.keySet().retainAll(present);
        pinkUntil.keySet().retainAll(present);

        Set<UUID> expired = new HashSet<UUID>();
        for (Map.Entry<UUID, Long> en : pinkUntil.entrySet()) {
            if (en.getValue() < now) expired.add(en.getKey());
        }
        pinkUntil.keySet().removeAll(expired);
    }

    public static boolean isPink(EntityPlayer p) {
        long now = System.currentTimeMillis();
        if (testUntil > now) return true;
        if (!Config.pinkEnabled) return false;
        Long t = pinkUntil.get(p.getUniqueID());
        return t != null && t > now;
    }
}
