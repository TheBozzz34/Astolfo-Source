package net.minecraft.util;

import java.util.regex.Pattern;

public class StringUtils {
   private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
   private static final long a = 44658011149839L;

   public static String ticksToElapsedTime(int var0) {
      int var3 = var0 / 20;
      int var4 = var3 / 60;
      var3 %= 60;
      return var3 < 10 ? var4 + ":0" + var3 : var4 + ":" + var3;
   }

   public static String stripControlCodes(String var0) {
      return patternControlCode.matcher(var0).replaceAll("");
   }

   public static boolean isNullOrEmpty(String var0) {
      return org.apache.commons.lang3.StringUtils.isEmpty(var0);
   }
}
