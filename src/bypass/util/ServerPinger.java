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

   public void F(String string, ServerPingData serverPingData) throws UnknownHostException {
      if (string != null && !string.startsWith("0.0.0.0") && !string.isEmpty()) {
         RealmsServerAddress realmsServerAddress = RealmsServerAddress.parseString(string);
         NetworkManager networkManager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(realmsServerAddress.getHost()), realmsServerAddress.getPort(), false);
         this.j.add(networkManager);
         networkManager.setNetHandler(new ServerPingHandler(this, serverPingData, networkManager, string));

         try {
            networkManager.sendPacket(new C00Handshake(RealmsSharedConstants.e, realmsServerAddress.getHost(), realmsServerAddress.getPort(), EnumConnectionState.STATUS));
            networkManager.sendPacket(new C00PacketServerQuery());
         } catch (Throwable throwable) {
            X.error(throwable);
         }
      }
   }

   public void o() {
      synchronized (this.j) {
         Iterator iterator2 = this.j.iterator();

         while (iterator2.hasNext()) {
            NetworkManager networkManager = (NetworkManager)iterator2.next();
            if (networkManager.isChannelOpen()) {
               networkManager.processReceivedPackets();
            } else {
               iterator2.remove();
               networkManager.checkDisconnected();
            }
         }
      }
   }

   public void x() {
      synchronized (this.j) {
         Iterator iterator2 = this.j.iterator();

         while (iterator2.hasNext()) {
            NetworkManager networkManager = (NetworkManager)iterator2.next();
            if (networkManager.isChannelOpen()) {
               iterator2.remove();
               networkManager.closeChannel(new ChatComponentText("Cancelled"));
            }
         }
      }
   }

   public static Logger P() {
      return X;
   }
}
