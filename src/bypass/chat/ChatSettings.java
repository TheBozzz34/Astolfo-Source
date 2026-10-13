package bypass.chat;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ChatSettings implements JsonSerializable {
   private final boolean c;
   private final boolean R;
   private static final long a = 139127221217819L;

   public ChatSettings(boolean var1, boolean var2) {
      this.c = var1;
      this.R = var2;
   }

   @Override
   public String c() {
      return "astolfo chat";
   }

   @Override
   public JsonElement V() {
      JsonObject var5 = new JsonObject();
      var5.addProperty("enabled", this.c);
      var5.addProperty("pings", this.R);
      return var5;
   }
}
