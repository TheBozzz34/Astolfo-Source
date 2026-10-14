package bypass.hypixel;

import bypass.util.collection.ShapedMap;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public final class HypixelAddressCheck {
   private static final ShapedMap<String, Boolean> X = new ShapedMap<>();
   private static final List<String> P = new ArrayList<>();
   private static final Minecraft i = Minecraft.getMinecraft();
   private static InetAddress F;

   public static void C(InetAddress inetAddress) {
      String string = inetAddress.getHostAddress();
      if ((F == null || !string.equalsIgnoreCase(F.getHostAddress())) && !string.equalsIgnoreCase("localhost") && !string.equalsIgnoreCase("127.0.0.1")) {
         if (!P.contains(string)) {
            P.add(string);
         }
      }
   }

   public static boolean G() {
      if (i.isSingleplayer()) {
         return false;
      }

      if (i.isIntegratedServerRunning()) {
         return false;
      }

      ServerData serverData = i.getCurrentServerData();
      if (serverData == null) {
         return false;
      }

      String string = serverData.serverIP.toLowerCase();
      if (X.containsKey(string) && !X.get(string)) {
         return false;
      }

      if (!string.isEmpty() && string.contains("hypixel") && (F == null || !string.contains(F.getHostName()) && !string.contains(F.getHostAddress()))) {
         int j = string.length();
         if (string.contains(":") && Character.isDigit(string.charAt(j - 1))) {
            int k = string.lastIndexOf(":") + 1;
            String string2 = string.substring(k);

            int l;
            try {
               l = Integer.parseInt(string2);
            } catch (Exception exception) {
               l = -1;
            }

            if (l >= 0 && l <= 65535) {
               string = string.substring(0, k - 1);
            }
         }

         String string3 = ".";
         if (string.contains(".")) {
            int m = string.lastIndexOf(".") + 1;
            String string4 = string.substring(m);
            if (!string4.equalsIgnoreCase("net")) {
               X.put(string, false);
               return false;
            } else {
               String string5 = string.substring(0, m - 1);
               boolean bl = string.equalsIgnoreCase("hypixel.net") || string5.endsWith(".hypixel") || P.contains(string5);
               X.put(string, bl);
               return bl;
            }
         } else {
            X.put(string, false);
            return false;
         }
      } else {
         X.put(string, false);
         return false;
      }
   }

   static {
      try {
         F = InetAddress.getLocalHost();
      } catch (UnknownHostException unknownHostException) {
      }

      try {
         C(InetAddress.getByName("hypixel.net"));
      } catch (UnknownHostException unknownHostException2) {
      }

      try {
         InetAddress[] inetAddresss = InetAddress.getAllByName("hypixel.net");

         for (InetAddress inetAddress : inetAddresss) {
            C(inetAddress);
         }
      } catch (UnknownHostException unknownHostException3) {
      }
   }
}
