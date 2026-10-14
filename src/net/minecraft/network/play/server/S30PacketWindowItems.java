package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S30PacketWindowItems implements Packet<INetHandlerPlayClient> {
   private int windowId;
   private ItemStack[] itemStacks;

   public S30PacketWindowItems() {
   }

   public S30PacketWindowItems(int windowIdIn, List<ItemStack> list) {
      this.windowId = windowIdIn;
      this.itemStacks = new ItemStack[list.size()];

      for (int i = 0; i < this.itemStacks.length; i++) {
         ItemStack itemStack = (ItemStack)list.get(i);
         this.itemStacks[i] = itemStack == null ? null : itemStack.copy();
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readUnsignedByte();
      short s = buf.readShort();
      this.itemStacks = new ItemStack[s];

      for (int i = 0; i < s; i++) {
         this.itemStacks[i] = buf.readItemStackFromBuffer();
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeShort(this.itemStacks.length);

      for (ItemStack itemStack : this.itemStacks) {
         buf.writeItemStackToBuffer(itemStack);
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleWindowItems(this);
   }

   public int func_148911_c() {
      return this.windowId;
   }

   public ItemStack[] getItemStacks() {
      return this.itemStacks;
   }
}
