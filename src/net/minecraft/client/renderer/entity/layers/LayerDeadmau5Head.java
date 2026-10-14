package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class LayerDeadmau5Head implements LayerRenderer<AbstractClientPlayer> {
   private final RenderPlayer playerRenderer;

   public LayerDeadmau5Head(RenderPlayer playerRendererIn) {
      this.playerRenderer = playerRendererIn;
   }

   public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      if (entitylivingbaseIn.getName().equals("Zarzel") && entitylivingbaseIn.hasSkin() && !entitylivingbaseIn.isInvisible()) {
         this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationSkin());

         for (int i = 0; i < 2; i++) {
            float f4 = entitylivingbaseIn.prevRotationYaw
               + (entitylivingbaseIn.rotationYaw - entitylivingbaseIn.prevRotationYaw) * partialTicks
               - (entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks);
            float f5 = entitylivingbaseIn.prevRotationPitch + (entitylivingbaseIn.rotationPitch - entitylivingbaseIn.prevRotationPitch) * partialTicks;
            GlStateManager.pushMatrix();
            GlStateManager.rotate(f4, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f5, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.375F * (i * 2 - 1), 0.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.375F, 0.0F);
            GlStateManager.rotate(-f5, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-f4, 0.0F, 1.0F, 0.0F);
            float f6 = 1.3333334F;
            GlStateManager.scale(f6, f6, f6);
            this.playerRenderer.getMainModel().renderDeadmau5Head(0.0625F);
            GlStateManager.popMatrix();
         }
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
