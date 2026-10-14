package bypass.font;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FontEntry implements JsonSerializable {
   private final String V;
   private final int N;

   public FontEntry(String string, int i) {
      this.V = string;
      this.N = i;
   }

   @Override
   public String c() {
      return "font";
   }

   @Override
   public JsonElement V() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("name", this.V);
      jsonObject.addProperty("size", this.N);
      return jsonObject;
   }
}
