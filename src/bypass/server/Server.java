package bypass.server;

import bypass.ClientAccess;
import bypass.manager.Manager;
import bypass.util.collection.ShapedMap;
import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.multiplayer.ServerData;

public abstract class Server extends Manager<String> implements ClientAccess {
   private final ShapedMap<String, Boolean> n = new ShapedMap<>();
   private static InetAddress S;
   private final String z;
   private final String L;
   private final ServerType P;
   private static final long b = 79331104580197L;
   private static final long l = -4046336724475772929L;

   public Server(String string, String string2, ServerType serverType) {
      this.z = string;
      this.L = string2;
      this.P = serverType;

      try {
         this.N();
      } catch (UnknownHostException unknownHostException) {
      }
   }

   public abstract boolean m();

   public String m_String() {
      return this.L + "." + this.z;
   }

   public InetAddress[] o_InetAddressArray() throws UnknownHostException {
      return InetAddress.getAllByName(this.m_String());
   }

   private void N() throws UnknownHostException {
      for (InetAddress inetAddress : this.o_InetAddressArray()) {
         this.Y(inetAddress);
      }
   }

   private void Y(InetAddress inetAddress) {
      String string = inetAddress.getHostAddress();
      if ((S == null || !string.equalsIgnoreCase(S.getHostAddress())) && !string.equalsIgnoreCase("localhost") && !string.equalsIgnoreCase("127.0.0.1")) {
         if (!this.C.contains(string)) {
            this.add(string);
         }
      }
   }

   public boolean g() {
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
      if (this.n.containsKey(string) && !this.n.get(string)) {
         return false;
      }

      if (!string.isEmpty() && string.contains(this.L) && (S == null || !string.contains(S.getHostName()) && !string.contains(S.getHostAddress()))) {
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

            if (k >= 0 && k <= (int)l) {
               string = string.substring(0, j - 1);
            }
         }

         String string3 = ".";
         if (string.contains(".")) {
            int i2 = string.lastIndexOf(".") + 1;
            String string4 = string.substring(i2);
            if (!string4.equalsIgnoreCase(this.z)) {
               this.n.put(string, false);
               return false;
            } else {
               String string5 = string.substring(0, i2 - 1);
               boolean bl = string.equalsIgnoreCase(this.m_String()) || string5.endsWith("." + this.L) || this.C.contains(string5);
               this.n.put(string, bl);
               return bl && this.m();
            }
         } else {
            this.n.put(string, false);
            return false;
         }
      } else {
         this.n.put(string, false);
         return false;
      }
   }

   public ServerType y() {
      return this.P;
   }

   static {
      try {
         S = InetAddress.getLocalHost();
      } catch (UnknownHostException unknownHostException) {
      }
   }
}
