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
   private static final long a = 43525299494244L;
   private static final long f = 2277682708245184511L;

   public static void C(InetAddress var0) {
      String var3 = var0.getHostAddress();
      if ((F == null || !var3.equalsIgnoreCase(F.getHostAddress())) && !var3.equalsIgnoreCase("localhost") && !var3.equalsIgnoreCase("127.0.0.1")) {
         if (!P.contains(var3)) {
            P.add(var3);
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

      ServerData var3 = i.getCurrentServerData();
      if (var3 == null) {
         return false;
      }

      String var4 = var3.serverIP.toLowerCase();
      if (X.containsKey(var4) && !X.get(var4)) {
         return false;
      }

      if (!var4.isEmpty() && var4.contains("hypixel") && (F == null || !var4.contains(F.getHostName()) && !var4.contains(F.getHostAddress()))) {
         int var6 = var4.length();
         if (var4.contains(":") && Character.isDigit(var4.charAt(var6 - 1))) {
            int var7 = var4.lastIndexOf(":") + 1;
            String var8 = var4.substring(var7);

            int var9;
            try {
               var9 = Integer.parseInt(var8);
            } catch (Exception var12) {
               var9 = -1;
            }

            if (var9 >= 0 && var9 <= (int)f) {
               var4 = var4.substring(0, var7 - 1);
            }
         }

         String var13 = ".";
         if (var4.contains(".")) {
            int var14 = var4.lastIndexOf(".") + 1;
            String var15 = var4.substring(var14);
            if (!var15.equalsIgnoreCase("net")) {
               X.put(var4, false);
               return false;
            } else {
               String var10 = var4.substring(0, var14 - 1);
               boolean var11 = var4.equalsIgnoreCase("hypixel.net") || var10.endsWith(".hypixel") || P.contains(var10);
               X.put(var4, var11);
               return var11;
            }
         } else {
            X.put(var4, false);
            return false;
         }
      } else {
         X.put(var4, false);
         return false;
      }
   }

   static {
      try {
         F = InetAddress.getLocalHost();
      } catch (UnknownHostException var22) {
      }

      try {
         C(InetAddress.getByName("hypixel.net"));
      } catch (UnknownHostException var21) {
      }

      try {
         InetAddress[] var16 = InetAddress.getAllByName("hypixel.net");

         for (InetAddress var20 : var16) {
            C(var20);
         }
      } catch (UnknownHostException var23) {
      }
   }
}
