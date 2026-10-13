package bypass.http;

public enum MediaType {
   APPLICATION("application"),
   AUDIO("audio"),
   EXAMPLE("example"),
   IMAGE("image"),
   MESSAGE("message"),
   MODEL("model"),
   MULTIPART("multipart"),
   TEXT("text"),
   VIDEO("video");

   private String e;

   MediaType(String var3) {
      this.e = var3;
   }

   public String e() {
      return this.e;
   }

   @Override
   public String toString() {
      return this.e();
   }
}
