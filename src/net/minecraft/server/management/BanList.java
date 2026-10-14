package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class BanList extends UserList<String, IPBanEntry> {
   private static final long a = 132942717808443L;

   public BanList(File bansFile) {
      super(bansFile);
   }

   @Override
   public UserListEntry<String> createEntry(JsonObject entryData) {
      return new IPBanEntry(entryData);
   }

   public boolean isBanned(SocketAddress address) {
      String string = this.addressToString(address);
      return this.hasEntry(string);
   }

   public IPBanEntry getBanEntry(SocketAddress address) {
      String string = this.addressToString(address);
      return this.getEntry(string);
   }

   private String addressToString(SocketAddress address) {
      String string = address.toString();
      if (string.contains("/")) {
         string = string.substring(string.indexOf(47) + 1);
      }

      if (string.contains(":")) {
         string = string.substring(0, string.indexOf(58));
      }

      return string;
   }
}
