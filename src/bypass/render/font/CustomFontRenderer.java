package bypass.render.font;

import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import java.awt.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public final class CustomFontRenderer extends GlyphFontRenderer {
   private final GlyphPage boldPage = new GlyphPage();
   private final GlyphPage italicPage = new GlyphPage();
   private final GlyphPage boldItalicPage = new GlyphPage();
   private final int[] colorCodes = new int[32];
   private final String formatCodes = "0123456789abcdefklmnor";

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

         this.colorCodes[i] = (k & 0xFF) << 16 | (l & 0xFF) << 8 | m & 0xFF;
      }
   }

   @Deprecated
   @Override
   public float drawStringWithPage(GlyphPage glyphPage, String string, float f, float g, int i) {
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

   public float renderString(String string, float f, float g, int i, boolean bl, boolean bl2) {
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

      GlyphPage glyphPage2 = this.glyphPage;
      float h = (i >> 24 & 0xFF) / 255.0F;
      boolean bl3 = false;
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
      GlStateManager.color((i >> 16 & 0xFF) / 255.0F, (i >> 8 & 0xFF) / 255.0F, (i & 0xFF) / 255.0F, h);
      int j = string.length();
      glyphPage2.bindTexture();

      for (int k = 0; k < j; k++) {
         char c = string.charAt(k);
         if (c == 167 && k < j && k + 1 < j) {
            int l = "0123456789abcdefklmnor".indexOf(string.charAt(k + 1));
            if (l < 16) {
               bl4 = false;
               bl5 = false;
               bl3 = false;
               bl7 = false;
               bl6 = false;
               glyphPage2 = this.glyphPage;
               glyphPage2.bindTexture();
               if (l < 0 || l > 15) {
                  l = 15;
               }

               if (bl) {
                  l += 16;
               }

               int m = this.colorCodes[l];
               GlStateManager.color((m >> 16 & 0xFF) / 255.0F, (m >> 8 & 0xFF) / 255.0F, (m & 0xFF) / 255.0F, h);
            } else if (l == 16) {
               bl3 = true;
            } else if (l == 17) {
               bl4 = true;
               if (bl5) {
                  glyphPage2 = this.boldItalicPage;
                  glyphPage2.bindTexture();
               } else {
                  glyphPage2 = this.boldPage;
                  glyphPage2.bindTexture();
               }
            } else if (l == 18) {
               bl6 = true;
            } else if (l == 19) {
               bl7 = true;
            } else if (l == 20) {
               bl5 = true;
               if (bl4) {
                  glyphPage2 = this.boldItalicPage;
                  glyphPage2.bindTexture();
               } else {
                  glyphPage2 = this.italicPage;
                  glyphPage2.bindTexture();
               }
            } else if (l == 21) {
               bl4 = false;
               bl5 = false;
               bl3 = false;
               bl7 = false;
               bl6 = false;
               GlStateManager.color((i >> 16 & 0xFF) / 255.0F, (i >> 8 & 0xFF) / 255.0F, (i & 0xFF) / 255.0F, h);
               glyphPage2 = this.glyphPage;
               glyphPage2.bindTexture();
            }

            k++;
         } else if (glyphPage2.hasGlyph(c)) {
            if (bl3) {
               char c2 = 0;

               while (glyphPage2.getGlyph(c2).width != glyphPage2.getGlyph(c).width) {
                  c2 = (char)(Math.random() * 256.0);
               }

               c = c2;
            }

            GlyphPage.aun aun2 = GlyphFontRenderer.drawGlyph(glyphPage2, f, g, c);
            if (bl6) {
               RenderUtil.lineVertices(f, g + aun2.height / 4 + 2.0F, f + aun2.width / 2, g + aun2.height / 4 + 2.0F, 1.0F);
            }

            if (bl7) {
               RenderUtil.lineVertices(f, g + aun2.height / 2, f + aun2.width / 2, g + aun2.height / 2, 1.0F);
            }

            f += aun2.width + this.charSpacing;
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      return f;
   }

   public float drawString(String string, float f, float g, int i, boolean bl) {
      return this.renderString(string, f, g, i, bl, true);
   }

   public int getStringHeight(String string) {
      if (string == null) {
         return 0;
      }

      int i = 0;
      GlyphPage glyphPage2 = this.glyphPage;
      boolean bl = false;
      boolean bl2 = false;
      int j = string.length();

      for (int k = 0; k < j; k++) {
         char c = string.charAt(k);
         if (c == 167 && k < j) {
            int l = "0123456789abcdefklmnor".indexOf(c);
            if (l < 16) {
               bl = false;
               bl2 = false;
            } else if (l == 17) {
               bl = true;
               if (bl2) {
                  glyphPage2 = this.boldItalicPage;
               } else {
                  glyphPage2 = this.boldPage;
               }
            } else if (l == 20) {
               bl2 = true;
               if (bl) {
                  glyphPage2 = this.boldItalicPage;
               } else {
                  glyphPage2 = this.italicPage;
               }
            } else if (l == 21) {
               bl = false;
               bl2 = false;
               glyphPage2 = this.glyphPage;
            }

            k++;
         } else if (glyphPage2.hasGlyph(c) && glyphPage2.getGlyph(c).height > i) {
            i = glyphPage2.getGlyph(c).height;
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
      GlyphPage glyphPage2 = this.glyphPage;
      boolean bl2 = false;
      boolean bl3 = false;
      int j = string.length();

      for (int k = 0; k < j; k++) {
         char c = string.charAt(k);
         if (c == 167 && k < j) {
            int l = "0123456789abcdefklmnor".indexOf(c);
            if (l < 16) {
               bl2 = false;
               bl3 = false;
            } else if (l == 17) {
               bl2 = true;
               if (bl3) {
                  glyphPage2 = this.boldItalicPage;
               } else {
                  glyphPage2 = this.boldPage;
               }
            } else if (l == 20) {
               bl3 = true;
               if (bl2) {
                  glyphPage2 = this.boldItalicPage;
               } else {
                  glyphPage2 = this.italicPage;
               }
            } else if (l == 21) {
               bl2 = false;
               bl3 = false;
               glyphPage2 = this.glyphPage;
            }

            k++;
         } else if (glyphPage2.hasGlyph(c)) {
            i += glyphPage2.getGlyph(c).width + this.charSpacing;
         }
      }

      return i / 2;
   }

   public void loadFont(Font font, boolean bl) {
      this.glyphPage.generate(font, bl);
      this.boldPage.generate(font.deriveFont(1), bl);
      this.italicPage.generate(font.deriveFont(2), bl);
      this.boldItalicPage.generate(font.deriveFont(3), bl);
   }
}
