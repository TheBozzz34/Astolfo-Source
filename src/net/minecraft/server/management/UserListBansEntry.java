package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.UUID;

public class UserListBansEntry extends BanEntry<GameProfile> {
   private static final long b = 16197147225991L;

   public UserListBansEntry(GameProfile var1) {
      this(var1, (Date)null, (String)null, (Date)null, (String)null);
   }

   public UserListBansEntry(GameProfile var1, Date var2, String var3, Date var4, String var5) {
      super(var1, var4, var3, var4, var5);
   }

   public UserListBansEntry(JsonObject var1) {
      super(toGameProfile(var1), var1);
   }

   @Override
   public void onSerialization(JsonObject var1) {
      if (this.getValue() != null) {
         var1.addProperty("uuid", this.getValue().getId() == null ? "" : this.getValue().getId().toString());
         var1.addProperty("name", this.getValue().getName());
         super.onSerialization(var1);
      }
   }

   private static GameProfile toGameProfile(JsonObject var0) {
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
