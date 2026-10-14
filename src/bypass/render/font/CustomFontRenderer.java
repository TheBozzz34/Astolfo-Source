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
      for (int i = 0; i < 32; i++) {
         int j = (i >> 3 & 1) * 85;
         int k = (i >> 2 & 1) * 170 + j;
         int l = (i >> 1 & 1) * 170 + j;
         int m = (i & 1) * 170 + j;
         if (i == 6) {
            k += 85;
         }

         if (i >= 16) {
            k /= 4;
            l /= 4;
            m /= 4;
         }

         this.h[i] = (k & 0xFF) << 16 | (l & 0xFF) << 8 | m & 0xFF;
      }
   }

   @Deprecated
   @Override
   public float B(GlyphPage glyphPage, String string, float f, float g, int i) {
      return 0.0F;
   }

   @Override
   public float drawString(String string, float f, float g, int i) {
      return this.drawString(string, f, g, i, false);
   }

   public float drawStringWithShadow(String string, float f, float g, int i) {
      return HUD.cFont.getValue()
         ? Math.max(this.drawString(string, f + 0.5F, g + 0.5F, i, true), this.drawString(string, f, g, i, false))
         : Minecraft.getMinecraft().fontRendererObj.drawString(string, f, g + 2.0F, i, true);
   }

   public void drawCenteredStringWithShadow(String string, float f, float g, int i) {
      this.drawStringWithShadow(string, f - this.getStringWidth(string) / 2, g - this.getStringHeight(string) / 2, i);
   }

   public void drawCenteredString(String string, float f, float g, int i) {
      this.drawString(string, f - this.getStringWidth(string) / 2, g - this.getStringHeight(string) / 2, i);
   }

   public float v(String string, float f, float g, int i, boolean bl, boolean bl2) {
      long l = b ^ 80426383024866L;
      if (string == null) {
         return 0.0F;
      }

      if (i == 553648127) {
         i = 16777215;
      }

      if ((i & -67108864) == 0) {
         i |= -16777216;
      }

      if (bl) {
         i = (i & 16579836) >> 2 | i & 0xFF000000;
      }

      GlyphPage glyphPage = this.v;
      float f2 = (i >> 24 & 0xFF) / 255.0F;
      boolean bl3 = false;
      long m = 7218836669071253982L ^ l;
      boolean bl4 = false;
      boolean bl5 = false;
      boolean bl6 = false;
      boolean bl7 = false;
      f *= 2.0F;
      g *= 2.0F;
      GlStateManager.pushMatrix();
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      GlStateManager.enableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color((i >> 16 & 0xFF) / 255.0F, (i >> 8 & 0xFF) / 255.0F, (i & 0xFF) / 255.0F, f2);
      int k = string.length();
      glyphPage.p();

      for (int n = 0; n < k; n++) {
         char c2 = string.charAt(n);
         if (c2 == 167 && n < k && n + 1 < k) {
            int i2 = "0123456789abcdefklmnor".indexOf(string.charAt(n + 1));
            if (i2 < 16) {
               bl4 = false;
               bl5 = false;
               bl3 = false;
               bl7 = false;
               bl6 = false;
               glyphPage = this.v;
               glyphPage.p();
               if (i2 < 0 || i2 > 15) {
                  i2 = 15;
               }

               if (bl) {
                  i2 += 16;
               }

               int i3 = this.h[i2];
               GlStateManager.color((i3 >> 16 & 0xFF) / 255.0F, (i3 >> 8 & 0xFF) / 255.0F, (i3 & 0xFF) / 255.0F, f2);
            } else if (i2 == 16) {
               bl3 = true;
            } else if (i2 == 17) {
               bl4 = true;
               if (bl5) {
                  glyphPage = this.p;
                  glyphPage.p();
               } else {
                  glyphPage = this.X;
                  glyphPage.p();
               }
            } else if (i2 == 18) {
               bl6 = true;
            } else if (i2 == 19) {
               bl7 = true;
            } else if (i2 == 20) {
               bl5 = true;
               if (bl4) {
                  glyphPage = this.p;
                  glyphPage.p();
               } else {
                  glyphPage = this.C;
                  glyphPage.p();
               }
            } else if (i2 == 21) {
               bl4 = false;
               bl5 = false;
               bl3 = false;
               bl7 = false;
               bl6 = false;
               GlStateManager.color((i >> 16 & 0xFF) / 255.0F, (i >> 8 & 0xFF) / 255.0F, (i & 0xFF) / 255.0F, f2);
               glyphPage = this.v;
               glyphPage.p();
            }

            n++;
         } else if (glyphPage.x(c2)) {
            if (bl3) {
               char c3 = 0;

               while (glyphPage.c(c3).F != glyphPage.c(c2).F) {
                  c3 = (char)(Math.random() * 256.0);
               }

               c2 = c3;
            }

            GlyphPage.aun aun2 = GlyphFontRenderer.O(glyphPage, f, g, c2);
            if (bl6) {
               RenderUtil.lineVertices(f, g + aun2.j / 4 + 2.0F, f + aun2.F / 2, g + aun2.j / 4 + 2.0F, 1.0F);
            }

            if (bl7) {
               RenderUtil.lineVertices(f, g + aun2.j / 2, f + aun2.F / 2, g + aun2.j / 2, 1.0F);
            }

            f += aun2.F + this.N;
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      return f;
   }

   public float drawString(String string, float f, float g, int i, boolean bl) {
      return this.v(string, f, g, i, bl, true);
   }

   public int getStringHeight(String string) {
      if (string == null) {
         return 0;
      }

      int i = 0;
      GlyphPage glyphPage = this.v;
      boolean bl = false;
      boolean bl2 = false;
      int k = string.length();

      for (int l = 0; l < k; l++) {
         char c2 = string.charAt(l);
         if (c2 == 167 && l < k) {
            int m = "0123456789abcdefklmnor".indexOf(c2);
            if (m < 16) {
               bl = false;
               bl2 = false;
            } else if (m == 17) {
               bl = true;
               if (bl2) {
                  glyphPage = this.p;
               } else {
                  glyphPage = this.X;
               }
            } else if (m == 20) {
               bl2 = true;
               if (bl) {
                  glyphPage = this.p;
               } else {
                  glyphPage = this.C;
               }
            } else if (m == 21) {
               bl = false;
               bl2 = false;
               glyphPage = this.v;
            }

            l++;
         } else if (glyphPage.x(c2) && glyphPage.c(c2).j > i) {
            i = glyphPage.c(c2).j;
         }
      }

      return i / 2;
   }

   public int getStringWidth(String string) {
      return this.getStringWidth(string, false);
   }

   public int getStringWidth(String string, boolean bl) {
      if (string == null) {
         return 0;
      }

      if (!HUD.cFont.getValue() && !bl) {
         return Minecraft.getMinecraft().fontRendererObj.getStringWidth(string);
      }

      int i = 0;
      GlyphPage glyphPage = this.v;
      boolean bl2 = false;
      boolean bl3 = false;
      int j = string.length();

      for (int k = 0; k < j; k++) {
         char c2 = string.charAt(k);
         if (c2 == 167 && k < j) {
            int l = "0123456789abcdefklmnor".indexOf(c2);
            if (l < 16) {
               bl2 = false;
               bl3 = false;
            } else if (l == 17) {
               bl2 = true;
               if (bl3) {
                  glyphPage = this.p;
               } else {
                  glyphPage = this.X;
               }
            } else if (l == 20) {
               bl3 = true;
               if (bl2) {
                  glyphPage = this.p;
               } else {
                  glyphPage = this.C;
               }
            } else if (l == 21) {
               bl2 = false;
               bl3 = false;
               glyphPage = this.v;
            }

            k++;
         } else if (glyphPage.x(c2)) {
            i += glyphPage.c(c2).F + this.N;
         }
      }

      return i / 2;
   }

   public void B(Font font, boolean bl) {
      this.v.X(font, bl);
      this.X.X(font.deriveFont(1), bl);
      this.C.X(font.deriveFont(2), bl);
      this.p.X(font.deriveFont(3), bl);
   }
}
