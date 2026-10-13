package bypass.account;

import bypass.config.ConfigFiles;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.util.UUIDTypeAdapter;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import org.apache.commons.lang3.tuple.Pair;

public class MicrosoftAuth {
   private static final long a = 38768142994414L;

   public static Pair<String, String> X(String var0) throws IllegalArgumentException, Exception {
      AuthHttp var4 = new AuthHttp("https://login.live.com/oauth20_token.srf");
      var4.z("Content-Type", "application/x-www-form-urlencoded");
      HashMap var5 = new HashMap();
      var5.put("client_id", "54fd49e4-2103-4044-9603-2b028c814ec3");
      var5.put("code", var0);
      var5.put("grant_type", "authorization_code");
      var5.put("redirect_uri", "http://localhost:59125");
      var5.put("scope", "XboxLive.signin XboxLive.offline_access");
      var4.e(var5);
      if (var4.s() >= 200 && var4.s() < 300) {
         JsonObject var6 = ConfigFiles.V.fromJson(var4.K(), JsonObject.class);
         return Pair.of(var6.get("access_token").getAsString(), var6.get("refresh_token").getAsString());
      } else {
         throw new IllegalArgumentException("authCode2Token response: " + var4.s());
      }
   }

   public static Pair<String, String> I(String var0) throws IllegalArgumentException, Exception {
      AuthHttp var4 = new AuthHttp("https://login.live.com/oauth20_token.srf");
      var4.Y();
      HashMap var5 = new HashMap();
      var5.put("client_id", "54fd49e4-2103-4044-9603-2b028c814ec3");
      var5.put("refresh_token", var0);
      var5.put("grant_type", "refresh_token");
      var5.put("redirect_uri", "http://localhost:59125");
      var4.e(var5);
      if (var4.s() >= 200 && var4.s() < 300) {
         JsonObject var6 = ConfigFiles.V.fromJson(var4.K(), JsonObject.class);
         return Pair.of(var6.get("access_token").getAsString(), var6.get("refresh_token").getAsString());
      } else {
         throw new IllegalArgumentException("refreshToken response: " + var4.s());
      }
   }

   public static String x(String var0) throws IllegalArgumentException, Exception {
      AuthHttp var4 = new AuthHttp("https://user.auth.xboxlive.com/user/authenticate");
      var4.z("Content-Type", "application/json");
      var4.z("Accept", "application/json");
      JsonObject var5 = new JsonObject();
      JsonObject var6 = new JsonObject();
      var6.addProperty("AuthMethod", "RPS");
      var6.addProperty("SiteName", "user.auth.xboxlive.com");
      var6.addProperty("RpsTicket", "d=" + var0);
      var5.add("Properties", var6);
      var5.addProperty("RelyingParty", "http://auth.xboxlive.com");
      var5.addProperty("TokenType", "JWT");
      var4.C(var5.toString());
      if (var4.s() >= 200 && var4.s() < 300) {
         return ConfigFiles.V.fromJson(var4.K(), JsonObject.class).get("Token").getAsString();
      } else {
         throw new IllegalArgumentException("authXBL response: " + var4.s());
      }
   }

   public static Pair<String, String> Q(String var0) throws AuthException, IllegalArgumentException, Exception {
      AuthHttp var4 = new AuthHttp("https://xsts.auth.xboxlive.com/xsts/authorize");
      var4.z("Content-Type", "application/json");
      var4.z("Accept", "application/json");
      JsonObject var5 = new JsonObject();
      JsonObject var6 = new JsonObject();
      JsonArray var7 = new JsonArray();
      JsonPrimitive var8 = new JsonPrimitive(var0);
      var7.add(var8);
      var6.add("UserTokens", var7);
      var6.addProperty("SandboxId", "RETAIL");
      var5.add("Properties", var6);
      var5.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
      var5.addProperty("TokenType", "JWT");
      var4.C(var5.toString());
      if (var4.s() == 401) {
         throw new AuthException("ias.msauth.error.noxbox");
      } else if (var4.s() >= 200 && var4.s() < 300) {
         JsonObject var9 = ConfigFiles.V.fromJson(var4.K(), JsonObject.class);
         return Pair.of(
            var9.get("Token").getAsString(), var9.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString()
         );
      } else {
         throw new IllegalArgumentException("authXSTS response: " + var4.s());
      }
   }

