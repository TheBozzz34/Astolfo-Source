package net.minecraft.client.renderer.entity.layers;

import net.minecraft.entity.EntityLivingBase;

public interface LayerRenderer<E extends EntityLivingBase> {
   void doRenderLayer(E entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale);

   boolean shouldCombineTextures();
}
