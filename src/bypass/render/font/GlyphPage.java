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
   private final GlyphPage.aun[] k = new GlyphPage.aun[256];
   private int U = -1;
   private int F = 0;
   private int c;
   private int D;
   private static final long a = 85247811898493L;

   public GlyphPage X(Font font, boolean bl) {
      return this.S(font, bl, bl, 16, 2);
   }

   private GlyphPage S(Font font, boolean bl, boolean bl2, int i, int j) {
      if (this.U == -1) {
         this.U = GlHelper.T();
      }

      FontMetrics fontMetrics = new Canvas().getFontMetrics(font);
      int l = 0;
      int m = 0;
      int n = 0;

      for (int i2 = 0; i2 < this.k.length; i2++) {
         char c2 = (char)i2;
         int i3 = fontMetrics.getHeight();
         int i4 = fontMetrics.charWidth(c2);
         if (i2 != 0 && i2 % i == 0) {
            m = j;
            n += l + j;
            l = 0;
         }

         if (i3 > l) {
            l = i3;
            if (l > this.F) {
               this.F = l;
            }
         }

         this.k[i2] = new GlyphPage.aun(m, n, i4, i3);
         m += i4 + j;
         if (m + i4 + j > this.c) {
            this.c = m + i4 + j;
         }

         if (n + i3 + j > this.D) {
            this.D = n + i3 + j;
         }
      }

      BufferedImage bufferedImage = new BufferedImage(this.c, this.D, 2);
      Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
      graphics2D.setFont(font);
      fontMetrics = graphics2D.getFontMetrics(font);
      graphics2D.setColor(new Color(255, 255, 255, 0));
      graphics2D.fillRect(0, 0, this.c, this.D);
      graphics2D.setColor(Color.WHITE);
      graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, bl2 ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
      graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, bl ? RenderingHints.VALUE_TEXT_ANTIALIAS_GASP : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      graphics2D.setRenderingHint(
         RenderingHints.KEY_ALPHA_INTERPOLATION, bl ? RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY : RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED
      );

      for (int i5 = 0; i5 < this.k.length; i5++) {
         graphics2D.drawString(String.valueOf((char)i5), this.k[i5].p, this.k[i5].s + fontMetrics.getAscent());
      }

      GlHelper.X(this.U, bufferedImage, bl ? 9729 : 9728, 10497);
      return this;
   }

   public void p() {
      GlStateManager.bindTexture(this.U);
   }

   public GlyphPage.aun c(char c) {
      return this.k[c];
   }

   public int q(String string) {
      int i = 0;

      for (char c : string.toCharArray()) {
         i += this.k[c].F;
      }

      return i;
   }

   public boolean x(char c) {
      return c >= 0 && c < 256;
   }

   public boolean s() {
      return this.U != -1;
   }

   public int j() {
      return this.F;
   }

   public int r() {
      return this.c;
   }

   public int c() {
      return this.D;
   }

   public class aun {
      public final int p;
      public final int s;
      public final int F;
      public final int j;

      public aun(int i, int j, int l, int m) {
         this.p = i;
         this.s = j;
         this.F = l;
         this.j = m;
      }
   }
}
