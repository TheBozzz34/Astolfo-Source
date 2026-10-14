package net.minecraft.network.play.server;

import bypass.gui.clickgui.defaultgui.DefaultClickGuiScreen;
import bypass.gui.clickgui.sidebar.SidebarClickGuiScreen;
import bypass.gui.clickgui.transparent.TransparentClickGuiScreen;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2EPacketCloseWindow implements Packet<INetHandlerPlayClient> {
   private int windowId;
   private static final long a = 40573987190701L;

   public S2EPacketCloseWindow() {
   }

   public S2EPacketCloseWindow(int windowIdIn) {
      this.windowId = windowIdIn;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
      if (!(guiScreen instanceof GuiChat)
         && !(guiScreen instanceof DefaultClickGuiScreen)
         && !(guiScreen instanceof TransparentClickGuiScreen)
         && !(guiScreen instanceof SidebarClickGuiScreen)) {
         handler.handleCloseWindow(this);
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readUnsignedByte();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
   }
}
