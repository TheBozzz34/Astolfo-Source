package shadersmod.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SMCLog {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void severe(String message) {
      LOGGER.error("[Shaders] " + message);
   }

   public static void warning(String message) {
      LOGGER.warn("[Shaders] " + message);
   }

   public static void info(String message) {
      LOGGER.info("[Shaders] " + message);
   }

   public static void fine(String message) {
      LOGGER.debug("[Shaders] " + message);
   }

   public static void o(String string, Object... objects) {
      String string2 = String.format(string, objects);
      LOGGER.error("[Shaders] " + string2);
   }

   public static void warning(String formatIn, Object... args) {
      String string = String.format(formatIn, args);
      LOGGER.warn("[Shaders] " + string);
   }

   public static void v(String string, Object... objects) {
      String string2 = String.format(string, objects);
      LOGGER.info("[Shaders] " + string2);
   }

   public static void T(String string, Object... objects) {
      String string2 = String.format(string, objects);
      LOGGER.debug("[Shaders] " + string2);
   }
}
