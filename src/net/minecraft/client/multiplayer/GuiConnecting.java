package net.minecraft.client.multiplayer;

import bypass.via.util.VFAddressParser;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiConnecting extends GuiScreen {
   private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
   private static final Logger logger = LogManager.getLogger();
   private NetworkManager networkManager;
   private boolean cancel;
   private final GuiScreen previousGuiScreen;
   private static final long a = 42915385894606L;

   public GuiConnecting(GuiScreen var1, Minecraft var2, ServerData var3) {
      this.mc = var2;
      this.previousGuiScreen = var1;
      ServerAddress var4 = ServerAddress.fromString(var3.serverIP);
      var2.loadWorld((WorldClient)null);
      var2.setServerData(var3);
      this.connect(var4.getIP(), var4.getPort());
   }

   public GuiConnecting(GuiScreen var1, Minecraft var2, String var3, int var4) {
      this.mc = var2;
      this.previousGuiScreen = var1;
      var2.loadWorld((WorldClient)null);
      this.connect(var3, var4);
   }

   private void connect(final String var1, final int var2) {
      logger.info("Connecting to " + var1 + ", " + var2);
      (new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
            private static final long a = 47869118792322L;
            private static final long f = 1724232281884721199L;

            @Override
            public void run() {
               InetAddress var4 = null;

               try {
                  if (GuiConnecting.this.cancel) {
                     return;
                  }

                  var4 = GuiConnecting.w(GuiConnecting.this, var1);
                  GuiConnecting.this.networkManager = NetworkManager.createNetworkManagerAndConnect(
                     var4, var2, GuiConnecting.T(GuiConnecting.this).gameSettings.isUsingNativeTransport()
                  );
                  GuiConnecting.this.networkManager
                     .setNetHandler(
                        new NetHandlerLoginClient(GuiConnecting.this.networkManager, GuiConnecting.Z(GuiConnecting.this), GuiConnecting.this.previousGuiScreen)
                     );
                  GuiConnecting.this.networkManager.sendPacket(new C00Handshake((int)f, var1, var2, EnumConnectionState.LOGIN));
                  GuiConnecting.this.networkManager.sendPacket(new C00PacketLoginStart(GuiConnecting.F(GuiConnecting.this).getSession().getProfile()));
               } catch (UnknownHostException var8) {
                  if (GuiConnecting.this.cancel) {
                     return;
                  }

                  GuiConnecting.logger.error("Couldn't connect to server", var8);
                  GuiConnecting.i(GuiConnecting.this)
                     .displayGuiScreen(
                        new GuiDisconnected(
                           GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Unknown host")
                        )
                     );
               } catch (Exception var9) {
                  if (GuiConnecting.this.cancel) {
                     return;
                  }

                  GuiConnecting.logger.error("Couldn't connect to server", var9);
                  String var6 = var9.toString();
                  if (var4 != null) {
                     String var7 = var4.toString() + ":" + var2;
                     var6 = var6.replaceAll(var7, "");
                  }

                  GuiConnecting.U(GuiConnecting.this)
                     .displayGuiScreen(
                        new GuiDisconnected(
                           GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", var6)
                        )
                     );
               }
            }
         })
         .start();
   }

   private InetAddress a(String var1) throws UnknownHostException {
      VFAddressParser var5 = new VFAddressParser().c(var1);
      if (var5.m == null) {
         return InetAddress.getByName(var1);
      }

      InetAddress var6 = InetAddress.getByName(var5.p);
      return InetAddress.getByAddress(var6.getHostName() + "." + var5.m, var6.getAddress());
   }

   @Override
   public void updateScreen() {
      if (this.networkManager != null) {
         if (this.networkManager.isChannelOpen()) {
            this.networkManager.processReceivedPackets();
         } else {
            this.networkManager.checkDisconnected();
         }
      }
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.id == 0) {
         this.cancel = true;
         if (this.networkManager != null) {
            this.networkManager.closeChannel(new ChatComponentText("Aborted"));
         }

         this.mc.displayGuiScreen(this.previousGuiScreen);
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      if (this.networkManager == null) {
         this.d(this.fontRendererObj, I18n.format("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
      } else {
         this.d(this.fontRendererObj, I18n.format("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
      }

      super.drawScreen(var1, var2, var3);
   }

   public static InetAddress w(GuiConnecting var0, String var1) throws UnknownHostException {
      return var0.a(var1);
   }

   public static Minecraft T(GuiConnecting var0) {
      return var0.mc;
   }

   public static Minecraft Z(GuiConnecting var0) {
      return var0.mc;
   }

   public static Minecraft F(GuiConnecting var0) {
      return var0.mc;
   }

   public static Minecraft i(GuiConnecting var0) {
      return var0.mc;
   }

   public static Minecraft U(GuiConnecting var0) {
      return var0.mc;
   }
}
