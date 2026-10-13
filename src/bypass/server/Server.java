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

   public Server(String var1, String var2, ServerType var3) {
      this.z = var1;
      this.L = var2;
      this.P = var3;

      try {
         this.N();
      } catch (UnknownHostException var5) {
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
      for (InetAddress var7 : this.o_InetAddressArray()) {
         this.Y(var7);
      }
   }

   private void Y(InetAddress var1) {
      String var5 = var1.getHostAddress();
      if ((S == null || !var5.equalsIgnoreCase(S.getHostAddress())) && !var5.equalsIgnoreCase("localhost") && !var5.equalsIgnoreCase("127.0.0.1")) {
         if (!this.C.contains(var5)) {
            this.add(var5);
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

      ServerData var4 = mc.getCurrentServerData();
      if (var4 == null) {
         return false;
      }

      String var5 = var4.serverIP.toLowerCase();
      if (this.n.containsKey(var5) && !this.n.get(var5)) {
         return false;
      }

      if (!var5.isEmpty() && var5.contains(this.L) && (S == null || !var5.contains(S.getHostName()) && !var5.contains(S.getHostAddress()))) {
         int var7 = var5.length();
         if (var5.contains(":") && Character.isDigit(var5.charAt(var7 - 1))) {
            int var8 = var5.lastIndexOf(":") + 1;
            String var9 = var5.substring(var8);

            int var10;
            try {
               var10 = Integer.parseInt(var9);
            } catch (Exception var13) {
               var10 = -1;
            }

            if (var10 >= 0 && var10 <= (int)l) {
               var5 = var5.substring(0, var8 - 1);
            }
         }

         String var14 = ".";
         if (var5.contains(".")) {
            int var15 = var5.lastIndexOf(".") + 1;
            String var16 = var5.substring(var15);
            if (!var16.equalsIgnoreCase(this.z)) {
               this.n.put(var5, false);
               return false;
            } else {
               String var11 = var5.substring(0, var15 - 1);
               boolean var12 = var5.equalsIgnoreCase(this.m_String()) || var11.endsWith("." + this.L) || this.C.contains(var11);
               this.n.put(var5, var12);
               return var12 && this.m();
            }
         } else {
            this.n.put(var5, false);
            return false;
         }
      } else {
         this.n.put(var5, false);
         return false;
      }
   }

   public ServerType y() {
      return this.P;
   }

   static {
      try {
         S = InetAddress.getLocalHost();
      } catch (UnknownHostException var17) {
      }
   }
}
