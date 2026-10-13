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

   public S2EPacketCloseWindow(int var1) {
      this.windowId = var1;
   }

   public void processPacket(INetHandlerPlayClient var1) {
      GuiScreen var5 = Minecraft.getMinecraft().currentScreen;
      if (!(var5 instanceof GuiChat)
         && !(var5 instanceof DefaultClickGuiScreen)
         && !(var5 instanceof TransparentClickGuiScreen)
         && !(var5 instanceof SidebarClickGuiScreen)) {
         var1.handleCloseWindow(this);
      }
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.windowId = var1.readUnsignedByte();
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeByte(this.windowId);
   }
}
