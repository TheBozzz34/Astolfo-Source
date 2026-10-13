package bypass.auth.net;

public final class FileNameSanitizer {
   private static final int D = 128;
   private static final String[] H = new String[]{
      "..", "?", "[", "]", "=", "<", ">", ":", ";", ",", "\"", "\"", "&", "$", "#", "*", "(", ")", "|", "~", "`", "!", "{", "}"
   };
   private static final long a = 16829074934235L;

   public static String l(String var0) {
      for (String var7 : H) {
         var0 = var0.replace(var7, "-");
      }

      if (var0.length() > 128) {
         var0 = var0.substring(0, 128);
      }

      return var0;
   }

   static {
      long var15 = a ^ 124845994538724L;
      long var10000 = var15 ^ 9036654228983386730L;
   }
}
