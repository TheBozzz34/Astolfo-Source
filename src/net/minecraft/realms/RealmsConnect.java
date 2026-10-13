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
   private static final long a = 101331843092958L;

   public RealmsConnect(RealmsScreen var1) {
      this.onlineScreen = var1;
   }

   public void connect(final String var1, final int var2) {
      Realms.setConnectedToRealms(true);
      (new Thread("Realms-connect-task") {
            private static final long a = 105647256874816L;
            private static final long f = 492340721139843119L;

            @Override
            public void run() {
               InetAddress var4 = null;

               try {
                  var4 = InetAddress.getByName(var1);
                  if (RealmsConnect.this.aborted) {
                     return;
                  }

                  RealmsConnect.this.connection = NetworkManager.createNetworkManagerAndConnect(
                     var4, var2, Minecraft.getMinecraft().gameSettings.isUsingNativeTransport()
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

                  RealmsConnect.this.connection.sendPacket(new C00Handshake((int)f, var1, var2, EnumConnectionState.LOGIN));
                  if (RealmsConnect.this.aborted) {
                     return;
                  }

                  RealmsConnect.this.connection.sendPacket(new C00PacketLoginStart(Minecraft.getMinecraft().getSession().getProfile()));
               } catch (UnknownHostException var8) {
                  Realms.clearResourcePack();
                  if (RealmsConnect.this.aborted) {
                     return;
                  }

                  RealmsConnect.LOGGER.error("Couldn't connect to world", var8);
                  Minecraft.getMinecraft().getResourcePackRepository().clearResourcePack();
                  Realms.setScreen(
                     new DisconnectedRealmsScreen(
                        RealmsConnect.this.onlineScreen,
                        "connect.failed",
                        new ChatComponentTranslation("disconnect.genericReason", "Unknown host '" + var1 + "'")
                     )
                  );
               } catch (Exception var9) {
                  Realms.clearResourcePack();
                  if (RealmsConnect.this.aborted) {
                     return;
                  }

                  RealmsConnect.LOGGER.error("Couldn't connect to world", var9);
                  String var6 = var9.toString();
                  if (var4 != null) {
                     String var7 = var4.toString() + ":" + var2;
                     var6 = var6.replaceAll(var7, "");
                  }

                  Realms.setScreen(
                     new DisconnectedRealmsScreen(
                        RealmsConnect.this.onlineScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", var6)
                     )
                  );
               }
            }
         })
         .start();
   }

   public void l() {
      this.aborted = true;
   }

   public void E() {
      if (this.connection != null) {
         if (this.connection.isChannelOpen()) {
            this.connection.processReceivedPackets();
         } else {
            this.connection.checkDisconnected();
         }
      }
   }
}
