package bypass.auth.net;

public final class FileNameSanitizer {
   private static final int D = 128;
   private static final String[] H = new String[]{
      "..", "?", "[", "]", "=", "<", ">", ":", ";", ",", "\"", "\"", "&", "$", "#", "*", "(", ")", "|", "~", "`", "!", "{", "}"
   };
   private static final long a = 16829074934235L;

   public static String l(String string) {
      for (String string2 : H) {
         string = string.replace(string2, "-");
      }

      if (string.length() > 128) {
         string = string.substring(0, 128);
      }

      return string;
   }

   static {
      long l = a ^ 124845994538724L;
      long m = l ^ 9036654228983386730L;
   }
}
