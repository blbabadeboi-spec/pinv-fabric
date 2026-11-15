package com.exemplo.pinv.mixin;

import com.exemplo.pinv.Config;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    /** Troca a opacidade fixa do "fantasma" (0.15) pelo valor configurado no menu. */
    @ModifyConstant(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            constant = @Constant(floatValue = 0.15F),
            require = 0)
    private float pinv$ghostAlpha(float original) {
        return Config.seeInvisible ? (float) Config.ghostAlpha : original;
    }
}
