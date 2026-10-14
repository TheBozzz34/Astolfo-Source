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

   public LayerWitherAura(RenderWither witherRendererIn) {
      this.witherRenderer = witherRendererIn;
   }

   public void doRenderLayer(EntityWither entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      if (entitylivingbaseIn.isArmored()) {
         GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());
         this.witherRenderer.bindTexture(WITHER_ARMOR);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         float f4 = entitylivingbaseIn.ticksExisted + partialTicks;
         float f5 = MathHelper.cos(f4 * 0.02F) * 3.0F;
         float f6 = f4 * 0.01F;
         GlStateManager.translate(f5, f6, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableBlend();
         float f7 = 0.5F;
         GlStateManager.color(f7, f7, f7, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(1, 1);
         this.witherModel.setLivingAnimations(entitylivingbaseIn, f, g, partialTicks);
         this.witherModel.setModelAttributes(this.witherRenderer.getMainModel());
         this.witherModel.render(entitylivingbaseIn, f, g, h, f2, f3, scale);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
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
