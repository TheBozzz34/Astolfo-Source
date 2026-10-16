package bypass.render.font;

import bypass.render.GlHelper;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.GlStateManager;

public final class GlyphPage {
   private final GlyphPage.aun[] glyphs = new GlyphPage.aun[256];
   private int textureId = -1;
   private int maxGlyphHeight = 0;
   private int textureWidth;
   private int textureHeight;

   public GlyphPage generate(Font font, boolean bl) {
      return this.generateAtlas(font, bl, bl, 16, 2);
   }

   private GlyphPage generateAtlas(Font font, boolean bl, boolean bl2, int i, int j) {
      if (this.textureId == -1) {
         this.textureId = GlHelper.genTexture();
      }

      FontMetrics fontMetrics = new Canvas().getFontMetrics(font);
      int k = 0;
      int l = 0;
      int m = 0;

      for (int n = 0; n < this.glyphs.length; n++) {
         char c = (char)n;
         int i2 = fontMetrics.getHeight();
         int i3 = fontMetrics.charWidth(c);
         if (n != 0 && n % i == 0) {
            l = j;
            m += k + j;
            k = 0;
         }

         if (i2 > k) {
            k = i2;
            if (k > this.maxGlyphHeight) {
               this.maxGlyphHeight = k;
            }
         }

         this.glyphs[n] = new GlyphPage.aun(l, m, i3, i2);
         l += i3 + j;
         if (l + i3 + j > this.textureWidth) {
            this.textureWidth = l + i3 + j;
         }

         if (m + i2 + j > this.textureHeight) {
            this.textureHeight = m + i2 + j;
         }
      }

      BufferedImage bufferedImage = new BufferedImage(this.textureWidth, this.textureHeight, 2);
      Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
      graphics2D.setFont(font);
      fontMetrics = graphics2D.getFontMetrics(font);
      graphics2D.setColor(new Color(255, 255, 255, 0));
      graphics2D.fillRect(0, 0, this.textureWidth, this.textureHeight);
      graphics2D.setColor(Color.WHITE);
      graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, bl2 ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
      graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, bl ? RenderingHints.VALUE_TEXT_ANTIALIAS_GASP : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      graphics2D.setRenderingHint(
         RenderingHints.KEY_ALPHA_INTERPOLATION, bl ? RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY : RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED
      );

      for (int i4 = 0; i4 < this.glyphs.length; i4++) {
         graphics2D.drawString(String.valueOf((char)i4), this.glyphs[i4].x, this.glyphs[i4].y + fontMetrics.getAscent());
      }

      GlHelper.uploadImage(this.textureId, bufferedImage, bl ? 9729 : 9728, 10497);
      return this;
   }

   public void bindTexture() {
      GlStateManager.bindTexture(this.textureId);
   }

   public GlyphPage.aun getGlyph(char c) {
      return this.glyphs[c];
   }

   public int getStringWidth(String string) {
      int i = 0;

      for (char c : string.toCharArray()) {
         i += this.glyphs[c].width;
      }

      return i;
   }

   public boolean hasGlyph(char c) {
      return c >= 0 && c < 256;
   }

   public boolean isGenerated() {
      return this.textureId != -1;
   }

   public int getMaxGlyphHeight() {
      return this.maxGlyphHeight;
   }

   public int getTextureWidth() {
      return this.textureWidth;
   }

   public int getTextureHeight() {
      return this.textureHeight;
   }

   public class aun {
      public final int x;
      public final int y;
      public final int width;
      public final int height;

      public aun(int i, int j, int k, int l) {
         this.x = i;
         this.y = j;
         this.width = k;
         this.height = l;
      }
   }
}
