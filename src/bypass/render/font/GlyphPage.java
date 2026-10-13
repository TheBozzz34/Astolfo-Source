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

   public GlyphPage X(Font var1, boolean var2) {
      return this.S(var1, var2, var2, 16, 2);
   }

   private GlyphPage S(Font var1, boolean var2, boolean var3, int var4, int var5) {
      if (this.U == -1) {
         this.U = GlHelper.T();
      }

      FontMetrics var8 = new Canvas().getFontMetrics(var1);
      int var9 = 0;
      int var10 = 0;
      int var11 = 0;

      for (int var12 = 0; var12 < this.k.length; var12++) {
         char var13 = (char)var12;
         int var14 = var8.getHeight();
         int var15 = var8.charWidth(var13);
         if (var12 != 0 && var12 % var4 == 0) {
            var10 = var5;
            var11 += var9 + var5;
            var9 = 0;
         }

         if (var14 > var9) {
            var9 = var14;
            if (var9 > this.F) {
               this.F = var9;
            }
         }

         this.k[var12] = new GlyphPage.aun(var10, var11, var15, var14);
         var10 += var15 + var5;
         if (var10 + var15 + var5 > this.c) {
            this.c = var10 + var15 + var5;
         }

         if (var11 + var14 + var5 > this.D) {
            this.D = var11 + var14 + var5;
         }
      }

      BufferedImage var17 = new BufferedImage(this.c, this.D, 2);
      Graphics2D var18 = (Graphics2D)var17.getGraphics();
      var18.setFont(var1);
      var8 = var18.getFontMetrics(var1);
      var18.setColor(new Color(255, 255, 255, 0));
      var18.fillRect(0, 0, this.c, this.D);
      var18.setColor(Color.WHITE);
      var18.setRenderingHint(
         RenderingHints.KEY_FRACTIONALMETRICS, var3 ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF
      );
      var18.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, var2 ? RenderingHints.VALUE_TEXT_ANTIALIAS_GASP : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      var18.setRenderingHint(
         RenderingHints.KEY_ALPHA_INTERPOLATION, var2 ? RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY : RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED
      );

      for (int var19 = 0; var19 < this.k.length; var19++) {
         var18.drawString(String.valueOf((char)var19), this.k[var19].p, this.k[var19].s + var8.getAscent());
      }

      GlHelper.X(this.U, var17, var2 ? 9729 : 9728, 10497);
      return this;
   }

   public void p() {
      GlStateManager.bindTexture(this.U);
   }

   public GlyphPage.aun c(char var1) {
      return this.k[var1];
   }

   public int q(String var1) {
      int var5 = 0;

      for (char var9 : var1.toCharArray()) {
         var5 += this.k[var9].F;
      }

      return var5;
   }

   public boolean x(char var1) {
      return var1 >= 0 && var1 < 256;
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

      public aun(int var2, int var3, int var4, int var5) {
         this.p = var2;
         this.s = var3;
         this.F = var4;
         this.j = var5;
      }
   }
}
