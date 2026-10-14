package bypass.auth.net;

public final class FileNameSanitizer {
   private static final int D = 128;
   private static final String[] H = new String[]{
      "..", "?", "[", "]", "=", "<", ">", ":", ";", ",", "\"", "\"", "&", "$", "#", "*", "(", ")", "|", "~", "`", "!", "{", "}"
   };

   public static String l(String string) {
      for (String string2 : H) {
         string = string.replace(string2, "-");
      }

      if (string.length() > 128) {
         string = string.substring(0, 128);
      }

      return string;
   }
}
