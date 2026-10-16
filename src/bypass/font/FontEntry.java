package bypass.font;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FontEntry implements JsonSerializable {
   private final String fontName;
   private final int fontSize;

   public FontEntry(String string, int i) {
      this.fontName = string;
      this.fontSize = i;
   }

   @Override
   public String getKey() {
      return "font";
   }

   @Override
   public JsonElement toJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("name", this.fontName);
      jsonObject.addProperty("size", this.fontSize);
      return jsonObject;
   }
}
