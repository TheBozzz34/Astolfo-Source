package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListOps extends UserList<GameProfile, UserListOpsEntry> {
   private static final long a = 31159359404612L;

   public UserListOps(File var1) {
      super(var1);
   }

   @Override
   public UserListEntry<GameProfile> createEntry(JsonObject var1) {
      return new UserListOpsEntry(var1);
   }

   @Override
   public String[] getKeys() {
      String[] var4 = new String[this.getValues().size()];
      int var5 = 0;

      for (UserListOpsEntry var7 : this.getValues().values()) {
         var4[var5++] = var7.getValue().getName();
      }

      return var4;
   }

   public boolean bypassesPlayerLimit(GameProfile var1) {
      UserListOpsEntry var5 = this.getEntry(var1);
      return var5 != null ? var5.bypassesPlayerLimit() : false;
   }

   public String getObjectKey(GameProfile var1) {
      return var1.getId().toString();
   }

   public GameProfile getGameProfileFromName(String var1) {
      for (UserListOpsEntry var6 : this.getValues().values()) {
         if (var1.equalsIgnoreCase(var6.getValue().getName())) {
            return var6.getValue();
         }
      }

      return null;
   }
}
