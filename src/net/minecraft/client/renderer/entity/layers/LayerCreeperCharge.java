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

   public LayerCreeperCharge(RenderCreeper var1) {
      this.creeperRenderer = var1;
   }

   public void doRenderLayer(EntityCreeper var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      long var9 = a ^ 122388188500882L;
      if (var1.getPowered()) {
         boolean var12 = var1.isInvisible();
         GlStateManager.depthMask(!var12);
         this.creeperRenderer.bindTexture(LIGHTNING_TEXTURE);
         long var10000 = 8740398672684268034L ^ var9;
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         var10000 = 757283221459216660L ^ var9;
         float var13 = var1.ticksExisted + var4;
         GlStateManager.translate(var13 * 0.01F, var13 * 0.01F, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableBlend();
         var10000 = 497000345296003587L ^ var9;
         float var14 = 0.5F;
         GlStateManager.color(var14, var14, var14, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(1, 1);
         this.creeperModel.setModelAttributes(this.creeperRenderer.getMainModel());
         this.creeperModel.render(var1, var2, var3, var5, var6, var7, var8);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         var10000 = 7648244898250723431L ^ var9;
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(var12);
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
