package bypass.account;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Base64;

public class AlteningAccount implements JsonSerializable {
   private final String f;
   private final boolean g;
   private static final long a = 137268381768746L;

   public AlteningAccount(String string, boolean bl) {
      this.f = string;
      this.g = bl;
   }

   @Override
   public String c() {
      return "altening";
   }

   @Override
   public JsonElement V() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("token", Base64.getEncoder().encodeToString(this.f.getBytes()));
      jsonObject.addProperty("addAccount", this.g);
      return jsonObject;
   }
}
