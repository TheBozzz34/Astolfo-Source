package net.minecraft.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.apache.logging.log4j.Logger;

public class Util {
   private static final long a = 103166176342370L;

   public static Util.EnumOS getOSType() {
      String var2 = System.getProperty("os.name").toLowerCase();
      return var2.contains("win")
         ? Util.EnumOS.WINDOWS
         : (
            var2.contains("mac")
               ? Util.EnumOS.OSX
               : (
                  var2.contains("solaris")
                     ? Util.EnumOS.SOLARIS
                     : (
                        var2.contains("sunos")
                           ? Util.EnumOS.SOLARIS
                           : (var2.contains("linux") ? Util.EnumOS.LINUX : (var2.contains("unix") ? Util.EnumOS.LINUX : Util.EnumOS.UNKNOWN))
                     )
               )
         );
   }

   public static <V> V runTask(FutureTask<V> var0, Logger var1) {
      try {
         var0.run();
         return (V)var0.get();
      } catch (ExecutionException var5) {
         var1.fatal("Error executing task", var5);
      } catch (InterruptedException var6) {
         var1.fatal("Error executing task", var6);
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
