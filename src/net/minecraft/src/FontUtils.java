package net.minecraft.src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.minecraft.util.ResourceLocation;

public class FontUtils {
   private static final long a = 44993888198802L;

   public static Properties readFontProperties(ResourceLocation var0) {
      String var4 = var0.getResourcePath();
      Properties var5 = new Properties();
      String var6 = ".png";
      if (!var4.endsWith(var6)) {
         return var5;
      }

      String var7 = var4.substring(0, var4.length() - var6.length()) + ".properties";

      try {
         ResourceLocation var8 = new ResourceLocation(var0.getResourceDomain(), var7);
         InputStream var9 = Config.getResourceStream(Config.getResourceManager(), var8);
         if (var9 == null) {
            return var5;
         }

         Config.log("Loading " + var7);
         var5.load(var9);
      } catch (FileNotFoundException var10) {
      } catch (IOException var11) {
         var11.printStackTrace();
      }

      return var5;
   }

   public static void readCustomCharWidths(Properties var0, float[] var1) {
      long var2 = a ^ 85193696730783L;

      for (Object var6 : var0.keySet()) {
         String var7 = (String)var6;
         long var10000 = 8008471881356093290L ^ var2;
         String var8 = "width.";
         if (var7.startsWith(var8)) {
            String var9 = var7.substring(var8.length());
            int var10 = Config.parseInt(var9, -1);
            if (var10 >= 0 && var10 < var1.length) {
               String var11 = var0.getProperty(var7);
               float var12 = Config.parseFloat(var11, -1.0F);
               if (var12 >= 0.0F) {
                  var1[var10] = var12;
               }
            }
         }
      }
   }

   public static float readFloat(Properties var0, String var1, float var2) {
      String var6 = var0.getProperty(var1);
      if (var6 == null) {
         return var2;
      } else {
         float var7 = Config.parseFloat(var6, Float.MIN_VALUE);
         if (var7 == Float.MIN_VALUE) {
            Config.warn("Invalid value for " + var1 + ": " + var6);
            return var2;
         } else {
            return var7;
         }
      }
   }

   public static ResourceLocation getHdFontLocation(ResourceLocation var0) {
      if (!Config.m()) {
         return var0;
      }

      if (var0 == null) {
         return var0;
      }

      String var4 = var0.getResourcePath();
      String var5 = "textures/";
      String var6 = "mcpatcher/";
      if (!var4.startsWith(var5)) {
         return var0;
      }

      var4 = var4.substring(var5.length());
      var4 = var6 + var4;
      ResourceLocation var7 = new ResourceLocation(var0.getResourceDomain(), var4);
      return Config.hasResource(Config.getResourceManager(), var7) ? var7 : var0;
   }
}
