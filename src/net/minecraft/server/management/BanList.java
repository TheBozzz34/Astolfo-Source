package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class BanList extends UserList<String, IPBanEntry> {
   private static final long a = 132942717808443L;

   public BanList(File var1) {
      super(var1);
   }

   @Override
   public UserListEntry<String> createEntry(JsonObject var1) {
      return new IPBanEntry(var1);
   }

   public boolean isBanned(SocketAddress var1) {
      String var2 = this.addressToString(var1);
      return this.hasEntry(var2);
   }

   public IPBanEntry getBanEntry(SocketAddress var1) {
      String var2 = this.addressToString(var1);
      return this.getEntry(var2);
   }

   private String addressToString(SocketAddress var1) {
      String var5 = var1.toString();
      if (var5.contains("/")) {
         var5 = var5.substring(var5.indexOf(47) + 1);
      }

      if (var5.contains(":")) {
         var5 = var5.substring(0, var5.indexOf(58));
      }

      return var5;
   }
}
