package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C10PacketCreativeInventoryAction implements Packet<INetHandlerPlayServer> {
   private int slotId;
   private ItemStack stack;
   private static final long a = 64936352792403L;

   public C10PacketCreativeInventoryAction() {
   }

   public C10PacketCreativeInventoryAction(int var1, ItemStack var2) {
      this.slotId = var1;
      this.stack = var2 != null ? var2.copy() : null;
   }

   public void processPacket(INetHandlerPlayServer var1) {
      var1.processCreativeInventoryAction(this);
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.slotId = var1.readShort();
      this.stack = var1.readItemStackFromBuffer();
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeShort(this.slotId);
      var1.writeItemStackToBuffer(this.stack);
   }

   public int getSlotId() {
      return this.slotId;
   }

   public ItemStack getStack() {
      return this.stack;
   }
}
