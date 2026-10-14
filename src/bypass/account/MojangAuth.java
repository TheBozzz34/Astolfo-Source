package bypass.account;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

public class MojangAuth implements Authenticator {
   private String X;
   private String a;
   private UUID f;
   private UUID C;
   public int Y;
   public long T;

   public MojangAuth(String string, String string2, UUID uuid, UUID uuid2) {
      this.X = string;
      this.a = string2;
      this.f = uuid;
      this.C = uuid2;
   }

   @Override
   public String a() {
      return this.X;
   }

   @Override
   public void Y(Minecraft minecraft, Consumer<Throwable> consumer) {
   }

   public boolean h() throws Exception {
      AuthHttp authHttp = new AuthHttp("https://authserver.mojang.com/validate");
      authHttp.z("Content-Type", "application/json");
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("accessToken", this.a);
      jsonObject.addProperty("clientToken", UUIDTypeAdapter.fromUUID(this.f));
      authHttp.C(jsonObject.toString());
      return authHttp.s() >= 200 && authHttp.s() < 300;
   }

   public void Z() throws Exception {
      AuthHttp authHttp = new AuthHttp("https://authserver.mojang.com/refresh");
      authHttp.z("Content-Type", "application/json");
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("accessToken", this.a);
      jsonObject.addProperty("clientToken", UUIDTypeAdapter.fromUUID(this.f));
      authHttp.C(jsonObject.toString());
      if (authHttp.s() >= 200 && authHttp.s() < 300) {
         JsonObject jsonObject2 = new Gson().fromJson(authHttp.K(), JsonObject.class);
         this.a = jsonObject2.get("accessToken").getAsString();
         this.f = UUIDTypeAdapter.fromString(jsonObject2.get("clientToken").getAsString());
         this.C = UUIDTypeAdapter.fromString(jsonObject2.getAsJsonObject("selectedProfile").get("id").getAsString());
         this.X = jsonObject2.getAsJsonObject("selectedProfile").get("name").getAsString();
      } else {
         throw new AuthException("ias.mojauth.unknown", authHttp.q());
      }
   }

   @Override
   public boolean u() {
      return true;
   }

   @Override
   public boolean d() {
      return true;
   }

   @Override
   public int B() {
      return this.Y;
   }

   @Override
   public long v() {
      return this.T;
   }

   @Override
   public void n() {
      this.Y++;
      this.T = System.currentTimeMillis();
   }

   @Override
   public UUID W() {
      return this.C;
   }
}
