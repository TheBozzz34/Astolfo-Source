package net.minecraft.src;

import java.util.Properties;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.World;

public class CustomSkyLayer {
   public String source = null;
   private int startFadeIn = -1;
   private int endFadeIn = -1;
   private int startFadeOut = -1;
   private int endFadeOut = -1;
   private int blend = 1;
   private boolean rotate = false;
   private float speed = 1.0F;
   private float[] axis;
   private RangeListInt days;
   private int x;
   public int textureId;
   public static final float[] DEFAULT_AXIS = new float[]{1.0F, 0.0F, 0.0F};

   public CustomSkyLayer(Properties props, String defSource) {
      this.axis = DEFAULT_AXIS;
      this.days = null;
      this.x = 8;
      this.textureId = -1;
      ConnectedParser connectedParser = new ConnectedParser("CustomSky");
      this.source = props.getProperty("source", defSource);
      this.startFadeIn = this.parseTime(props.getProperty("startFadeIn"));
      this.endFadeIn = this.parseTime(props.getProperty("endFadeIn"));
      this.startFadeOut = this.parseTime(props.getProperty("startFadeOut"));
      this.endFadeOut = this.parseTime(props.getProperty("endFadeOut"));
      this.blend = Blender.parseBlend(props.getProperty("blend"));
      this.rotate = this.parseBoolean(props.getProperty("rotate"), true);
      this.speed = this.parseFloat(props.getProperty("speed"), 1.0F);
      this.axis = this.parseAxis(props.getProperty("axis"), DEFAULT_AXIS);
      this.days = connectedParser.parseRangeListInt(props.getProperty("days"));
      this.x = connectedParser.parseIntOrDefault(props.getProperty("daysLoop"), 8);
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
      if (this.source == null) {
         Config.warn("No source texture: " + path);
         return false;
      }

      this.source = TextureUtils.fixResourcePath(this.source, TextureUtils.getBasePath(path));
      if (this.startFadeIn >= 0 && this.endFadeIn >= 0 && this.endFadeOut >= 0) {
         int i = this.normalizeTime(this.endFadeIn - this.startFadeIn);
         if (this.startFadeOut < 0) {
            this.startFadeOut = this.normalizeTime(this.endFadeOut - i);
            if (this.timeBetween(this.startFadeOut, this.startFadeIn, this.endFadeIn)) {
               this.startFadeOut = this.endFadeIn;
            }
         }

         int j = this.normalizeTime(this.startFadeOut - this.endFadeIn);
         int k = this.normalizeTime(this.endFadeOut - this.startFadeOut);
         int l = this.normalizeTime(this.startFadeIn - this.endFadeOut);
         int m = i + j + k + l;
         if (m != 24000) {
            Config.warn("Invalid fadeIn/fadeOut times, sum is not 24h: " + m);
            return false;
         } else if (this.speed < 0.0F) {
            Config.warn("Invalid speed: " + this.speed);
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

   public void render(int i, float f, float g) {
      float h = g * this.getFadeBrightness(i);
      h = Config.limit(h, 0.0F, 1.0F);
      if (h >= 1.0E-4F) {
         GlStateManager.bindTexture(this.textureId);
         Blender.setupBlend(this.blend, h);
         GlStateManager.pushMatrix();
         if (this.rotate) {
            GlStateManager.rotate(f * 360.0F * this.speed, this.axis[0], this.axis[1], this.axis[2]);
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
      if (this.timeBetween(timeOfDay, this.startFadeIn, this.endFadeIn)) {
         int i = this.normalizeTime(this.endFadeIn - this.startFadeIn);
         int j = this.normalizeTime(timeOfDay - this.startFadeIn);
         return (float)j / i;
      } else if (this.timeBetween(timeOfDay, this.endFadeIn, this.startFadeOut)) {
         return 1.0F;
      } else if (this.timeBetween(timeOfDay, this.startFadeOut, this.endFadeOut)) {
         int k = this.normalizeTime(this.endFadeOut - this.startFadeOut);
         int l = this.normalizeTime(timeOfDay - this.startFadeOut);
         return 1.0F - (float)l / k;
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
      if (this.timeBetween(timeOfDay, this.endFadeOut, this.startFadeIn)) {
         return false;
      }

      if (this.days != null) {
         long l = world.getWorldTime();
         long m = l - this.startFadeIn;

         while (m < 0L) {
            m += 24000 * this.x;
         }

         int i = (int)(m / 24000L);
         int j = i % this.x;
         if (!this.days.isInRange(j)) {
            return false;
         }
      }

      return true;
   }

   private boolean timeBetween(int timeOfDay, int timeStart, int timeEnd) {
      return timeStart <= timeEnd ? timeOfDay >= timeStart && timeOfDay <= timeEnd : timeOfDay >= timeStart || timeOfDay <= timeEnd;
   }
}
