package bypass.font;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FontEntry implements JsonSerializable {
   private final String V;
   private final int N;
   private static final long a = 118831888780078L;

   public FontEntry(String var1, int var2) {
      this.V = var1;
      this.N = var2;
   }

   @Override
   public String c() {
      return "font";
   }

   @Override
   public JsonElement V() {
      JsonObject var5 = new JsonObject();
      var5.addProperty("name", this.V);
      var5.addProperty("size", this.N);
      return var5;
   }
}
