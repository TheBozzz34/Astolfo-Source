package oshi.util;

import java.util.Properties;
import oshi.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class GlobalConfig {
   private static final Properties CONFIG = FileUtil.readPropertiesFromFilename("");
   private static final long a = 12727871831450L;

   private GlobalConfig() {
   }

   public static String get(String var0, String var1) {
      return CONFIG.getProperty(var0, var1);
   }

   public static int get(String var0, int var1) {
      String var4 = CONFIG.getProperty(var0);
      return var4 == null ? var1 : ParseUtil.parseIntOrDefault(var4, var1);
   }

   public static double get(String var0, double var1) {
      String var6 = CONFIG.getProperty(var0);
      return var6 == null ? var1 : ParseUtil.parseDoubleOrDefault(var6, var1);
   }

   public static boolean get(String var0, boolean var1) {
      String var5 = CONFIG.getProperty(var0);
      return var5 == null ? var1 : Boolean.parseBoolean(var5);
   }

   public static void set(String var0, Object var1) {
      if (var1 == null) {
         CONFIG.remove(var0);
      } else {
         CONFIG.setProperty(var0, var1.toString());
      }
   }

   public static void remove(String var0) {
      CONFIG.remove(var0);
   }

   public static void clear() {
      CONFIG.clear();
   }

   public static void load(Properties var0) {
      CONFIG.putAll(var0);
   }

   public static class PropertyException extends RuntimeException {
      private static final long serialVersionUID = 94599842162352L;

      public PropertyException(String var1) {
         super("Invalid property: \"" + var1 + "\" = " + GlobalConfig.get(var1, null));
      }

      public PropertyException(String var1, String var2) {
         super("Invalid property \"" + var1 + "\": " + var2);
      }
   }
}
