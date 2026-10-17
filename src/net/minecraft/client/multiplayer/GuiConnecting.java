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

                  inetAddress = GuiConnecting.accessResolveAddress(GuiConnecting.this, ip);
                  GuiConnecting.this.networkManager = NetworkManager.createNetworkManagerAndConnect(
                     inetAddress, port, GuiConnecting.accessMcForNativeTransport(GuiConnecting.this).gameSettings.isUsingNativeTransport()
                  );
                  GuiConnecting.this.networkManager
                     .setNetHandler(
                        new NetHandlerLoginClient(
                           GuiConnecting.this.networkManager, GuiConnecting.accessMcForLoginHandler(GuiConnecting.this), GuiConnecting.this.previousGuiScreen
                        )
                     );
                  GuiConnecting.this.networkManager.sendPacket(new C00Handshake(47, ip, port, EnumConnectionState.LOGIN));
                  GuiConnecting.this.networkManager
                     .sendPacket(new C00PacketLoginStart(GuiConnecting.accessMcForSessionProfile(GuiConnecting.this).getSession().getProfile()));
               } catch (UnknownHostException unknownHostException) {
                  if (GuiConnecting.this.cancel) {
                     return;
                  }

                  GuiConnecting.logger.error("Couldn't connect to server", unknownHostException);
                  GuiConnecting.accessMcForUnknownHost(GuiConnecting.this)
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

                  GuiConnecting.accessMcForConnectError(GuiConnecting.this)
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

   private InetAddress resolveAddress(String string) throws UnknownHostException {
      VFAddressParser vfAddressParser = new VFAddressParser().parse(string);
      if (vfAddressParser.viaSuffix == null) {
         return InetAddress.getByName(string);
      }

      InetAddress inetAddress = InetAddress.getByName(vfAddressParser.serverAddress);
      return InetAddress.getByAddress(inetAddress.getHostName() + "." + vfAddressParser.viaSuffix, inetAddress.getAddress());
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
         this.drawCenteredString(this.fontRendererObj, I18n.format("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
      } else {
         this.drawCenteredString(this.fontRendererObj, I18n.format("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public static InetAddress accessResolveAddress(GuiConnecting guiConnecting, String string) throws UnknownHostException {
      return guiConnecting.resolveAddress(string);
   }

   public static Minecraft accessMcForNativeTransport(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   public static Minecraft accessMcForLoginHandler(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   public static Minecraft accessMcForSessionProfile(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   public static Minecraft accessMcForUnknownHost(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }

   public static Minecraft accessMcForConnectError(GuiConnecting guiConnecting) {
      return guiConnecting.mc;
   }
}
