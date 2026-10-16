package bypass.hypixel;

import bypass.util.collection.ShapedMap;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public final class HypixelAddressCheck {
   private static final ShapedMap<String, Boolean> serverIpCache = new ShapedMap<>();
   private static final List<String> hypixelIps = new ArrayList<>();
   private static final Minecraft mc = Minecraft.getMinecraft();
   private static InetAddress localHost;

   public static void addHypixelAddress(InetAddress inetAddress) {
      String string = inetAddress.getHostAddress();
      if ((localHost == null || !string.equalsIgnoreCase(localHost.getHostAddress()))
         && !string.equalsIgnoreCase("localhost")
         && !string.equalsIgnoreCase("127.0.0.1")) {
         if (!hypixelIps.contains(string)) {
            hypixelIps.add(string);
         }
      }
   }

   public static boolean isOnHypixel() {
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
      if (serverIpCache.containsKey(string) && !serverIpCache.get(string)) {
         return false;
      }

      if (!string.isEmpty()
         && string.contains("hypixel")
         && (localHost == null || !string.contains(localHost.getHostName()) && !string.contains(localHost.getHostAddress()))) {
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
            if (!string4.equalsIgnoreCase("net")) {
               serverIpCache.put(string, false);
               return false;
            } else {
               String string5 = string.substring(0, l - 1);
               boolean bl = string.equalsIgnoreCase("hypixel.net") || string5.endsWith(".hypixel") || hypixelIps.contains(string5);
               serverIpCache.put(string, bl);
               return bl;
            }
         } else {
            serverIpCache.put(string, false);
            return false;
         }
      } else {
         serverIpCache.put(string, false);
         return false;
      }
   }

   static {
      try {
         localHost = InetAddress.getLocalHost();
      } catch (UnknownHostException unknownHostException) {
      }

      try {
         addHypixelAddress(InetAddress.getByName("hypixel.net"));
      } catch (UnknownHostException unknownHostException2) {
      }

      try {
         InetAddress[] inetAddresss = InetAddress.getAllByName("hypixel.net");

         for (InetAddress inetAddress : inetAddresss) {
            addHypixelAddress(inetAddress);
         }
      } catch (UnknownHostException unknownHostException3) {
      }
   }
}
