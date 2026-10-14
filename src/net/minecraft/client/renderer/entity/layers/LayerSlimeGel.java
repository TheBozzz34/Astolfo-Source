package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.monster.EntitySlime;

public class LayerSlimeGel implements LayerRenderer<EntitySlime> {
   private final RenderSlime slimeRenderer;
   private final ModelBase slimeModel = new ModelSlime(0);
   private static final long a = 129946385225543L;

   public LayerSlimeGel(RenderSlime slimeRendererIn) {
      this.slimeRenderer = slimeRendererIn;
   }

   public void doRenderLayer(EntitySlime entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      long l = a ^ 108608627680796L;
      if (!entitylivingbaseIn.isInvisible()) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableNormalize();
         long m = 6647230615109399514L ^ l;
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
