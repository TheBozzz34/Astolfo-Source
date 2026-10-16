package bypass.server;

import bypass.ClientAccess;
import bypass.manager.Manager;
import bypass.util.collection.ShapedMap;
import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.multiplayer.ServerData;

public abstract class Server extends Manager<String> implements ClientAccess {
   private final ShapedMap<String, Boolean> matchCache = new ShapedMap<>();
   private static InetAddress localAddress;
   private final String tld;
   private final String domain;
   private final ServerType type;

   public Server(String string, String string2, ServerType serverType) {
      this.tld = string;
      this.domain = string2;
      this.type = serverType;

      try {
         this.resolveAddresses();
      } catch (UnknownHostException unknownHostException) {
      }
   }

   public abstract boolean isBrandValid();

   public String m_String() {
      return this.domain + "." + this.tld;
   }

   public InetAddress[] o_InetAddressArray() throws UnknownHostException {
      return InetAddress.getAllByName(this.m_String());
   }

   private void resolveAddresses() throws UnknownHostException {
      for (InetAddress inetAddress : this.o_InetAddressArray()) {
         this.addAddress(inetAddress);
      }
   }

   private void addAddress(InetAddress inetAddress) {
      String string = inetAddress.getHostAddress();
      if ((localAddress == null || !string.equalsIgnoreCase(localAddress.getHostAddress()))
         && !string.equalsIgnoreCase("localhost")
         && !string.equalsIgnoreCase("127.0.0.1")) {
         if (!this.items.contains(string)) {
            this.add(string);
         }
      }
   }

   public boolean isCurrentServer() {
      if (mc.isSingleplayer()) {
         return false;
      }

      if (mc.isIntegratedServerRunning()) {
         return false;
      }

      ServerData serverData = mc.getCurrentServerData();
      if (serverData == null) {
         return false;
      }

      String string = serverData.serverIP.toLowerCase();
      if (this.matchCache.containsKey(string) && !this.matchCache.get(string)) {
         return false;
      }

      if (!string.isEmpty()
         && string.contains(this.domain)
         && (localAddress == null || !string.contains(localAddress.getHostName()) && !string.contains(localAddress.getHostAddress()))) {
         int i = string.length();
         if (string.contains(":") && Character.isDigit(string.charAt(i - 1))) {
            int j = string.lastIndexOf(":") + 1;
            String string2 = string.substring(j);

            int k;
            try {
               k = Integer.parseInt(string2);
            } catch (Exception exception) {
               k = -1;
            }

            if (k >= 0 && k <= 65535) {
               string = string.substring(0, j - 1);
            }
         }

         String string3 = ".";
         if (string.contains(".")) {
            int l = string.lastIndexOf(".") + 1;
            String string4 = string.substring(l);
            if (!string4.equalsIgnoreCase(this.tld)) {
               this.matchCache.put(string, false);
               return false;
            } else {
               String string5 = string.substring(0, l - 1);
               boolean bl = string.equalsIgnoreCase(this.m_String()) || string5.endsWith("." + this.domain) || this.items.contains(string5);
               this.matchCache.put(string, bl);
               return bl && this.isBrandValid();
            }
         } else {
            this.matchCache.put(string, false);
            return false;
         }
      } else {
         this.matchCache.put(string, false);
         return false;
      }
   }

   public ServerType getType() {
      return this.type;
   }

   static {
      try {
         localAddress = InetAddress.getLocalHost();
      } catch (UnknownHostException unknownHostException) {
      }
   }
}
