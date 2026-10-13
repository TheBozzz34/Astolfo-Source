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
   private static final long b = 110086881115662L;

   public MojangAuth(String var1, String var2, UUID var3, UUID var4) {
      this.X = var1;
      this.a = var2;
      this.f = var3;
      this.C = var4;
   }

   @Override
   public String a() {
      return this.X;
   }

   @Override
   public void Y(Minecraft var1, Consumer<Throwable> var2) {
   }

   public boolean h() throws Exception {
      AuthHttp var4 = new AuthHttp("https://authserver.mojang.com/validate");
      var4.z("Content-Type", "application/json");
      JsonObject var5 = new JsonObject();
      var5.addProperty("accessToken", this.a);
      var5.addProperty("clientToken", UUIDTypeAdapter.fromUUID(this.f));
      var4.C(var5.toString());
      return var4.s() >= 200 && var4.s() < 300;
   }

   public void Z() throws Exception {
      AuthHttp var4 = new AuthHttp("https://authserver.mojang.com/refresh");
      var4.z("Content-Type", "application/json");
      JsonObject var5 = new JsonObject();
      var5.addProperty("accessToken", this.a);
      var5.addProperty("clientToken", UUIDTypeAdapter.fromUUID(this.f));
      var4.C(var5.toString());
      if (var4.s() >= 200 && var4.s() < 300) {
         JsonObject var6 = new Gson().fromJson(var4.K(), JsonObject.class);
         this.a = var6.get("accessToken").getAsString();
         this.f = UUIDTypeAdapter.fromString(var6.get("clientToken").getAsString());
         this.C = UUIDTypeAdapter.fromString(var6.getAsJsonObject("selectedProfile").get("id").getAsString());
         this.X = var6.getAsJsonObject("selectedProfile").get("name").getAsString();
      } else {
         throw new AuthException("ias.mojauth.unknown", var4.q());
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
