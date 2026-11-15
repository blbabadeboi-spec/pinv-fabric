package com.exemplo.pinv.mixin;

import com.exemplo.pinv.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    /**
     * O jogo ja desenha entidades invisiveis de forma translucida (fantasma) quando
     * isInvisibleTo(jogador) e falso. Aqui dizemos que o jogador local PODE ver os outros players invisiveis.
     */
    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    private void pinv$ghost(PlayerEntity viewer, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.seeInvisible) return;
        Entity self = (Entity) (Object) this;
        if (self instanceof PlayerEntity && self.getWorld().isClient
                && self != MinecraftClient.getInstance().player) {
            cir.setReturnValue(false);
        }
    }
}
