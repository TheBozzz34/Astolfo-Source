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

   public RenderLightningBolt(RenderManager var1) {
      super(var1);
   }

   public void doRender(EntityLightningBolt var1, double var2, double var4, double var6, float var8, float var9) {
      Tessellator var13 = Tessellator.getInstance();
      WorldRenderer var14 = var13.getWorldRenderer();
      GlStateManager.disableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 1);
      double[] var15 = new double[8];
      double[] var16 = new double[8];
      double var17 = 0.0;
      double var19 = 0.0;
      Random var21 = new Random(var1.boltVertex);

      for (int var22 = 7; var22 >= 0; var22--) {
         var15[var22] = var17;
         var16[var22] = var19;
         var17 += var21.nextInt(11) - 5;
         var19 += var21.nextInt(11) - 5;
      }

      for (int var53 = 0; var53 < 4; var53++) {
         Random var23 = new Random(var1.boltVertex);

         for (int var24 = 0; var24 < 3; var24++) {
            int var25 = 7;
            int var26 = 0;
            if (var24 > 0) {
               var25 = 7 - var24;
            }

            if (var24 > 0) {
               var26 = var25 - 2;
            }

            double var27 = var15[var25] - var17;
            double var29 = var16[var25] - var19;

            for (int var31 = var25; var31 >= var26; var31--) {
               double var32 = var27;
               double var34 = var29;
               if (var24 == 0) {
                  var27 += var23.nextInt(11) - 5;
                  var29 += var23.nextInt(11) - 5;
               } else {
                  var27 += var23.nextInt(31) - 15;
                  var29 += var23.nextInt(31) - 15;
               }

               var14.begin(5, DefaultVertexFormats.POSITION_COLOR);
               double var40 = 0.1 + var53 * 0.2;
               if (var24 == 0) {
                  var40 *= var31 * 0.1 + 1.0;
               }

               double var42 = 0.1 + var53 * 0.2;
               if (var24 == 0) {
                  var42 *= (var31 - 1) * 0.1 + 1.0;
               }

               for (int var44 = 0; var44 < 5; var44++) {
                  double var45 = var2 + 0.5 - var40;
                  double var47 = var6 + 0.5 - var40;
                  if (var44 == 1 || var44 == 2) {
                     var45 += var40 * 2.0;
                  }

                  if (var44 == 2 || var44 == 3) {
                     var47 += var40 * 2.0;
                  }

                  double var49 = var2 + 0.5 - var42;
                  double var51 = var6 + 0.5 - var42;
                  if (var44 == 1 || var44 == 2) {
                     var49 += var42 * 2.0;
                  }

                  if (var44 == 2 || var44 == 3) {
                     var51 += var42 * 2.0;
                  }

                  var14.pos(var49 + var27, var4 + var31 * 16, var51 + var29).color(0.45F, 0.45F, 0.5F, 0.3F).endVertex();
                  var14.pos(var45 + var32, var4 + (var31 + 1) * 16, var47 + var34).color(0.45F, 0.45F, 0.5F, 0.3F).endVertex();
               }

               var13.draw();
            }
         }
      }

      GlStateManager.disableBlend();
      GlStateManager.enableLighting();
      GlStateManager.enableTexture2D();
   }

   public ResourceLocation getEntityTexture(EntityLightningBolt var1) {
      return null;
   }
}
