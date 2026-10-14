package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListBans extends UserList<GameProfile, UserListBansEntry> {
   public UserListBans(File bansFile) {
      super(bansFile);
   }

   @Override
   public UserListEntry<GameProfile> createEntry(JsonObject entryData) {
      return new UserListBansEntry(entryData);
   }

   public boolean isBanned(GameProfile profile) {
      return this.hasEntry(profile);
   }

   @Override
   public String[] getKeys() {
      String[] strings = new String[this.getValues().size()];
      int i = 0;

      for (UserListBansEntry userListBansEntry : this.getValues().values()) {
         strings[i++] = userListBansEntry.getValue().getName();
      }

      return strings;
   }

   public String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }

   public GameProfile isUsernameBanned(String username) {
      for (UserListBansEntry userListBansEntry : this.getValues().values()) {
         if (username.equalsIgnoreCase(userListBansEntry.getValue().getName())) {
            return userListBansEntry.getValue();
         }
      }

      return null;
   }
}
