package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.monster.EntitySlime;

public class LayerSlimeGel implements LayerRenderer<EntitySlime> {
   private final RenderSlime slimeRenderer;
   private final ModelBase slimeModel = new ModelSlime(0);

   public LayerSlimeGel(RenderSlime slimeRendererIn) {
      this.slimeRenderer = slimeRendererIn;
   }

   public void doRenderLayer(EntitySlime entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      if (!entitylivingbaseIn.isInvisible()) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableNormalize();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         this.slimeModel.setModelAttributes(this.slimeRenderer.getMainModel());
         this.slimeModel.render(entitylivingbaseIn, f, g, h, f2, f3, scale);
         GlStateManager.disableBlend();
         GlStateManager.disableNormalize();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
