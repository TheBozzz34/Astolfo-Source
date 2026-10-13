package net.minecraft.src;

import java.util.Properties;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.World;

public class CustomSkyLayer {
   public String l = null;
   private int v = -1;
   private int j = -1;
   private int X = -1;
   private int I = -1;
   private int Y = 1;
   private boolean T = false;
   private float O = 1.0F;
   private float[] b;
   private RangeListInt G;
   private int x;
   public int d;
   public static final float[] N = new float[]{1.0F, 0.0F, 0.0F};
   private static final long a = 38928256471646L;

   public CustomSkyLayer(Properties var1, String var2) {
      this.b = N;
      this.G = null;
      this.x = 8;
      this.d = -1;
      ConnectedParser var5 = new ConnectedParser("CustomSky");
      this.l = var1.getProperty("source", var2);
      this.v = this.parseTime(var1.getProperty("startFadeIn"));
      this.j = this.parseTime(var1.getProperty("endFadeIn"));
      this.X = this.parseTime(var1.getProperty("startFadeOut"));
      this.I = this.parseTime(var1.getProperty("endFadeOut"));
      this.Y = Blender.parseBlend(var1.getProperty("blend"));
      this.T = this.parseBoolean(var1.getProperty("rotate"), true);
      this.O = this.parseFloat(var1.getProperty("speed"), 1.0F);
      this.b = this.parseAxis(var1.getProperty("axis"), N);
      this.G = var5.F(var1.getProperty("days"));
      this.x = var5.O(var1.getProperty("daysLoop"), 8);
   }

   private int parseTime(String var1) {
      if (var1 == null) {
         return -1;
      }

      String[] var5 = Config.tokenize(var1, ":");
      if (var5.length != 2) {
         Config.warn("Invalid time: " + var1);
         return -1;
      }

      String var6 = var5[0];
      String var7 = var5[1];
      int var8 = Config.parseInt(var6, -1);
      int var9 = Config.parseInt(var7, -1);
      if (var8 >= 0 && var8 <= 23 && var9 >= 0 && var9 <= 59) {
         var8 -= 6;
         if (var8 < 0) {
            var8 += 24;
         }

         return var8 * 1000 + (int)(var9 / 60.0 * 1000.0);
      } else {
         Config.warn("Invalid time: " + var1);
         return -1;
      }
   }

   private boolean parseBoolean(String var1, boolean var2) {
      if (var1 == null) {
         return var2;
      }

      if (var1.toLowerCase().equals("true")) {
         return true;
      }

      if (var1.toLowerCase().equals("false")) {
         return false;
      }

      Config.warn("Unknown boolean: " + var1);
      return var2;
   }

   private float parseFloat(String var1, float var2) {
      if (var1 == null) {
         return var2;
      } else {
         float var6 = Config.parseFloat(var1, Float.MIN_VALUE);
         if (var6 == Float.MIN_VALUE) {
            Config.warn("Invalid value: " + var1);
            return var2;
         } else {
            return var6;
         }
      }
   }

   private float[] parseAxis(String var1, float[] var2) {
      if (var1 == null) {
         return var2;
      }

      String[] var6 = Config.tokenize(var1, " ");
      if (var6.length != 3) {
         Config.warn("Invalid axis: " + var1);
         return var2;
      }

      float[] var7 = new float[3];

      for (int var8 = 0; var8 < var6.length; var8++) {
         var7[var8] = Config.parseFloat(var6[var8], Float.MIN_VALUE);
         if (var7[var8] == Float.MIN_VALUE) {
            Config.warn("Invalid axis: " + var1);
            return var2;
         }

         if (var7[var8] < -1.0F || var7[var8] > 1.0F) {
            Config.warn("Invalid axis values: " + var1);
            return var2;
         }
      }

      float var12 = var7[0];
      float var9 = var7[1];
      float var10 = var7[2];
      if (var12 * var12 + var9 * var9 + var10 * var10 < 1.0E-5F) {
         Config.warn("Invalid axis values: " + var1);
         return var2;
      } else {
         return new float[]{var10, var9, -var12};
      }
   }

