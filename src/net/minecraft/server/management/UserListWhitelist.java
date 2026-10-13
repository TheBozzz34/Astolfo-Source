package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListWhitelist extends UserList<GameProfile, UserListWhitelistEntry> {
   private static final long a = 32916172508322L;

   public UserListWhitelist(File var1) {
      super(var1);
   }

   @Override
   public UserListEntry<GameProfile> createEntry(JsonObject var1) {
      return new UserListWhitelistEntry(var1);
   }

   @Override
   public String[] getKeys() {
      String[] var4 = new String[this.getValues().size()];
      int var5 = 0;

      for (UserListWhitelistEntry var7 : this.getValues().values()) {
         var4[var5++] = var7.getValue().getName();
      }

      return var4;
   }

   public String getObjectKey(GameProfile var1) {
      return var1.getId().toString();
   }

   public GameProfile getBannedProfile(String var1) {
      for (UserListWhitelistEntry var6 : this.getValues().values()) {
         if (var1.equalsIgnoreCase(var6.getValue().getName())) {
            return var6.getValue();
         }
      }

      return null;
   }
}
