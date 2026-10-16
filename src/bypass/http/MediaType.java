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

   private String typeName;

   MediaType(String string2) {
      this.typeName = string2;
   }

   public String getTypeName() {
      return this.typeName;
   }

   @Override
   public String toString() {
      return this.getTypeName();
   }
}
