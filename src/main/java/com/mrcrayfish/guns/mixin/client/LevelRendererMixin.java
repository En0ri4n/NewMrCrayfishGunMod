package com.mrcrayfish.guns.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mrcrayfish.guns.client.handler.BulletTrailRenderingHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish<p>
 * <p>
 * Transformed and adapted as needed by: En0ri4n
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin
{
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 0))
    private void renderBullets(PoseStack stack, float partialTicks, long finishTimeNano, boolean drawBlockOutline, Camera info, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection, CallbackInfo ci)
    {
        BulletTrailRenderingHandler.get().render(stack, partialTicks);
    }
}
