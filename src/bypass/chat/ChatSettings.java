package bypass.chat;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ChatSettings implements JsonSerializable {
   private final boolean c;
   private final boolean R;

   public ChatSettings(boolean bl, boolean bl2) {
      this.c = bl;
      this.R = bl2;
   }

   @Override
   public String c() {
      return "astolfo chat";
   }

   @Override
   public JsonElement V() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("enabled", this.c);
      jsonObject.addProperty("pings", this.R);
      return jsonObject;
   }
}
