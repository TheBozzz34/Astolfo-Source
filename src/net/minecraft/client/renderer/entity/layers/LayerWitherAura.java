package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelWither;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderWither;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class LayerWitherAura implements LayerRenderer<EntityWither> {
   private static final ResourceLocation WITHER_ARMOR = new ResourceLocation("textures/entity/wither/wither_armor.png");
   private final RenderWither witherRenderer;
   private final ModelWither witherModel = new ModelWither(0.5F);
   private static final long a = 65063711998092L;

   public LayerWitherAura(RenderWither witherRendererIn) {
      this.witherRenderer = witherRendererIn;
   }

   public void doRenderLayer(EntityWither entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      long l = a ^ 99667813236612L;
      if (entitylivingbaseIn.isArmored()) {
         GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());
         this.witherRenderer.bindTexture(WITHER_ARMOR);
         long m = 4059429740802391265L ^ l;
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         m = 6601287161854134159L ^ l;
         float f4 = entitylivingbaseIn.ticksExisted + partialTicks;
         float f5 = MathHelper.cos(f4 * 0.02F) * 3.0F;
         float f6 = f4 * 0.01F;
         GlStateManager.translate(f5, f6, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableBlend();
         m = 3938517004653175143L ^ l;
         float f7 = 0.5F;
         GlStateManager.color(f7, f7, f7, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(1, 1);
         this.witherModel.setLivingAnimations(entitylivingbaseIn, f, g, partialTicks);
         this.witherModel.setModelAttributes(this.witherRenderer.getMainModel());
         this.witherModel.render(entitylivingbaseIn, f, g, h, f2, f3, scale);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         m = 4825788885000043899L ^ l;
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
