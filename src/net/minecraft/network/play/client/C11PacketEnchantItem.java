package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C11PacketEnchantItem implements Packet<INetHandlerPlayServer> {
   private int windowId;
   private int button;

   public C11PacketEnchantItem() {
   }

   public C11PacketEnchantItem(int var1, int var2) {
      this.windowId = var1;
      this.button = var2;
   }

   public void processPacket(INetHandlerPlayServer var1) {
      var1.processEnchantItem(this);
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.windowId = var1.readByte();
      this.button = var1.readByte();
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeByte(this.windowId);
      var1.writeByte(this.button);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public int getButton() {
      return this.button;
   }
}
