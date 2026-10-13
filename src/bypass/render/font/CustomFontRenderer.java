package bypass.render.font;

import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import java.awt.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public final class CustomFontRenderer extends GlyphFontRenderer {
   private final GlyphPage X = new GlyphPage();
   private final GlyphPage C = new GlyphPage();
   private final GlyphPage p = new GlyphPage();
   private final int[] h = new int[32];
   private final String V = "0123456789abcdefklmnor";
   private static final long b = 4075160982218L;

   public CustomFontRenderer() {
      for (int var3 = 0; var3 < 32; var3++) {
         int var4 = (var3 >> 3 & 1) * 85;
         int var5 = (var3 >> 2 & 1) * 170 + var4;
         int var6 = (var3 >> 1 & 1) * 170 + var4;
         int var7 = (var3 & 1) * 170 + var4;
         if (var3 == 6) {
            var5 += 85;
         }

         if (var3 >= 16) {
            var5 /= 4;
            var6 /= 4;
            var7 /= 4;
         }

         this.h[var3] = (var5 & 0xFF) << 16 | (var6 & 0xFF) << 8 | var7 & 0xFF;
      }
   }

   @Deprecated
   @Override
   public float B(GlyphPage var1, String var2, float var3, float var4, int var5) {
      return 0.0F;
   }

   @Override
   public float drawString(String var1, float var2, float var3, int var4) {
      return this.drawString(var1, var2, var3, var4, false);
   }

   public float drawStringWithShadow(String var1, float var2, float var3, int var4) {
      return HUD.cFont.getValue()
         ? Math.max(this.drawString(var1, var2 + 0.5F, var3 + 0.5F, var4, true), this.drawString(var1, var2, var3, var4, false))
         : Minecraft.getMinecraft().fontRendererObj.drawString(var1, var2, var3 + 2.0F, var4, true);
   }

   public void drawCenteredStringWithShadow(String var1, float var2, float var3, int var4) {
      this.drawStringWithShadow(var1, var2 - this.getStringWidth(var1) / 2, var3 - this.getStringHeight(var1) / 2, var4);
   }

   public void drawCenteredString(String var1, float var2, float var3, int var4) {
      this.drawString(var1, var2 - this.getStringWidth(var1) / 2, var3 - this.getStringHeight(var1) / 2, var4);
   }

   public float v(String var1, float var2, float var3, int var4, boolean var5, boolean var6) {
      long var7 = b ^ 80426383024866L;
      if (var1 == null) {
         return 0.0F;
      }

      if (var4 == 553648127) {
         var4 = 16777215;
      }

      if ((var4 & -67108864) == 0) {
         var4 |= -16777216;
      }

      if (var5) {
         var4 = (var4 & 16579836) >> 2 | var4 & 0xFF000000;
      }

      GlyphPage var10 = this.v;
      float var11 = (var4 >> 24 & 0xFF) / 255.0F;
      boolean var12 = false;
      long var10000 = 7218836669071253982L ^ var7;
      boolean var13 = false;
      boolean var14 = false;
      boolean var15 = false;
      boolean var16 = false;
      var2 *= 2.0F;
      var3 *= 2.0F;
      GlStateManager.pushMatrix();
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      GlStateManager.enableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color((var4 >> 16 & 0xFF) / 255.0F, (var4 >> 8 & 0xFF) / 255.0F, (var4 & 0xFF) / 255.0F, var11);
      int var17 = var1.length();
      var10.p();

      for (int var18 = 0; var18 < var17; var18++) {
         char var19 = var1.charAt(var18);
         if (var19 == 167 && var18 < var17 && var18 + 1 < var17) {
            int var25 = "0123456789abcdefklmnor".indexOf(var1.charAt(var18 + 1));
            if (var25 < 16) {
               var13 = false;
               var14 = false;
               var12 = false;
               var16 = false;
               var15 = false;
               var10 = this.v;
               var10.p();
               if (var25 < 0 || var25 > 15) {
                  var25 = 15;
               }

               if (var5) {
                  var25 += 16;
               }

               int var21 = this.h[var25];
               GlStateManager.color((var21 >> 16 & 0xFF) / 255.0F, (var21 >> 8 & 0xFF) / 255.0F, (var21 & 0xFF) / 255.0F, var11);
            } else if (var25 == 16) {
               var12 = true;
            } else if (var25 == 17) {
               var13 = true;
               if (var14) {
                  var10 = this.p;
                  var10.p();
               } else {
                  var10 = this.X;
                  var10.p();
               }
            } else if (var25 == 18) {
               var15 = true;
            } else if (var25 == 19) {
               var16 = true;
            } else if (var25 == 20) {
               var14 = true;
               if (var13) {
                  var10 = this.p;
                  var10.p();
               } else {
                  var10 = this.C;
                  var10.p();
               }
            } else if (var25 == 21) {
               var13 = false;
               var14 = false;
               var12 = false;
               var16 = false;
               var15 = false;
               GlStateManager.color((var4 >> 16 & 0xFF) / 255.0F, (var4 >> 8 & 0xFF) / 255.0F, (var4 & 0xFF) / 255.0F, var11);
               var10 = this.v;
               var10.p();
            }

            var18++;
         } else if (var10.x(var19)) {
            if (var12) {
               char var20 = 0;

               while (var10.c(var20).F != var10.c(var19).F) {
                  var20 = (char)(Math.random() * 256.0);
               }

               var19 = var20;
            }

            GlyphPage.aun var24 = GlyphFontRenderer.O(var10, var2, var3, var19);
            if (var15) {
               RenderUtil.lineVertices(var2, var3 + var24.j / 4 + 2.0F, var2 + var24.F / 2, var3 + var24.j / 4 + 2.0F, 1.0F);
            }

            if (var16) {
               RenderUtil.lineVertices(var2, var3 + var24.j / 2, var2 + var24.F / 2, var3 + var24.j / 2, 1.0F);
            }

            var2 += var24.F + this.N;
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      return var2;
   }

   public float drawString(String var1, float var2, float var3, int var4, boolean var5) {
      return this.v(var1, var2, var3, var4, var5, true);
   }

   public int getStringHeight(String var1) {
      if (var1 == null) {
         return 0;
      }

      int var5 = 0;
      GlyphPage var6 = this.v;
      boolean var7 = false;
      boolean var8 = false;
      int var9 = var1.length();

      for (int var10 = 0; var10 < var9; var10++) {
         char var11 = var1.charAt(var10);
         if (var11 == 167 && var10 < var9) {
            int var12 = "0123456789abcdefklmnor".indexOf(var11);
            if (var12 < 16) {
               var7 = false;
               var8 = false;
            } else if (var12 == 17) {
               var7 = true;
               if (var8) {
                  var6 = this.p;
               } else {
                  var6 = this.X;
               }
            } else if (var12 == 20) {
               var8 = true;
               if (var7) {
                  var6 = this.p;
               } else {
                  var6 = this.C;
               }
            } else if (var12 == 21) {
               var7 = false;
               var8 = false;
               var6 = this.v;
            }

            var10++;
         } else if (var6.x(var11) && var6.c(var11).j > var5) {
            var5 = var6.c(var11).j;
         }
      }

      return var5 / 2;
   }

   public int getStringWidth(String var1) {
      return this.getStringWidth(var1, false);
   }

   public int getStringWidth(String var1, boolean var2) {
      if (var1 == null) {
         return 0;
      }

      if (!HUD.cFont.getValue() && !var2) {
         return Minecraft.getMinecraft().fontRendererObj.getStringWidth(var1);
      }

      int var6 = 0;
      GlyphPage var7 = this.v;
      boolean var8 = false;
      boolean var9 = false;
      int var10 = var1.length();

      for (int var11 = 0; var11 < var10; var11++) {
         char var12 = var1.charAt(var11);
         if (var12 == 167 && var11 < var10) {
            int var13 = "0123456789abcdefklmnor".indexOf(var12);
            if (var13 < 16) {
               var8 = false;
               var9 = false;
            } else if (var13 == 17) {
               var8 = true;
               if (var9) {
                  var7 = this.p;
               } else {
                  var7 = this.X;
               }
            } else if (var13 == 20) {
               var9 = true;
               if (var8) {
                  var7 = this.p;
               } else {
                  var7 = this.C;
               }
            } else if (var13 == 21) {
               var8 = false;
               var9 = false;
               var7 = this.v;
            }

            var11++;
         } else if (var7.x(var12)) {
            var6 += var7.c(var12).F + this.N;
         }
      }

      return var6 / 2;
   }

   public void B(Font var1, boolean var2) {
      this.v.X(var1, var2);
      this.X.X(var1.deriveFont(1), var2);
      this.C.X(var1.deriveFont(2), var2);
      this.p.X(var1.deriveFont(3), var2);
   }
}
