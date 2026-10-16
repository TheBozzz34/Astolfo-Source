package bypass.account;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

public class MojangAuth implements Authenticator {
   private String username;
   private String accessToken;
   private UUID clientToken;
   private UUID uuid;
   public int useCount;
   public long lastUsed;

   public MojangAuth(String string, String string2, UUID uuid, UUID uuid2) {
      this.username = string;
      this.accessToken = string2;
      this.clientToken = uuid;
      this.uuid = uuid2;
   }

   @Override
   public String getUsername() {
      return this.username;
   }

   @Override
   public void login(Minecraft minecraft, Consumer<Throwable> consumer) {
   }

   public boolean validate() throws Exception {
      AuthHttp authHttp = new AuthHttp("https://authserver.mojang.com/validate");
      authHttp.setHeader("Content-Type", "application/json");
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("accessToken", this.accessToken);
      jsonObject.addProperty("clientToken", UUIDTypeAdapter.fromUUID(this.clientToken));
      authHttp.post(jsonObject.toString());
      return authHttp.getResponseCode() >= 200 && authHttp.getResponseCode() < 300;
   }

   public void refresh() throws Exception {
      AuthHttp authHttp = new AuthHttp("https://authserver.mojang.com/refresh");
      authHttp.setHeader("Content-Type", "application/json");
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("accessToken", this.accessToken);
      jsonObject.addProperty("clientToken", UUIDTypeAdapter.fromUUID(this.clientToken));
      authHttp.post(jsonObject.toString());
      if (authHttp.getResponseCode() >= 200 && authHttp.getResponseCode() < 300) {
         JsonObject jsonObject2 = new Gson().fromJson(authHttp.readResponse(), JsonObject.class);
         this.accessToken = jsonObject2.get("accessToken").getAsString();
         this.clientToken = UUIDTypeAdapter.fromString(jsonObject2.get("clientToken").getAsString());
         this.uuid = UUIDTypeAdapter.fromString(jsonObject2.getAsJsonObject("selectedProfile").get("id").getAsString());
         this.username = jsonObject2.getAsJsonObject("selectedProfile").get("name").getAsString();
      } else {
         throw new AuthException("ias.mojauth.unknown", authHttp.readErrorResponse());
      }
   }

   @Override
   public boolean canLogin() {
      return true;
   }

   @Override
   public boolean canEdit() {
      return true;
   }

   @Override
   public int getUseCount() {
      return this.useCount;
   }

   @Override
   public long getLastUsed() {
      return this.lastUsed;
   }

   @Override
   public void markUsed() {
      this.useCount++;
      this.lastUsed = System.currentTimeMillis();
   }

   @Override
   public UUID getUuid() {
      return this.uuid;
   }
}
