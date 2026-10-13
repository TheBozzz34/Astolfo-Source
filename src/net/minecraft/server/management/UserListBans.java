package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListBans extends UserList<GameProfile, UserListBansEntry> {
   private static final long a = 120848512167847L;

   public UserListBans(File var1) {
      super(var1);
   }

   @Override
   public UserListEntry<GameProfile> createEntry(JsonObject var1) {
      return new UserListBansEntry(var1);
   }

   public boolean isBanned(GameProfile var1) {
      return this.hasEntry(var1);
   }

   @Override
   public String[] getKeys() {
      String[] var4 = new String[this.getValues().size()];
      int var5 = 0;

      for (UserListBansEntry var7 : this.getValues().values()) {
         var4[var5++] = var7.getValue().getName();
      }

      return var4;
   }

   public String getObjectKey(GameProfile var1) {
      return var1.getId().toString();
   }

   public GameProfile isUsernameBanned(String var1) {
      for (UserListBansEntry var6 : this.getValues().values()) {
         if (var1.equalsIgnoreCase(var6.getValue().getName())) {
            return var6.getValue();
         }
      }

      return null;
   }
}
