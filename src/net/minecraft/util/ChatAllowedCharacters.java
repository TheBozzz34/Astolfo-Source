package net.minecraft.util;

public class ChatAllowedCharacters {
   public static final char[] allowedCharactersArray = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
   private static final long a = 121205712528851L;

   public static boolean isAllowedCharacter(char var0) {
      return var0 != 167 && var0 >= ' ' && var0 != 127;
   }

   public static String filterAllowedCharacters(String var0) {
      StringBuilder var4 = new StringBuilder();

      for (char var8 : var0.toCharArray()) {
         if (isAllowedCharacter(var8)) {
            var4.append(var8);
         }
      }

      return var4.toString();
   }
}
