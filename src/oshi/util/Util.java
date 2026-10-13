package oshi.util;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Util {
   private static final long a = 95288523370837L;
   private static final long e = 4785992528786620510L;

   private Util() {
   }

   public static void sleep(long var0) {
      try {
         Thread.sleep(var0);
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
      }
   }

   public static boolean wildcardMatch(String var0, String var1) {
      return var1.length() > 0 && var1.charAt(0) == (int)e
         ? !wildcardMatch(var0, var1.substring(1))
         : var0.matches(var1.replace("?", ".?").replace("*", ".*?"));
   }

   public static boolean isBlank(String var0) {
      return var0 == null || var0.isEmpty();
   }
}
