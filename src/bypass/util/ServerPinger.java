package bypass.util;

import com.google.common.collect.Lists;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.realms.RealmsServerAddress;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPinger {
   private static final Logger X = LogManager.getLogger();
   private final List<NetworkManager> j = Collections.synchronizedList(Lists.newArrayList());
   private static final long a = 75349393174193L;

   public void F(String var1, ServerPingData var2) throws UnknownHostException {
      if (var1 != null && !var1.startsWith("0.0.0.0") && !var1.isEmpty()) {
         RealmsServerAddress var6 = RealmsServerAddress.parseString(var1);
         NetworkManager var7 = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(var6.getHost()), var6.getPort(), false);
         this.j.add(var7);
         var7.setNetHandler(new ServerPingHandler(this, var2, var7, var1));

         try {
            var7.sendPacket(new C00Handshake(RealmsSharedConstants.e, var6.getHost(), var6.getPort(), EnumConnectionState.STATUS));
            var7.sendPacket(new C00PacketServerQuery());
         } catch (Throwable var9) {
            X.error(var9);
         }
      }
   }

   public void o() {
      synchronized (this.j) {
         Iterator var5 = this.j.iterator();

         while (var5.hasNext()) {
            NetworkManager var6 = (NetworkManager)var5.next();
            if (var6.isChannelOpen()) {
               var6.processReceivedPackets();
            } else {
               var5.remove();
               var6.checkDisconnected();
            }
         }
      }
   }

   public void x() {
      synchronized (this.j) {
         Iterator var5 = this.j.iterator();

         while (var5.hasNext()) {
            NetworkManager var6 = (NetworkManager)var5.next();
            if (var6.isChannelOpen()) {
               var5.remove();
               var6.closeChannel(new ChatComponentText("Cancelled"));
            }
         }
      }
   }

   public static Logger P() {
      return X;
   }
}
