package net.minecraft.src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.minecraft.util.ResourceLocation;

public class FontUtils {
   private static final long a = 44993888198802L;

   public static Properties readFontProperties(ResourceLocation locationFontTexture) {
      String string = locationFontTexture.getResourcePath();
      Properties properties2 = new Properties();
      String string2 = ".png";
      if (!string.endsWith(string2)) {
         return properties2;
      }

      String string3 = string.substring(0, string.length() - string2.length()) + ".properties";

      try {
         ResourceLocation resourceLocation = new ResourceLocation(locationFontTexture.getResourceDomain(), string3);
         InputStream inputStream = Config.getResourceStream(Config.getResourceManager(), resourceLocation);
         if (inputStream == null) {
            return properties2;
         }

         Config.log("Loading " + string3);
         properties2.load(inputStream);
      } catch (FileNotFoundException fileNotFoundException) {
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }

      return properties2;
   }

   public static void readCustomCharWidths(Properties props, float[] charWidth) {
      long l = a ^ 85193696730783L;

      for (Object object : props.keySet()) {
         String string = (String)object;
         long m = 8008471881356093290L ^ l;
         String string2 = "width.";
         if (string.startsWith(string2)) {
            String string3 = string.substring(string2.length());
            int i = Config.parseInt(string3, -1);
            if (i >= 0 && i < charWidth.length) {
               String string4 = props.getProperty(string);
               float f = Config.parseFloat(string4, -1.0F);
               if (f >= 0.0F) {
                  charWidth[i] = f;
               }
            }
         }
      }
   }

   public static float readFloat(Properties props, String key, float defOffset) {
      String string = props.getProperty(key);
      if (string == null) {
         return defOffset;
      } else {
         float f = Config.parseFloat(string, Float.MIN_VALUE);
         if (f == Float.MIN_VALUE) {
            Config.warn("Invalid value for " + key + ": " + string);
            return defOffset;
         } else {
            return f;
         }
      }
   }

   public static ResourceLocation getHdFontLocation(ResourceLocation fontLoc) {
      if (!Config.m()) {
         return fontLoc;
      }

      if (fontLoc == null) {
         return fontLoc;
      }

      String string = fontLoc.getResourcePath();
      String string2 = "textures/";
      String string3 = "mcpatcher/";
      if (!string.startsWith(string2)) {
         return fontLoc;
      }

      string = string.substring(string2.length());
      string = string3 + string;
      ResourceLocation resourceLocation = new ResourceLocation(fontLoc.getResourceDomain(), string);
      return Config.hasResource(Config.getResourceManager(), resourceLocation) ? resourceLocation : fontLoc;
   }
}
