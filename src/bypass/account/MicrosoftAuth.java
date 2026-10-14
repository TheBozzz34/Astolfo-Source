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
   public static Pair<String, String> X(String string) throws IllegalArgumentException, Exception {
      AuthHttp authHttp = new AuthHttp("https://login.live.com/oauth20_token.srf");
      authHttp.z("Content-Type", "application/x-www-form-urlencoded");
      HashMap hashMap = new HashMap();
      hashMap.put("client_id", "54fd49e4-2103-4044-9603-2b028c814ec3");
      hashMap.put("code", string);
      hashMap.put("grant_type", "authorization_code");
      hashMap.put("redirect_uri", "http://localhost:59125");
      hashMap.put("scope", "XboxLive.signin XboxLive.offline_access");
      authHttp.e(hashMap);
      if (authHttp.s() >= 200 && authHttp.s() < 300) {
         JsonObject jsonObject = ConfigFiles.V.fromJson(authHttp.K(), JsonObject.class);
         return Pair.of(jsonObject.get("access_token").getAsString(), jsonObject.get("refresh_token").getAsString());
      } else {
         throw new IllegalArgumentException("authCode2Token response: " + authHttp.s());
      }
   }

   public static Pair<String, String> I(String string) throws IllegalArgumentException, Exception {
      AuthHttp authHttp = new AuthHttp("https://login.live.com/oauth20_token.srf");
      authHttp.Y();
      HashMap hashMap = new HashMap();
      hashMap.put("client_id", "54fd49e4-2103-4044-9603-2b028c814ec3");
      hashMap.put("refresh_token", string);
      hashMap.put("grant_type", "refresh_token");
      hashMap.put("redirect_uri", "http://localhost:59125");
      authHttp.e(hashMap);
      if (authHttp.s() >= 200 && authHttp.s() < 300) {
         JsonObject jsonObject = ConfigFiles.V.fromJson(authHttp.K(), JsonObject.class);
         return Pair.of(jsonObject.get("access_token").getAsString(), jsonObject.get("refresh_token").getAsString());
      } else {
         throw new IllegalArgumentException("refreshToken response: " + authHttp.s());
      }
   }

   public static String x(String string) throws IllegalArgumentException, Exception {
      AuthHttp authHttp = new AuthHttp("https://user.auth.xboxlive.com/user/authenticate");
      authHttp.z("Content-Type", "application/json");
      authHttp.z("Accept", "application/json");
      JsonObject jsonObject = new JsonObject();
      JsonObject jsonObject2 = new JsonObject();
      jsonObject2.addProperty("AuthMethod", "RPS");
      jsonObject2.addProperty("SiteName", "user.auth.xboxlive.com");
      jsonObject2.addProperty("RpsTicket", "d=" + string);
      jsonObject.add("Properties", jsonObject2);
      jsonObject.addProperty("RelyingParty", "http://auth.xboxlive.com");
      jsonObject.addProperty("TokenType", "JWT");
      authHttp.C(jsonObject.toString());
      if (authHttp.s() >= 200 && authHttp.s() < 300) {
         return ConfigFiles.V.fromJson(authHttp.K(), JsonObject.class).get("Token").getAsString();
      } else {
         throw new IllegalArgumentException("authXBL response: " + authHttp.s());
      }
   }

   public static Pair<String, String> Q(String string) throws AuthException, IllegalArgumentException, Exception {
      AuthHttp authHttp = new AuthHttp("https://xsts.auth.xboxlive.com/xsts/authorize");
      authHttp.z("Content-Type", "application/json");
      authHttp.z("Accept", "application/json");
      JsonObject jsonObject = new JsonObject();
      JsonObject jsonObject2 = new JsonObject();
      JsonArray jsonArray = new JsonArray();
      JsonPrimitive jsonPrimitive = new JsonPrimitive(string);
      jsonArray.add(jsonPrimitive);
      jsonObject2.add("UserTokens", jsonArray);
      jsonObject2.addProperty("SandboxId", "RETAIL");
      jsonObject.add("Properties", jsonObject2);
      jsonObject.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
      jsonObject.addProperty("TokenType", "JWT");
      authHttp.C(jsonObject.toString());
      if (authHttp.s() == 401) {
         throw new AuthException("ias.msauth.error.noxbox");
      } else if (authHttp.s() >= 200 && authHttp.s() < 300) {
         JsonObject jsonObject3 = ConfigFiles.V.fromJson(authHttp.K(), JsonObject.class);
         return Pair.of(
            jsonObject3.get("Token").getAsString(), jsonObject3.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString()
         );
      } else {
         throw new IllegalArgumentException("authXSTS response: " + authHttp.s());
      }
   }

   public static String P(String string, String string2) throws IllegalArgumentException, Exception {
      AuthHttp authHttp = new AuthHttp("https://api.minecraftservices.com/authentication/login_with_xbox");
      authHttp.z("Content-Type", "application/json");
      authHttp.z("Accept", "application/json");
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("identityToken", "XBL3.0 x=" + string + ";" + string2);
      authHttp.C(jsonObject.toString());
      if (authHttp.s() >= 200 && authHttp.s() < 300) {
         return ConfigFiles.V.fromJson(authHttp.K(), JsonObject.class).get("access_token").getAsString();
      } else {
         throw new IllegalArgumentException("authMinecraft response: " + authHttp.s());
      }
   }

   public static void I_void(String string) throws AuthException, IllegalArgumentException, Exception {
      AuthHttp authHttp = new AuthHttp("https://api.minecraftservices.com/entitlements/mcstore");
      authHttp.z("Authorization", "Bearer " + string);
      authHttp.Y();
      if (authHttp.s() >= 200 && authHttp.s() < 300) {
         if (ConfigFiles.V.fromJson(authHttp.K(), JsonObject.class).getAsJsonArray("items").size() == 0) {
            throw new AuthException("ias.msauth.error.gamenotowned");
         }
      } else {
         throw new IllegalArgumentException("checkGameOwnership response: " + authHttp.s());
      }
   }

   public static Pair<UUID, String> m(String string) throws IllegalArgumentException, Exception {
      AuthHttp authHttp = new AuthHttp("https://api.minecraftservices.com/minecraft/profile");
      authHttp.z("Authorization", "Bearer " + string);
      authHttp.Y();
      if (authHttp.s() >= 200 && authHttp.s() < 300) {
         JsonObject jsonObject = ConfigFiles.V.fromJson(authHttp.K(), JsonObject.class);
         return Pair.of(UUIDTypeAdapter.fromString(jsonObject.get("id").getAsString()), jsonObject.get("name").getAsString());
      } else {
         throw new IllegalArgumentException("getProfile response: " + authHttp.s());
      }
   }

   public static MojangAuth d(String string, String string2) throws AuthException, IOException {
      AuthHttp authHttp = new AuthHttp("https://authserver.mojang.com/authenticate");
      authHttp.z("Content-Type", "application/json");
      UUID uuid = UUID.randomUUID();
      JsonObject jsonObject = new JsonObject();
      JsonObject jsonObject2 = new JsonObject();
      jsonObject2.addProperty("name", "Minecraft");
      jsonObject2.addProperty("version", 1);
      jsonObject.add("agent", jsonObject2);
      jsonObject.addProperty("username", string);
      jsonObject.addProperty("password", string2);
      jsonObject.addProperty("clientToken", UUIDTypeAdapter.fromUUID(uuid));
      authHttp.C(jsonObject.toString());
      if (authHttp.s() >= 200 && authHttp.s() < 300) {
         JsonObject jsonObject3 = new Gson().fromJson(authHttp.K(), JsonObject.class);
         String string3 = jsonObject3.get("accessToken").getAsString();
         UUID uuid2 = UUIDTypeAdapter.fromString(jsonObject3.get("clientToken").getAsString());
         if (!uuid2.equals(uuid)) {
            throw new AuthException("ias.mojauth.unknown", "Response token " + uuid2 + " is not equals to sent token " + uuid);
         }

         UUID uuid3 = UUIDTypeAdapter.fromString(jsonObject3.getAsJsonObject("selectedProfile").get("id").getAsString());
         String string4 = jsonObject3.getAsJsonObject("selectedProfile").get("name").getAsString();
         return new MojangAuth(string4, string3, uuid, uuid3);
      } else {
         new JsonObject();
         String string5 = authHttp.q();

         JsonObject jsonObject4;
         try {
            jsonObject4 = new Gson().fromJson(string5, JsonObject.class);
         } catch (Exception exception) {
            if (string5.toLowerCase().contains("cloudfront")) {
               throw new AuthException("ias.mojauth.toofast", string5);
            }

            throw new AuthException("ias.mojauth.unknown", string5);
         }

         String string6 = jsonObject4.get("error").getAsString();
         if (string6.equals("ForbiddenOperationException")) {
            String string7 = jsonObject4.get("errorMessage").getAsString();
            if (string7.equals("Invalid credentials. Invalid username or password.")) {
               throw new AuthException("ias.mojauth.invalidcreds", jsonObject4.toString());
            }

            if (string7.equals("Invalid credentials.")) {
               throw new AuthException("ias.mojauth.toofast", jsonObject4.toString());
            }
         }

         if (string6.equals("ResourceException")) {
            throw new AuthException("ias.mojauth.migrated", jsonObject4.toString());
         } else {
            throw new AuthException("ias.mojauth.unknown", jsonObject4.toString());
         }
      }
   }
}
