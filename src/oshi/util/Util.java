package oshi.util;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Util {
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
      return pattern.length() > 0 && pattern.charAt(0) == '^'
         ? !wildcardMatch(text, pattern.substring(1))
         : text.matches(pattern.replace("?", ".?").replace("*", ".*?"));
   }

   public static boolean isBlank(String s) {
      return s == null || s.isEmpty();
   }
}
