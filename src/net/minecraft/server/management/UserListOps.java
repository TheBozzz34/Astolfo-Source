package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListOps extends UserList<GameProfile, UserListOpsEntry> {
   public UserListOps(File saveFile) {
      super(saveFile);
   }

   @Override
   public UserListEntry<GameProfile> createEntry(JsonObject entryData) {
      return new UserListOpsEntry(entryData);
   }

   @Override
   public String[] getKeys() {
      String[] strings = new String[this.getValues().size()];
      int i = 0;

      for (UserListOpsEntry userListOpsEntry : this.getValues().values()) {
         strings[i++] = userListOpsEntry.getValue().getName();
      }

      return strings;
   }

   public boolean bypassesPlayerLimit(GameProfile profile) {
      UserListOpsEntry userListOpsEntry = this.getEntry(profile);
      return userListOpsEntry != null ? userListOpsEntry.bypassesPlayerLimit() : false;
   }

   public String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }

   public GameProfile getGameProfileFromName(String username) {
      for (UserListOpsEntry userListOpsEntry : this.getValues().values()) {
         if (username.equalsIgnoreCase(userListOpsEntry.getValue().getName())) {
            return userListOpsEntry.getValue();
         }
      }

      return null;
   }
}
