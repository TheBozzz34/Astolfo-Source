package net.minecraft.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

public class ServerStatusResponse {
   private IChatComponent serverMotd;
   private ServerStatusResponse.PlayerCountData playerCount;
   private ServerStatusResponse.MinecraftProtocolVersionIdentifier protocolVersion;
   private String favicon;

   public IChatComponent getServerDescription() {
      return this.serverMotd;
   }

   public void setServerDescription(IChatComponent var1) {
      this.serverMotd = var1;
   }

   public ServerStatusResponse.PlayerCountData getPlayerCountData() {
      return this.playerCount;
   }

   public void setPlayerCountData(ServerStatusResponse.PlayerCountData var1) {
      this.playerCount = var1;
   }

   public ServerStatusResponse.MinecraftProtocolVersionIdentifier getProtocolVersionInfo() {
      return this.protocolVersion;
   }

   public void setProtocolVersionInfo(ServerStatusResponse.MinecraftProtocolVersionIdentifier var1) {
      this.protocolVersion = var1;
   }

   public void setFavicon(String var1) {
      this.favicon = var1;
   }

   public String getFavicon() {
      return this.favicon;
   }

   public static class MinecraftProtocolVersionIdentifier {
      private final String name;
      private final int protocol;

      public MinecraftProtocolVersionIdentifier(String var1, int var2) {
         this.name = var1;
         this.protocol = var2;
      }

      public String getName() {
         return this.name;
      }

      public int getProtocol() {
         return this.protocol;
      }

      public static class Serializer
         implements JsonDeserializer<ServerStatusResponse.MinecraftProtocolVersionIdentifier>,
         JsonSerializer<ServerStatusResponse.MinecraftProtocolVersionIdentifier> {
         private static final long a = 76036135667557L;

         public ServerStatusResponse.MinecraftProtocolVersionIdentifier deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            JsonObject var6 = JsonUtils.getJsonObject(var1, "version");
            return new ServerStatusResponse.MinecraftProtocolVersionIdentifier(JsonUtils.getString(var6, "name"), JsonUtils.getInt(var6, "protocol"));
         }

         public JsonElement serialize(ServerStatusResponse.MinecraftProtocolVersionIdentifier var1, Type var2, JsonSerializationContext var3) {
            JsonObject var6 = new JsonObject();
            var6.addProperty("name", var1.getName());
            var6.addProperty("protocol", var1.getProtocol());
            return var6;
         }
      }
   }

   public static class PlayerCountData {
      private final int maxPlayers;
      private final int onlinePlayerCount;
      private GameProfile[] players;

      public PlayerCountData(int var1, int var2) {
         this.maxPlayers = var1;
         this.onlinePlayerCount = var2;
      }

      public int getMaxPlayers() {
         return this.maxPlayers;
      }

      public int getOnlinePlayerCount() {
         return this.onlinePlayerCount;
      }

      public GameProfile[] getPlayers() {
         return this.players;
      }

      public void setPlayers(GameProfile[] var1) {
         this.players = var1;
      }

      public static class Serializer implements JsonDeserializer<ServerStatusResponse.PlayerCountData>, JsonSerializer<ServerStatusResponse.PlayerCountData> {
         private static final long a = 39959654111624L;

         public ServerStatusResponse.PlayerCountData deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            JsonObject var7 = JsonUtils.getJsonObject(var1, "players");
            ServerStatusResponse.PlayerCountData var8 = new ServerStatusResponse.PlayerCountData(
               JsonUtils.getInt(var7, "max"), JsonUtils.getInt(var7, "online")
            );
            if (JsonUtils.isJsonArray(var7, "sample")) {
               JsonArray var9 = JsonUtils.getJsonArray(var7, "sample");
               if (var9.size() > 0) {
                  GameProfile[] var10 = new GameProfile[var9.size()];

                  for (int var11 = 0; var11 < var10.length; var11++) {
                     JsonObject var12 = JsonUtils.getJsonObject(var9.get(var11), "player[" + var11 + "]");
                     String var13 = JsonUtils.getString(var12, "id");
                     var10[var11] = new GameProfile(UUID.fromString(var13), JsonUtils.getString(var12, "name"));
                  }

                  var8.setPlayers(var10);
               }
            }

            return var8;
         }

         public JsonElement serialize(ServerStatusResponse.PlayerCountData var1, Type var2, JsonSerializationContext var3) {
            JsonObject var7 = new JsonObject();
            var7.addProperty("max", var1.getMaxPlayers());
            var7.addProperty("online", var1.getOnlinePlayerCount());
            if (var1.getPlayers() != null && var1.getPlayers().length > 0) {
               JsonArray var8 = new JsonArray();

               for (int var9 = 0; var9 < var1.getPlayers().length; var9++) {
                  JsonObject var10 = new JsonObject();
                  UUID var11 = var1.getPlayers()[var9].getId();
                  var10.addProperty("id", var11 == null ? "" : var11.toString());
                  var10.addProperty("name", var1.getPlayers()[var9].getName());
                  var8.add(var10);
               }

               var7.add("sample", var8);
            }

            return var7;
         }
      }
   }

   public static class Serializer implements JsonDeserializer<ServerStatusResponse>, JsonSerializer<ServerStatusResponse> {
      private static final long a = 96386556883922L;

      public ServerStatusResponse deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var7 = JsonUtils.getJsonObject(var1, "status");
         ServerStatusResponse var8 = new ServerStatusResponse();
         if (var7.has("description")) {
            var8.setServerDescription(var3.deserialize(var7.get("description"), IChatComponent.class));
         }

         if (var7.has("players")) {
            var8.setPlayerCountData(var3.deserialize(var7.get("players"), ServerStatusResponse.PlayerCountData.class));
         }

         if (var7.has("version")) {
            var8.setProtocolVersionInfo(var3.deserialize(var7.get("version"), ServerStatusResponse.MinecraftProtocolVersionIdentifier.class));
         }

         if (var7.has("favicon")) {
            var8.setFavicon(JsonUtils.getString(var7, "favicon"));
         }

         return var8;
      }

      public JsonElement serialize(ServerStatusResponse var1, Type var2, JsonSerializationContext var3) {
         JsonObject var7 = new JsonObject();
         if (var1.getServerDescription() != null) {
            var7.add("description", var3.serialize(var1.getServerDescription()));
         }

         if (var1.getPlayerCountData() != null) {
            var7.add("players", var3.serialize(var1.getPlayerCountData()));
         }

         if (var1.getProtocolVersionInfo() != null) {
            var7.add("version", var3.serialize(var1.getProtocolVersionInfo()));
         }

         if (var1.getFavicon() != null) {
            var7.addProperty("favicon", var1.getFavicon());
         }

         return var7;
      }
   }
}
