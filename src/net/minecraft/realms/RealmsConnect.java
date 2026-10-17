package net.minecraft.realms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsConnect {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen onlineScreen;
   private volatile boolean aborted = false;
   private NetworkManager connection;

   public RealmsConnect(RealmsScreen realmsScreen) {
      this.onlineScreen = realmsScreen;
   }

   public void connect(final String string, final int i) {
      Realms.setConnectedToRealms(true);
      (new Thread("Realms-connect-task") {
            @Override
            public void run() {
               InetAddress inetAddress = null;

               try {
                  inetAddress = InetAddress.getByName(string);
                  if (RealmsConnect.this.aborted) {
                     return;
                  }

                  RealmsConnect.this.connection = NetworkManager.createNetworkManagerAndConnect(
                     inetAddress, i, Minecraft.getMinecraft().gameSettings.isUsingNativeTransport()
                  );
                  if (RealmsConnect.this.aborted) {
                     return;
                  }

                  RealmsConnect.this.connection
                     .setNetHandler(
                        new NetHandlerLoginClient(RealmsConnect.this.connection, Minecraft.getMinecraft(), RealmsConnect.this.onlineScreen.getProxy())
                     );
                  if (RealmsConnect.this.aborted) {
                     return;
                  }

                  RealmsConnect.this.connection.sendPacket(new C00Handshake(47, string, i, EnumConnectionState.LOGIN));
                  if (RealmsConnect.this.aborted) {
                     return;
                  }

                  RealmsConnect.this.connection.sendPacket(new C00PacketLoginStart(Minecraft.getMinecraft().getSession().getProfile()));
               } catch (UnknownHostException unknownHostException) {
                  Realms.clearResourcePack();
                  if (RealmsConnect.this.aborted) {
                     return;
                  }

                  RealmsConnect.LOGGER.error("Couldn't connect to world", unknownHostException);
                  Minecraft.getMinecraft().getResourcePackRepository().clearResourcePack();
                  Realms.setScreen(
                     new DisconnectedRealmsScreen(
                        RealmsConnect.this.onlineScreen,
                        "connect.failed",
                        new ChatComponentTranslation("disconnect.genericReason", "Unknown host '" + string + "'")
                     )
                  );
               } catch (Exception exception) {
                  Realms.clearResourcePack();
                  if (RealmsConnect.this.aborted) {
                     return;
                  }

                  RealmsConnect.LOGGER.error("Couldn't connect to world", exception);
                  String string2 = exception.toString();
                  if (inetAddress != null) {
                     String string3 = inetAddress.toString() + ":" + i;
                     string2 = string2.replaceAll(string3, "");
                  }

                  Realms.setScreen(
                     new DisconnectedRealmsScreen(
                        RealmsConnect.this.onlineScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", string2)
                     )
                  );
               }
            }
         })
         .start();
   }

   public void abort() {
      this.aborted = true;
   }

   public void tick() {
      if (this.connection != null) {
         if (this.connection.isChannelOpen()) {
            this.connection.processReceivedPackets();
         } else {
            this.connection.checkDisconnected();
         }
      }
   }
}
