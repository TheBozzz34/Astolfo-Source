package bypass;

public final class VersionInfo {
   private static final String[] authors = new String[]{"Vladymyr96"};

   private VersionInfo() {
   }

   public static String getVersion() {
      return "1.2.0";
   }

   public static String[] getAuthors() {
      return authors;
   }
}
