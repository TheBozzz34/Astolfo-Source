package net.minecraft.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.apache.logging.log4j.Logger;

public class Util {
   private static final long a = 103166176342370L;

   public static Util.EnumOS getOSType() {
      String string = System.getProperty("os.name").toLowerCase();
      return string.contains("win")
         ? Util.EnumOS.WINDOWS
         : (
            string.contains("mac")
               ? Util.EnumOS.OSX
               : (
                  string.contains("solaris")
                     ? Util.EnumOS.SOLARIS
                     : (
                        string.contains("sunos")
                           ? Util.EnumOS.SOLARIS
                           : (string.contains("linux") ? Util.EnumOS.LINUX : (string.contains("unix") ? Util.EnumOS.LINUX : Util.EnumOS.UNKNOWN))
                     )
               )
         );
   }

   public static <V> V runTask(FutureTask<V> task, Logger logger) {
      try {
         task.run();
         return (V)task.get();
      } catch (ExecutionException executionException) {
         logger.fatal("Error executing task", executionException);
      } catch (InterruptedException interruptedException) {
         logger.fatal("Error executing task", interruptedException);
      }

      return null;
   }

   public enum EnumOS {
      LINUX,
      SOLARIS,
      WINDOWS,
      OSX,
      UNKNOWN;
   }
}
