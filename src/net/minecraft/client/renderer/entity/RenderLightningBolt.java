package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.ResourceLocation;

public class RenderLightningBolt extends Render<EntityLightningBolt> {
   private static final long a = 19862906130597L;

   public RenderLightningBolt(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   public void doRender(EntityLightningBolt entity, double x, double y, double z, float entityYaw, float partialTicks) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      GlStateManager.disableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 1);
      double[] doubles = new double[8];
      double[] doubles2 = new double[8];
      double d = 0.0;
      double e = 0.0;
      Random random = new Random(entity.boltVertex);

      for (int i = 7; i >= 0; i--) {
         doubles[i] = d;
         doubles2[i] = e;
         d += random.nextInt(11) - 5;
         e += random.nextInt(11) - 5;
      }

      for (int j = 0; j < 4; j++) {
         Random random2 = new Random(entity.boltVertex);

         for (int k = 0; k < 3; k++) {
            int l = 7;
            int m = 0;
            if (k > 0) {
               l = 7 - k;
            }

            if (k > 0) {
               m = l - 2;
            }

            double d2 = doubles[l] - d;
            double d3 = doubles2[l] - e;

            for (int n = l; n >= m; n--) {
               double d4 = d2;
               double d5 = d3;
               if (k == 0) {
                  d2 += random2.nextInt(11) - 5;
                  d3 += random2.nextInt(11) - 5;
               } else {
                  d2 += random2.nextInt(31) - 15;
                  d3 += random2.nextInt(31) - 15;
               }

               worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);
               double d6 = 0.1 + j * 0.2;
               if (k == 0) {
                  d6 *= n * 0.1 + 1.0;
               }

               double d7 = 0.1 + j * 0.2;
               if (k == 0) {
                  d7 *= (n - 1) * 0.1 + 1.0;
               }

               for (int i2 = 0; i2 < 5; i2++) {
                  double d8 = x + 0.5 - d6;
                  double d9 = z + 0.5 - d6;
                  if (i2 == 1 || i2 == 2) {
                     d8 += d6 * 2.0;
                  }

                  if (i2 == 2 || i2 == 3) {
                     d9 += d6 * 2.0;
                  }

                  double d10 = x + 0.5 - d7;
                  double d11 = z + 0.5 - d7;
                  if (i2 == 1 || i2 == 2) {
                     d10 += d7 * 2.0;
                  }

                  if (i2 == 2 || i2 == 3) {
                     d11 += d7 * 2.0;
                  }

                  worldRenderer.pos(d10 + d2, y + n * 16, d11 + d3).color(0.45F, 0.45F, 0.5F, 0.3F).endVertex();
                  worldRenderer.pos(d8 + d4, y + (n + 1) * 16, d9 + d5).color(0.45F, 0.45F, 0.5F, 0.3F).endVertex();
               }

               tessellator.draw();
            }
         }
      }

      GlStateManager.disableBlend();
      GlStateManager.enableLighting();
      GlStateManager.enableTexture2D();
   }

   public ResourceLocation getEntityTexture(EntityLightningBolt entity) {
      return null;
   }
}
