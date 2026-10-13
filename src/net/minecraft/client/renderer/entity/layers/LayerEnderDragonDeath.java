package net.minecraft.client.renderer.entity.layers;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.boss.EntityDragon;

public class LayerEnderDragonDeath implements LayerRenderer<EntityDragon> {
   private static final long a = 48522250044644L;

   public void doRenderLayer(EntityDragon var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      long var9 = a ^ 128401481445014L;
      if (var1.deathTicks > 0) {
         Tessellator var12 = Tessellator.getInstance();
         WorldRenderer var13 = var12.getWorldRenderer();
         RenderHelper.disableStandardItemLighting();
         float var14 = (var1.deathTicks + var4) / 200.0F;
         float var15 = 0.0F;
         if (var14 > 0.8F) {
            var15 = (var14 - 0.8F) / 0.2F;
         }

         Random var16 = new Random(432L);
         GlStateManager.disableTexture2D();
         long var10000 = 1237620780460898848L ^ var9;
         GlStateManager.shadeModel(7425);
         GlStateManager.enableBlend();
         var10000 = 5024837102255047908L ^ var9;
         GlStateManager.blendFunc(770, 1);
         GlStateManager.disableAlpha();
         GlStateManager.enableCull();
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, -1.0F, -2.0F);

         for (int var17 = 0; var17 < (var14 + var14 * var14) / 2.0F * 60.0F; var17++) {
            GlStateManager.rotate(var16.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(var16.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(var16.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(var16.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(var16.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(var16.nextFloat() * 360.0F + var14 * 90.0F, 0.0F, 0.0F, 1.0F);
            float var18 = var16.nextFloat() * 20.0F + 5.0F + var15 * 10.0F;
            float var19 = var16.nextFloat() * 2.0F + 1.0F + var15 * 2.0F;
            var13.begin(6, DefaultVertexFormats.POSITION_COLOR);
            var13.pos(0.0, 0.0, 0.0).color(255, 255, 255, (int)(255.0F * (1.0F - var15))).endVertex();
            var13.pos(-0.866 * var19, var18, -0.5F * var19).color(255, 0, 255, 0).endVertex();
            var13.pos(0.866 * var19, var18, -0.5F * var19).color(255, 0, 255, 0).endVertex();
            var13.pos(0.0, var18, 1.0F * var19).color(255, 0, 255, 0).endVertex();
            var13.pos(-0.866 * var19, var18, -0.5F * var19).color(255, 0, 255, 0).endVertex();
            var12.draw();
         }

         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         var10000 = 8224665305279087972L ^ var9;
         GlStateManager.disableCull();
         GlStateManager.disableBlend();
         GlStateManager.shadeModel(7424);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableTexture2D();
         GlStateManager.enableAlpha();
         RenderHelper.enableStandardItemLighting();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
