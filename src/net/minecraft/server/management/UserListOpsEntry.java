package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class UserListOpsEntry extends UserListEntry<GameProfile> {
   private final int permissionLevel;
   private final boolean bypassesPlayerLimit;
   private static final long a = 93586314382896L;

   public UserListOpsEntry(GameProfile var1, int var2, boolean var3) {
      super(var1);
      this.permissionLevel = var2;
      this.bypassesPlayerLimit = var3;
   }

   public UserListOpsEntry(JsonObject var1) {
      super(constructProfile(var1), var1);
      this.permissionLevel = var1.has("level") ? var1.get("level").getAsInt() : 0;
      this.bypassesPlayerLimit = var1.has("bypassesPlayerLimit") && var1.get("bypassesPlayerLimit").getAsBoolean();
   }

   public int getPermissionLevel() {
      return this.permissionLevel;
   }

   public boolean bypassesPlayerLimit() {
      return this.bypassesPlayerLimit;
   }

   @Override
   public void onSerialization(JsonObject var1) {
      if (this.getValue() != null) {
         var1.addProperty("uuid", this.getValue().getId() == null ? "" : this.getValue().getId().toString());
         var1.addProperty("name", this.getValue().getName());
         super.onSerialization(var1);
         var1.addProperty("level", this.permissionLevel);
         var1.addProperty("bypassesPlayerLimit", this.bypassesPlayerLimit);
      }
   }

   private static GameProfile constructProfile(JsonObject var0) {
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
