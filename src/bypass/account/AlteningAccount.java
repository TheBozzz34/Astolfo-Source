package bypass.account;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Base64;

public class AlteningAccount implements JsonSerializable {
   private final String token;
   private final boolean addAccount;

   public AlteningAccount(String string, boolean bl) {
      this.token = string;
      this.addAccount = bl;
   }

   @Override
   public String getKey() {
      return "altening";
   }

   @Override
   public JsonElement toJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("token", Base64.getEncoder().encodeToString(this.token.getBytes()));
      jsonObject.addProperty("addAccount", this.addAccount);
      return jsonObject;
   }
}