   public boolean isValid(String var1) {
      if (this.l == null) {
         Config.warn("No source texture: " + var1);
         return false;
      }

      this.l = TextureUtils.fixResourcePath(this.l, TextureUtils.getBasePath(var1));
      if (this.v >= 0 && this.j >= 0 && this.I >= 0) {
         int var5 = this.normalizeTime(this.j - this.v);
         if (this.X < 0) {
            this.X = this.normalizeTime(this.I - var5);
            if (this.timeBetween(this.X, this.v, this.j)) {
               this.X = this.j;
            }
         }

         int var6 = this.normalizeTime(this.X - this.j);
         int var7 = this.normalizeTime(this.I - this.X);
         int var8 = this.normalizeTime(this.v - this.I);
         int var9 = var5 + var6 + var7 + var8;
         if (var9 != 24000) {
            Config.warn("Invalid fadeIn/fadeOut times, sum is not 24h: " + var9);
            return false;
         } else if (this.O < 0.0F) {
            Config.warn("Invalid speed: " + this.O);
            return false;
         } else if (this.x <= 0) {
            Config.warn("Invalid daysLoop: " + this.x);
            return false;
         } else {
            return true;
         }
      } else {
         Config.warn("Invalid times, required are: startFadeIn, endFadeIn and endFadeOut.");
         return false;
      }
   }

   private int normalizeTime(int var1) {
      while (var1 >= 24000) {
         var1 -= 24000;
      }

      while (var1 < 0) {
         var1 += 24000;
      }

      return var1;
   }

   public void s(int var1, float var2, float var3) {
      float var7 = var3 * this.getFadeBrightness(var1);
      var7 = Config.limit(var7, 0.0F, 1.0F);
      if (var7 >= 1.0E-4F) {
         GlStateManager.bindTexture(this.d);
         Blender.setupBlend(this.Y, var7);
         GlStateManager.pushMatrix();
         if (this.T) {
            GlStateManager.rotate(var2 * 360.0F * this.O, this.b[0], this.b[1], this.b[2]);
         }

         Tessellator var8 = Tessellator.getInstance();
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
         this.renderSide(var8, 4);
         GlStateManager.pushMatrix();
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         this.renderSide(var8, 1);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
         this.renderSide(var8, 0);
         GlStateManager.popMatrix();
         GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
         this.renderSide(var8, 5);
         GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
         this.renderSide(var8, 2);
         GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
         this.renderSide(var8, 3);
         GlStateManager.popMatrix();
      }
   }

   private float getFadeBrightness(int var1) {
      if (this.timeBetween(var1, this.v, this.j)) {
         int var7 = this.normalizeTime(this.j - this.v);
         int var8 = this.normalizeTime(var1 - this.v);
         return (float)var8 / var7;
      } else if (this.timeBetween(var1, this.j, this.X)) {
         return 1.0F;
      } else if (this.timeBetween(var1, this.X, this.I)) {
         int var5 = this.normalizeTime(this.I - this.X);
         int var6 = this.normalizeTime(var1 - this.X);
         return 1.0F - (float)var6 / var5;
      } else {
         return 0.0F;
      }
   }

   private void renderSide(Tessellator var1, int var2) {
      WorldRenderer var5 = var1.getWorldRenderer();
      double var6 = var2 % 3 / 3.0;
      double var8 = var2 / 3 / 2.0;
      var5.begin(7, DefaultVertexFormats.POSITION_TEX);
      var5.pos(-100.0, -100.0, -100.0).tex(var6, var8).endVertex();
      var5.pos(-100.0, -100.0, 100.0).tex(var6, var8 + 0.5).endVertex();
      var5.pos(100.0, -100.0, 100.0).tex(var6 + 0.3333333333333333, var8 + 0.5).endVertex();
      var5.pos(100.0, -100.0, -100.0).tex(var6 + 0.3333333333333333, var8).endVertex();
      var1.draw();
   }

   public boolean isActive(World var1, int var2) {
      if (this.timeBetween(var2, this.I, this.v)) {
         return false;
      }

      if (this.G != null) {
         long var6 = var1.getWorldTime();
         long var8 = var6 - this.v;

         while (var8 < 0L) {
            var8 += 24000 * this.x;
         }

         int var10 = (int)(var8 / 24000L);
         int var11 = var10 % this.x;
         if (!this.G.isInRange(var11)) {
            return false;
         }
      }

      return true;
   }

   private boolean timeBetween(int var1, int var2, int var3) {
      return var2 <= var3 ? var1 >= var2 && var1 <= var3 : var1 >= var2 || var1 <= var3;
   }
}
