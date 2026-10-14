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

   public GuiConnecting(GuiScreen guiScreen, Minecraft mcIn, ServerData serverData) {
      this.mc = mcIn;
      this.previousGuiScreen = guiScreen;
      ServerAddress serverAddress = ServerAddress.fromString(serverData.serverIP);
      mcIn.loadWorld((WorldClient)null);
      mcIn.setServerData(serverData);
      this.connect(serverAddress.getIP(), serverAddress.getPort());
   }

   public GuiConnecting(GuiScreen guiScreen, Minecraft mcIn, String hostName, int port) {
      this.mc = mcIn;
      this.previousGuiScreen = guiScreen;
      mcIn.loadWorld((WorldClient)null);
      this.connect(hostName, port);
   }

   private void connect(final String ip, final int port) {
      logger.info("Connecting to " + ip + ", " + port);
      (new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
            @Override
            public void run() {
               InetAddress inetAddress = null;

               try {
                  if (GuiConnecting.this.cancel) {
                     return;
                  }

                  inetAddress = GuiConnecting.w(GuiConnecting.this, ip);
                  GuiConnecting.this.networkManager = NetworkManager.createNetworkManagerAndConnect(
                     inetAddress, port, GuiConnecting.T(GuiConnecting.this).gameSettings.isUsingNativeTransport()
                  );
                  GuiConnecting.this.networkManager
                     .setNetHandler(
                        new NetHandlerLoginClient(GuiConnecting.this.networkManager, GuiConnecting.Z(GuiConnecting.this), GuiConnecting.this.previousGuiScreen)
                     );
                  GuiConnecting.this.networkManager.sendPacket(new C00Handshake(47, ip, port, EnumConnectionState.LOGIN));
                  GuiConnecting.this.networkManager.sendPacket(new C00PacketLoginStart(GuiConnecting.F(GuiConnecting.this).getSession().getProfile()));
               } catch (UnknownHostException unknownHostException) {
                  if (GuiConnecting.this.cancel) {
                     return;
                  }

                  GuiConnecting.logger.error("Couldn't connect to server", unknownHostException);
                  GuiConnecting.i(GuiConnecting.this)
                     .displayGuiScreen(
                        new GuiDisconnected(
                           GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Unknown host")
                        )
                     );
               } catch (Exception exception) {
                  if (GuiConnecting.this.cancel) {
                     return;
                  }

                  GuiConnecting.logger.error("Couldn't connect to server", exception);
                  String string = exception.toString();
                  if (inetAddress != null) {
                     String string2 = inetAddress.toString() + ":" + port;
                     string = string.replaceAll(string2, "");
                  }

                  GuiConnecting.U(GuiConnecting.this)
                     .displayGuiScreen(
                        new GuiDisconnected(
                           GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", string)
                        )
                     );
               }
            }
         })
         .start();
   }

   private InetAddress a(String string) throws UnknownHostException {
      VFAddressParser vfAddressParser = new VFAddressParser().c(string);
      if (vfAddressParser.m == null) {
         return InetAddress.getByName(string);
      }

      InetAddress inetAddress = InetAddress.getByName(vfAddressParser.p);
      return InetAddress.getByAddress(inetAddress.getHostName() + "." + vfAddressParser.m, inetAddress.getAddress());
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
   public void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.cancel = true;
         if (this.networkManager != null) {
            this.networkManager.closeChannel(new ChatComponentText("Aborted"));
         }

         this.mc.displayGuiScreen(this.previousGuiScreen);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      if (this.networkManager == null) {
         this.d(this.fontRendererObj, I18n.format("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
      } else {
         this.d(this.fontRendererObj, I18n.format("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public static InetAddress w(GuiConnecting guiConnecting, String string) throws UnknownHostException {
      return guiConnecting.a(string);
   }

   public static Minecraft T(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   public static Minecraft Z(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   public static Minecraft F(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   public static Minecraft i(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   public static Minecraft U(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }
}
