package shadersmod.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SMCLog {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final long a = 104605001389058L;

   public static void severe(String var0) {
      LOGGER.error("[Shaders] " + var0);
   }

   public static void warning(String var0) {
      LOGGER.warn("[Shaders] " + var0);
   }

   public static void info(String var0) {
      LOGGER.info("[Shaders] " + var0);
   }

   public static void fine(String var0) {
      LOGGER.debug("[Shaders] " + var0);
   }

   public static void o(String var0, Object... var1) {
      String var4 = String.format(var0, var1);
      LOGGER.error("[Shaders] " + var4);
   }

   public static void warning(String var0, Object... var1) {
      String var4 = String.format(var0, var1);
      LOGGER.warn("[Shaders] " + var4);
   }

   public static void v(String var0, Object... var1) {
      String var5 = String.format(var0, var1);
      LOGGER.info("[Shaders] " + var5);
   }

   public static void T(String var0, Object... var1) {
      String var6 = String.format(var0, var1);
      LOGGER.debug("[Shaders] " + var6);
   }
}
