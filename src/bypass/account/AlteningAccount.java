package bypass.account;

import bypass.manager.JsonSerializable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Base64;

public class AlteningAccount implements JsonSerializable {
   private final String f;
   private final boolean g;
   private static final long a = 137268381768746L;

   public AlteningAccount(String var1, boolean var2) {
      this.f = var1;
      this.g = var2;
   }

   @Override
   public String c() {
      return "altening";
   }

   @Override
   public JsonElement V() {
      JsonObject var5 = new JsonObject();
      var5.addProperty("token", Base64.getEncoder().encodeToString(this.f.getBytes()));
      var5.addProperty("addAccount", this.g);
      return var5;
   }
}
