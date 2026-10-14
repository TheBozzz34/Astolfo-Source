package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;

public class LayerSaddle implements LayerRenderer<EntityPig> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/pig/pig_saddle.png");
   private final RenderPig pigRenderer;
   private final ModelPig pigModel = new ModelPig(0.5F);

   public LayerSaddle(RenderPig pigRendererIn) {
      this.pigRenderer = pigRendererIn;
   }

   public void doRenderLayer(EntityPig entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      if (entitylivingbaseIn.getSaddled()) {
         this.pigRenderer.bindTexture(TEXTURE);
         this.pigModel.setModelAttributes(this.pigRenderer.getMainModel());
         this.pigModel.render(entitylivingbaseIn, f, g, h, f2, f3, scale);
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
