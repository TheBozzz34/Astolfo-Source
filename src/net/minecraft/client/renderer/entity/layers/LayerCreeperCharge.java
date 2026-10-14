package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;

public class LayerCreeperCharge implements LayerRenderer<EntityCreeper> {
   private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
   private final RenderCreeper creeperRenderer;
   private final ModelCreeper creeperModel = new ModelCreeper(2.0F);
   private static final long a = 87889438414011L;

   public LayerCreeperCharge(RenderCreeper creeperRendererIn) {
      this.creeperRenderer = creeperRendererIn;
   }

   public void doRenderLayer(EntityCreeper entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      long l = a ^ 122388188500882L;
      if (entitylivingbaseIn.getPowered()) {
         boolean bl = entitylivingbaseIn.isInvisible();
         GlStateManager.depthMask(!bl);
         this.creeperRenderer.bindTexture(LIGHTNING_TEXTURE);
         long m = 8740398672684268034L ^ l;
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         m = 757283221459216660L ^ l;
         float f4 = entitylivingbaseIn.ticksExisted + partialTicks;
         GlStateManager.translate(f4 * 0.01F, f4 * 0.01F, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableBlend();
         m = 497000345296003587L ^ l;
         float f5 = 0.5F;
         GlStateManager.color(f5, f5, f5, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(1, 1);
         this.creeperModel.setModelAttributes(this.creeperRenderer.getMainModel());
         this.creeperModel.render(entitylivingbaseIn, f, g, h, f2, f3, scale);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         m = 7648244898250723431L ^ l;
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(bl);
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
