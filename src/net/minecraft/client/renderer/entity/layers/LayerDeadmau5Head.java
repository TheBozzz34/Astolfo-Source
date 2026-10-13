package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class LayerDeadmau5Head implements LayerRenderer<AbstractClientPlayer> {
   private final RenderPlayer playerRenderer;
   private static final long a = 130841865289046L;

   public LayerDeadmau5Head(RenderPlayer var1) {
      this.playerRenderer = var1;
   }

   public void doRenderLayer(AbstractClientPlayer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.getName().equals("Zarzel") && var1.hasSkin() && !var1.isInvisible()) {
         this.playerRenderer.bindTexture(var1.getLocationSkin());

         for (int var12 = 0; var12 < 2; var12++) {
            float var13 = var1.prevRotationYaw
               + (var1.rotationYaw - var1.prevRotationYaw) * var4
               - (var1.prevRenderYawOffset + (var1.renderYawOffset - var1.prevRenderYawOffset) * var4);
            float var14 = var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var4;
            GlStateManager.pushMatrix();
            GlStateManager.rotate(var13, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(var14, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.375F * (var12 * 2 - 1), 0.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.375F, 0.0F);
            GlStateManager.rotate(-var14, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-var13, 0.0F, 1.0F, 0.0F);
            float var15 = 1.3333334F;
            GlStateManager.scale(var15, var15, var15);
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
