package bypass.chat;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ChatSettings implements JsonSerializable {
   private final boolean enabled;
   private final boolean pings;

   public ChatSettings(boolean bl, boolean bl2) {
      this.enabled = bl;
      this.pings = bl2;
   }

   @Override
   public String getKey() {
      return "astolfo chat";
   }

   @Override
   public JsonElement toJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("enabled", this.enabled);
      jsonObject.addProperty("pings", this.pings);
      return jsonObject;
   }
}
