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

   public LayerWitherAura(RenderWither var1) {
      this.witherRenderer = var1;
   }

   public void doRenderLayer(EntityWither var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      long var9 = a ^ 99667813236612L;
      if (var1.isArmored()) {
         GlStateManager.depthMask(!var1.isInvisible());
         this.witherRenderer.bindTexture(WITHER_ARMOR);
         long var10000 = 4059429740802391265L ^ var9;
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         var10000 = 6601287161854134159L ^ var9;
         float var12 = var1.ticksExisted + var4;
         float var13 = MathHelper.cos(var12 * 0.02F) * 3.0F;
         float var14 = var12 * 0.01F;
         GlStateManager.translate(var13, var14, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableBlend();
         var10000 = 3938517004653175143L ^ var9;
         float var15 = 0.5F;
         GlStateManager.color(var15, var15, var15, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(1, 1);
         this.witherModel.setLivingAnimations(var1, var2, var3, var4);
         this.witherModel.setModelAttributes(this.witherRenderer.getMainModel());
         this.witherModel.render(var1, var2, var3, var5, var6, var7, var8);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         var10000 = 4825788885000043899L ^ var9;
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
