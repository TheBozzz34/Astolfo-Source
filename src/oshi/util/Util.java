package oshi.util;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Util {
   private static final long a = 95288523370837L;
   private static final long e = 4785992528786620510L;

   private Util() {
   }

   public static void sleep(long ms) {
      try {
         Thread.sleep(ms);
      } catch (InterruptedException interruptedException) {
         Thread.currentThread().interrupt();
      }
   }

   public static boolean wildcardMatch(String text, String pattern) {
      return pattern.length() > 0 && pattern.charAt(0) == (int)e
         ? !wildcardMatch(text, pattern.substring(1))
         : text.matches(pattern.replace("?", ".?").replace("*", ".*?"));
   }

   public static boolean isBlank(String s) {
      return s == null || s.isEmpty();
   }
}