   public static String P(String var0, String var1) throws IllegalArgumentException, Exception {
      AuthHttp var5 = new AuthHttp("https://api.minecraftservices.com/authentication/login_with_xbox");
      var5.z("Content-Type", "application/json");
      var5.z("Accept", "application/json");
      JsonObject var6 = new JsonObject();
      var6.addProperty("identityToken", "XBL3.0 x=" + var0 + ";" + var1);
      var5.C(var6.toString());
      if (var5.s() >= 200 && var5.s() < 300) {
         return ConfigFiles.V.fromJson(var5.K(), JsonObject.class).get("access_token").getAsString();
      } else {
         throw new IllegalArgumentException("authMinecraft response: " + var5.s());
      }
   }

   public static void I_void(String var0) throws AuthException, IllegalArgumentException, Exception {
      AuthHttp var4 = new AuthHttp("https://api.minecraftservices.com/entitlements/mcstore");
      var4.z("Authorization", "Bearer " + var0);
      var4.Y();
      if (var4.s() >= 200 && var4.s() < 300) {
         if (ConfigFiles.V.fromJson(var4.K(), JsonObject.class).getAsJsonArray("items").size() == 0) {
            throw new AuthException("ias.msauth.error.gamenotowned");
         }
      } else {
         throw new IllegalArgumentException("checkGameOwnership response: " + var4.s());
      }
   }

   public static Pair<UUID, String> m(String var0) throws IllegalArgumentException, Exception {
      AuthHttp var4 = new AuthHttp("https://api.minecraftservices.com/minecraft/profile");
      var4.z("Authorization", "Bearer " + var0);
      var4.Y();
      if (var4.s() >= 200 && var4.s() < 300) {
         JsonObject var5 = ConfigFiles.V.fromJson(var4.K(), JsonObject.class);
         return Pair.of(UUIDTypeAdapter.fromString(var5.get("id").getAsString()), var5.get("name").getAsString());
      } else {
         throw new IllegalArgumentException("getProfile response: " + var4.s());
      }
   }

   public static MojangAuth d(String var0, String var1) throws AuthException, IOException {
      AuthHttp var6 = new AuthHttp("https://authserver.mojang.com/authenticate");
      var6.z("Content-Type", "application/json");
      UUID var7 = UUID.randomUUID();
      JsonObject var8 = new JsonObject();
      JsonObject var9 = new JsonObject();
      var9.addProperty("name", "Minecraft");
      var9.addProperty("version", 1);
      var8.add("agent", var9);
      var8.addProperty("username", var0);
      var8.addProperty("password", var1);
      var8.addProperty("clientToken", UUIDTypeAdapter.fromUUID(var7));
      var6.C(var8.toString());
      if (var6.s() >= 200 && var6.s() < 300) {
         JsonObject var16 = new Gson().fromJson(var6.K(), JsonObject.class);
         String var17 = var16.get("accessToken").getAsString();
         UUID var18 = UUIDTypeAdapter.fromString(var16.get("clientToken").getAsString());
         if (!var18.equals(var7)) {
            throw new AuthException("ias.mojauth.unknown", "Response token " + var18 + " is not equals to sent token " + var7);
         }

         UUID var19 = UUIDTypeAdapter.fromString(var16.getAsJsonObject("selectedProfile").get("id").getAsString());
         String var14 = var16.getAsJsonObject("selectedProfile").get("name").getAsString();
         return new MojangAuth(var14, var17, var7, var19);
      } else {
         new JsonObject();
         String var11 = var6.q();

         JsonObject var10;
         try {
            var10 = new Gson().fromJson(var11, JsonObject.class);
         } catch (Exception var15) {
            if (var11.toLowerCase().contains("cloudfront")) {
               throw new AuthException("ias.mojauth.toofast", var11);
            }

            throw new AuthException("ias.mojauth.unknown", var11);
         }

         String var12 = var10.get("error").getAsString();
         if (var12.equals("ForbiddenOperationException")) {
            String var13 = var10.get("errorMessage").getAsString();
            if (var13.equals("Invalid credentials. Invalid username or password.")) {
               throw new AuthException("ias.mojauth.invalidcreds", var10.toString());
            }

            if (var13.equals("Invalid credentials.")) {
               throw new AuthException("ias.mojauth.toofast", var10.toString());
            }
         }

         if (var12.equals("ResourceException")) {
            throw new AuthException("ias.mojauth.migrated", var10.toString());
         } else {
            throw new AuthException("ias.mojauth.unknown", var10.toString());
         }
      }
   }
}
