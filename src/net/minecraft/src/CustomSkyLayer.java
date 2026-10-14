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

   public CustomSkyLayer(Properties props, String defSource) {
      this.b = N;
      this.G = null;
      this.x = 8;
      this.d = -1;
      ConnectedParser connectedParser = new ConnectedParser("CustomSky");
      this.l = props.getProperty("source", defSource);
      this.v = this.parseTime(props.getProperty("startFadeIn"));
      this.j = this.parseTime(props.getProperty("endFadeIn"));
      this.X = this.parseTime(props.getProperty("startFadeOut"));
      this.I = this.parseTime(props.getProperty("endFadeOut"));
      this.Y = Blender.parseBlend(props.getProperty("blend"));
      this.T = this.parseBoolean(props.getProperty("rotate"), true);
      this.O = this.parseFloat(props.getProperty("speed"), 1.0F);
      this.b = this.parseAxis(props.getProperty("axis"), N);
      this.G = connectedParser.F(props.getProperty("days"));
      this.x = connectedParser.O(props.getProperty("daysLoop"), 8);
   }

   private int parseTime(String str) {
      if (str == null) {
         return -1;
      }

      String[] strings = Config.tokenize(str, ":");
      if (strings.length != 2) {
         Config.warn("Invalid time: " + str);
         return -1;
      }

      String string = strings[0];
      String string2 = strings[1];
      int i = Config.parseInt(string, -1);
      int j = Config.parseInt(string2, -1);
      if (i >= 0 && i <= 23 && j >= 0 && j <= 59) {
         i -= 6;
         if (i < 0) {
            i += 24;
         }

         return i * 1000 + (int)(j / 60.0 * 1000.0);
      } else {
         Config.warn("Invalid time: " + str);
         return -1;
      }
   }

   private boolean parseBoolean(String str, boolean defVal) {
      if (str == null) {
         return defVal;
      }

      if (str.toLowerCase().equals("true")) {
         return true;
      }

      if (str.toLowerCase().equals("false")) {
         return false;
      }

      Config.warn("Unknown boolean: " + str);
      return defVal;
   }

   private float parseFloat(String str, float defVal) {
      if (str == null) {
         return defVal;
      } else {
         float f = Config.parseFloat(str, Float.MIN_VALUE);
         if (f == Float.MIN_VALUE) {
            Config.warn("Invalid value: " + str);
            return defVal;
         } else {
            return f;
         }
      }
   }

   private float[] parseAxis(String str, float[] defVal) {
      if (str == null) {
         return defVal;
      }

      String[] strings = Config.tokenize(str, " ");
      if (strings.length != 3) {
         Config.warn("Invalid axis: " + str);
         return defVal;
      }

      float[] floats = new float[3];

      for (int i = 0; i < strings.length; i++) {
         floats[i] = Config.parseFloat(strings[i], Float.MIN_VALUE);
         if (floats[i] == Float.MIN_VALUE) {
            Config.warn("Invalid axis: " + str);
            return defVal;
         }

         if (floats[i] < -1.0F || floats[i] > 1.0F) {
            Config.warn("Invalid axis values: " + str);
            return defVal;
         }
      }

      float f = floats[0];
      float g = floats[1];
      float h = floats[2];
      if (f * f + g * g + h * h < 1.0E-5F) {
         Config.warn("Invalid axis values: " + str);
         return defVal;
      } else {
         return new float[]{h, g, -f};
      }
   }

   public boolean isValid(String path) {
      if (this.l == null) {
         Config.warn("No source texture: " + path);
         return false;
      }

      this.l = TextureUtils.fixResourcePath(this.l, TextureUtils.getBasePath(path));
      if (this.v >= 0 && this.j >= 0 && this.I >= 0) {
         int i = this.normalizeTime(this.j - this.v);
         if (this.X < 0) {
            this.X = this.normalizeTime(this.I - i);
            if (this.timeBetween(this.X, this.v, this.j)) {
               this.X = this.j;
            }
         }

         int k = this.normalizeTime(this.X - this.j);
         int m = this.normalizeTime(this.I - this.X);
         int n = this.normalizeTime(this.v - this.I);
         int i2 = i + k + m + n;
         if (i2 != 24000) {
            Config.warn("Invalid fadeIn/fadeOut times, sum is not 24h: " + i2);
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

   private int normalizeTime(int timeMc) {
      while (timeMc >= 24000) {
         timeMc -= 24000;
      }

      while (timeMc < 0) {
         timeMc += 24000;
      }

      return timeMc;
   }

   public void s(int i, float f, float g) {
      float h = g * this.getFadeBrightness(i);
      h = Config.limit(h, 0.0F, 1.0F);
      if (h >= 1.0E-4F) {
         GlStateManager.bindTexture(this.d);
         Blender.setupBlend(this.Y, h);
         GlStateManager.pushMatrix();
         if (this.T) {
            GlStateManager.rotate(f * 360.0F * this.O, this.b[0], this.b[1], this.b[2]);
         }

         Tessellator tessellator = Tessellator.getInstance();
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
         this.renderSide(tessellator, 4);
         GlStateManager.pushMatrix();
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         this.renderSide(tessellator, 1);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
         this.renderSide(tessellator, 0);
         GlStateManager.popMatrix();
         GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
         this.renderSide(tessellator, 5);
         GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
         this.renderSide(tessellator, 2);
         GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
         this.renderSide(tessellator, 3);
         GlStateManager.popMatrix();
      }
   }

   private float getFadeBrightness(int timeOfDay) {
      if (this.timeBetween(timeOfDay, this.v, this.j)) {
         int i = this.normalizeTime(this.j - this.v);
         int k = this.normalizeTime(timeOfDay - this.v);
         return (float)k / i;
      } else if (this.timeBetween(timeOfDay, this.j, this.X)) {
         return 1.0F;
      } else if (this.timeBetween(timeOfDay, this.X, this.I)) {
         int l = this.normalizeTime(this.I - this.X);
         int m = this.normalizeTime(timeOfDay - this.X);
         return 1.0F - (float)m / l;
      } else {
         return 0.0F;
      }
   }

   private void renderSide(Tessellator tess, int side) {
      WorldRenderer worldRenderer = tess.getWorldRenderer();
      double d = side % 3 / 3.0;
      double e = side / 3 / 2.0;
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(-100.0, -100.0, -100.0).tex(d, e).endVertex();
      worldRenderer.pos(-100.0, -100.0, 100.0).tex(d, e + 0.5).endVertex();
      worldRenderer.pos(100.0, -100.0, 100.0).tex(d + 0.3333333333333333, e + 0.5).endVertex();
      worldRenderer.pos(100.0, -100.0, -100.0).tex(d + 0.3333333333333333, e).endVertex();
      tess.draw();
   }

   public boolean isActive(World world, int timeOfDay) {
      if (this.timeBetween(timeOfDay, this.I, this.v)) {
         return false;
      }

      if (this.G != null) {
         long l = world.getWorldTime();
         long m = l - this.v;

         while (m < 0L) {
            m += 24000 * this.x;
         }

         int i = (int)(m / 24000L);
         int j = i % this.x;
         if (!this.G.isInRange(j)) {
            return false;
         }
      }

      return true;
   }

   private boolean timeBetween(int timeOfDay, int timeStart, int timeEnd) {
      return timeStart <= timeEnd ? timeOfDay >= timeStart && timeOfDay <= timeEnd : timeOfDay >= timeStart || timeOfDay <= timeEnd;
   }
}
