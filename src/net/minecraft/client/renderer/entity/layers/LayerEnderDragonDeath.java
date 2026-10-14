package net.minecraft.client.renderer.entity.layers;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.boss.EntityDragon;

public class LayerEnderDragonDeath implements LayerRenderer<EntityDragon> {
   public void doRenderLayer(EntityDragon entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      if (entitylivingbaseIn.deathTicks > 0) {
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         RenderHelper.disableStandardItemLighting();
         float f4 = (entitylivingbaseIn.deathTicks + partialTicks) / 200.0F;
         float f5 = 0.0F;
         if (f4 > 0.8F) {
            f5 = (f4 - 0.8F) / 0.2F;
         }

         Random random = new Random(432L);
         GlStateManager.disableTexture2D();
         GlStateManager.shadeModel(7425);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 1);
         GlStateManager.disableAlpha();
         GlStateManager.enableCull();
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, -1.0F, -2.0F);

         for (int i = 0; i < (f4 + f4 * f4) / 2.0F * 60.0F; i++) {
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F + f4 * 90.0F, 0.0F, 0.0F, 1.0F);
            float f6 = random.nextFloat() * 20.0F + 5.0F + f5 * 10.0F;
            float f7 = random.nextFloat() * 2.0F + 1.0F + f5 * 2.0F;
            worldRenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
            worldRenderer.pos(0.0, 0.0, 0.0).color(255, 255, 255, (int)(255.0F * (1.0F - f5))).endVertex();
            worldRenderer.pos(-0.866 * f7, f6, -0.5F * f7).color(255, 0, 255, 0).endVertex();
            worldRenderer.pos(0.866 * f7, f6, -0.5F * f7).color(255, 0, 255, 0).endVertex();
            worldRenderer.pos(0.0, f6, 1.0F * f7).color(255, 0, 255, 0).endVertex();
            worldRenderer.pos(-0.866 * f7, f6, -0.5F * f7).color(255, 0, 255, 0).endVertex();
            tessellator.draw();
         }

         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
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
