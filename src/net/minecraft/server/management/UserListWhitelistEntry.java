package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class UserListWhitelistEntry extends UserListEntry<GameProfile> {
   private static final long a = 94612710633621L;

   public UserListWhitelistEntry(GameProfile var1) {
      super(var1);
   }

   public UserListWhitelistEntry(JsonObject var1) {
      super(gameProfileFromJsonObject(var1), var1);
   }

   @Override
   public void onSerialization(JsonObject var1) {
      if (this.getValue() != null) {
         var1.addProperty("uuid", this.getValue().getId() == null ? "" : this.getValue().getId().toString());
         var1.addProperty("name", this.getValue().getName());
         super.onSerialization(var1);
      }
   }

   private static GameProfile gameProfileFromJsonObject(JsonObject var0) {
      if (var0.has("uuid") && var0.has("name")) {
         String var4 = var0.get("uuid").getAsString();

         UUID var5;
         try {
            var5 = UUID.fromString(var4);
         } catch (Throwable var7) {
            return null;
         }

         return new GameProfile(var5, var0.get("name").getAsString());
      } else {
         return null;
      }
   }
}
