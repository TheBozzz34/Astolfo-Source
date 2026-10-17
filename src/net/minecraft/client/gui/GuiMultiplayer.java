package net.minecraft.client.gui;

import bypass.gui.screen.ProxyScreen;
import bypass.via.gui.ProtocolButton;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.hypixel.api.HypixelAPI;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiMultiplayer extends GuiScreen implements GuiYesNoCallback {
   public static List<ProtocolVersion> O = new ArrayList<>();
   public static final HypixelAPI a = new HypixelAPI(UUID.fromString("3b88faf8-17e3-4a98-a1ee-ef767a6eb4b1"));
   private static final Logger logger = LogManager.getLogger();
   private final OldServerPinger oldServerPinger = new OldServerPinger();
   private GuiScreen parentScreen;
   private ServerSelectionList serverListSelector;
   private ServerList savedServerList;
   private GuiButton btnEditServer;
   private GuiButton btnSelectServer;
   private GuiButton btnDeleteServer;
   private ProtocolButton q;
   private boolean deletingServer;
   private boolean addingServer;
   private boolean editingServer;
   private boolean directConnect;
   private String hoveringText;
   private ServerData selectedServer;
   private LanServerDetector.LanServerList lanServerList;
   private LanServerDetector.ThreadLanServerFind lanServerDetector;
   private boolean initialized;
   private String M;
   private int w;
   private int K;

   public GuiMultiplayer(GuiScreen parentScreen) {
      this.parentScreen = parentScreen;
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      if (!this.initialized) {
         this.initialized = true;
         this.savedServerList = new ServerList(this.mc);
         this.savedServerList.loadServerList();
         this.lanServerList = new LanServerDetector.LanServerList();

         try {
            this.lanServerDetector = new LanServerDetector.ThreadLanServerFind(this.lanServerList);
            this.lanServerDetector.start();
         } catch (Exception exception) {
            logger.warn("Unable to start LAN server detection: " + exception.getMessage());
         }

         this.serverListSelector = new ServerSelectionList(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
         this.serverListSelector.func_148195_a(this.savedServerList);
      } else {
         this.serverListSelector.setDimensions(this.width, this.height, 32, this.height - 64);
      }

      this.createButtons();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.serverListSelector.handleMouseInput();
   }

   public void createButtons() {
      this.buttonList.add(this.btnEditServer = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.format("selectServer.edit")));
      this.buttonList.add(this.btnDeleteServer = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.format("selectServer.delete")));
      this.buttonList.add(this.btnSelectServer = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select")));
      this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.direct")));
      this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.format("selectServer.add")));
      this.buttonList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh")));
      this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel")));
      this.buttonList.add(new GuiButton(9, 3, this.height - 28, 50, 20, "Proxy"));
      this.buttonList.add(this.q = new ProtocolButton(10, this.width - 105, 5));
      this.selectServer(this.serverListSelector.func_148193_k());
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      if (this.lanServerList.getWasUpdated()) {
         List list = this.lanServerList.getLanServers();
         this.lanServerList.setWasNotUpdated();
         this.serverListSelector.func_148194_a(list);
      }

      this.oldServerPinger.pingPendingNetworks();
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      if (this.lanServerDetector != null) {
         this.lanServerDetector.interrupt();
         this.lanServerDetector = null;
      }

      this.oldServerPinger.clearPendingNetworks();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         GuiListExtended.IGuiListEntry iGuiListEntry = this.serverListSelector.func_148193_k() < 0
            ? null
            : this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k());
         if (button.id == 2 && iGuiListEntry instanceof ServerListEntryNormal) {
            String string = ((ServerListEntryNormal)iGuiListEntry).getServerData().serverName;
            if (string != null) {
               this.deletingServer = true;
               String string2 = I18n.format("selectServer.deleteQuestion");
               String string3 = "'" + string + "' " + I18n.format("selectServer.deleteWarning");
               String string4 = I18n.format("selectServer.deleteButton");
               String string5 = I18n.format("gui.cancel");
               GuiYesNo guiYesNo = new GuiYesNo(this, string2, string3, string4, string5, this.serverListSelector.func_148193_k());
               this.mc.displayGuiScreen(guiYesNo);
            }
         } else if (button.id == 1) {
            this.connectToSelected();
         } else if (button.id == 4) {
            this.directConnect = true;
            this.mc.displayGuiScreen(new GuiScreenServerList(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
         } else if (button.id == 3) {
            this.addingServer = true;
            this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
         } else if (button.id == 7 && iGuiListEntry instanceof ServerListEntryNormal) {
            this.editingServer = true;
            ServerData serverData = ((ServerListEntryNormal)iGuiListEntry).getServerData();
            this.selectedServer = new ServerData(serverData.serverName, serverData.serverIP, false);
            this.selectedServer.copyFrom(serverData);
            this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer));
         } else if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (button.id == 8) {
            this.refreshServerList();
         } else if (button.id == 9) {
            this.mc.displayGuiScreen(new ProxyScreen());
         }
      }
   }

   private void refreshServerList() {
      this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      GuiListExtended.IGuiListEntry iGuiListEntry = this.serverListSelector.func_148193_k() < 0
         ? null
         : this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k());
      if (this.deletingServer) {
         this.deletingServer = false;
         if (result && iGuiListEntry instanceof ServerListEntryNormal) {
            this.savedServerList.removeServerData(this.serverListSelector.func_148193_k());
            this.savedServerList.saveServerList();
            this.serverListSelector.setSelectedSlotIndex(-1);
            this.serverListSelector.func_148195_a(this.savedServerList);
         }

         this.mc.displayGuiScreen(this);
      } else if (this.directConnect) {
         this.directConnect = false;
         if (result) {
            this.connectToServer(this.selectedServer);
         } else {
            this.mc.displayGuiScreen(this);
         }
      } else if (this.addingServer) {
         this.addingServer = false;
         if (result) {
            this.savedServerList.addServerData(this.selectedServer);
            this.savedServerList.saveServerList();
            this.serverListSelector.setSelectedSlotIndex(-1);
            this.serverListSelector.func_148195_a(this.savedServerList);
         }

         this.mc.displayGuiScreen(this);
      } else if (this.editingServer) {
         this.editingServer = false;
         if (result && iGuiListEntry instanceof ServerListEntryNormal) {
            ServerData serverData = ((ServerListEntryNormal)iGuiListEntry).getServerData();
            serverData.serverName = this.selectedServer.serverName;
            serverData.serverIP = this.selectedServer.serverIP;
            serverData.copyFrom(this.selectedServer);
            this.savedServerList.saveServerList();
            this.serverListSelector.func_148195_a(this.savedServerList);
         }

         this.mc.displayGuiScreen(this);
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      int i = this.serverListSelector.func_148193_k();
      GuiListExtended.IGuiListEntry iGuiListEntry = i < 0 ? null : this.serverListSelector.getListEntry(i);
      if (keyCode == 63) {
         this.refreshServerList();
      } else if (i >= 0) {
         if (keyCode == 200) {
            if (isShiftKeyDown()) {
               if (i > 0 && iGuiListEntry instanceof ServerListEntryNormal) {
                  this.savedServerList.swapServers(i, i - 1);
                  this.selectServer(this.serverListSelector.func_148193_k() - 1);
                  this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                  this.serverListSelector.func_148195_a(this.savedServerList);
               }
            } else if (i > 0) {
               this.selectServer(this.serverListSelector.func_148193_k() - 1);
               this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
               if (this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k()) instanceof ServerListEntryLanScan) {
                  if (this.serverListSelector.func_148193_k() > 0) {
                     this.selectServer(this.serverListSelector.getSize() - 1);
                     this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                  } else {
                     this.selectServer(-1);
                  }
               }
            } else {
               this.selectServer(-1);
            }
         } else if (keyCode == 208) {
            if (isShiftKeyDown()) {
               if (i < this.savedServerList.countServers() - 1) {
                  this.savedServerList.swapServers(i, i + 1);
                  this.selectServer(i + 1);
                  this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                  this.serverListSelector.func_148195_a(this.savedServerList);
               }
            } else if (i < this.serverListSelector.getSize()) {
               this.selectServer(this.serverListSelector.func_148193_k() + 1);
               this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
               if (this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k()) instanceof ServerListEntryLanScan) {
                  if (this.serverListSelector.func_148193_k() < this.serverListSelector.getSize() - 1) {
                     this.selectServer(this.serverListSelector.getSize() + 1);
                     this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                  } else {
                     this.selectServer(-1);
                  }
               }
            } else {
               this.selectServer(-1);
            }
         } else if (keyCode != 28 && keyCode != 156) {
            super.keyTyped(typedChar, keyCode);
         } else {
            this.actionPerformed(this.buttonList.get(2));
         }
      } else {
         super.keyTyped(typedChar, keyCode);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.hoveringText = null;
      this.drawDefaultBackground();
      this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.title"), this.width / 2, 20, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.hoveringText != null) {
         this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
      }

      this.drawString(this.fontRendererObj, "§7Username: §3" + this.mc.getSession().getUsername(), 3, 3, -1);
   }

   public void connectToSelected() {
      GuiListExtended.IGuiListEntry iGuiListEntry = this.serverListSelector.func_148193_k() < 0
         ? null
         : this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k());
      if (iGuiListEntry instanceof ServerListEntryNormal) {
         this.connectToServer(((ServerListEntryNormal)iGuiListEntry).getServerData());
      } else if (iGuiListEntry instanceof ServerListEntryLanDetected) {
         LanServerDetector.LanServer lanServer = ((ServerListEntryLanDetected)iGuiListEntry).getLanServer();
         this.connectToServer(new ServerData(lanServer.getServerMotd(), lanServer.getServerIpPort(), true));
      }
   }

   private void connectToServer(ServerData server) {
      if (server.serverIP != null) {
         ServerData.I = server.serverIP;
      }

      this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, server));
   }

   public void selectServer(int index) {
      this.serverListSelector.setSelectedSlotIndex(index);
      GuiListExtended.IGuiListEntry iGuiListEntry = index < 0 ? null : this.serverListSelector.getListEntry(index);
      this.btnSelectServer.enabled = false;
      this.btnEditServer.enabled = false;
      this.btnDeleteServer.enabled = false;
      if (iGuiListEntry != null && !(iGuiListEntry instanceof ServerListEntryLanScan)) {
         this.btnSelectServer.enabled = true;
         if (iGuiListEntry instanceof ServerListEntryNormal) {
            this.btnEditServer.enabled = true;
            this.btnDeleteServer.enabled = true;
         }
      }
   }

   public OldServerPinger getOldServerPinger() {
      return this.oldServerPinger;
   }

   public void setHoveringText(String string) {
      this.hoveringText = string;
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.serverListSelector.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      this.serverListSelector.mouseReleased(mouseX, mouseY, state);
   }

   public ServerList getServerList() {
      return this.savedServerList;
   }

   public boolean func_175392_a(ServerListEntryNormal serverListEntryNormal, int i) {
      return i > 0;
   }

   public boolean func_175394_b(ServerListEntryNormal serverListEntryNormal, int i) {
      return i < this.savedServerList.countServers() - 1;
   }

   public void func_175391_a(ServerListEntryNormal serverListEntryNormal, int i, boolean bl) {
      int j = bl ? 0 : i - 1;
      this.savedServerList.swapServers(i, j);
      if (this.serverListSelector.func_148193_k() == i) {
         this.selectServer(j);
      }

      this.serverListSelector.func_148195_a(this.savedServerList);
   }

   public void func_175393_b(ServerListEntryNormal serverListEntryNormal, int i, boolean bl) {
      int j = bl ? this.savedServerList.countServers() - 1 : i + 1;
      this.savedServerList.swapServers(i, j);
      if (this.serverListSelector.func_148193_k() == i) {
         this.selectServer(j);
      }

      this.serverListSelector.func_148195_a(this.savedServerList);
   }

   static {
      O.add(ProtocolVersion.v1_8);
      O.add(ProtocolVersion.v1_9);
      O.add(ProtocolVersion.v1_9_1);
      O.add(ProtocolVersion.v1_9_2);
      O.add(ProtocolVersion.v1_9_3);
      O.add(ProtocolVersion.v1_10);
      O.add(ProtocolVersion.v1_11);
      O.add(ProtocolVersion.v1_11_1);
      O.add(ProtocolVersion.v1_12);
      O.add(ProtocolVersion.v1_12_1);
      O.add(ProtocolVersion.v1_12_2);
      O.add(ProtocolVersion.v1_13);
      O.add(ProtocolVersion.v1_13_1);
      O.add(ProtocolVersion.v1_13_2);
      O.add(ProtocolVersion.v1_14);
      O.add(ProtocolVersion.v1_14_1);
      O.add(ProtocolVersion.v1_14_2);
      O.add(ProtocolVersion.v1_14_3);
      O.add(ProtocolVersion.v1_14_4);
      O.add(ProtocolVersion.v1_15);
      O.add(ProtocolVersion.v1_15_1);
      O.add(ProtocolVersion.v1_15_2);
      O.add(ProtocolVersion.v1_16);
      O.add(ProtocolVersion.v1_16_1);
      O.add(ProtocolVersion.v1_16_2);
      O.add(ProtocolVersion.v1_16_3);
      O.add(ProtocolVersion.v1_16_4);
      O.add(ProtocolVersion.v1_17);
      O.add(ProtocolVersion.v1_17_1);
      O.add(ProtocolVersion.v1_18);
      O.add(ProtocolVersion.v1_18_2);
      O.add(ProtocolVersion.v1_19);
   }
}
