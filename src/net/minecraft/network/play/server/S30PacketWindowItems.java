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
   private static final long a = 78587540574049L;

   public S30PacketWindowItems() {
   }

   public S30PacketWindowItems(int var1, List<ItemStack> var2) {
      this.windowId = var1;
      this.itemStacks = new ItemStack[var2.size()];

      for (int var6 = 0; var6 < this.itemStacks.length; var6++) {
         ItemStack var7 = (ItemStack)var2.get(var6);
         this.itemStacks[var6] = var7 == null ? null : var7.copy();
      }
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.windowId = var1.readUnsignedByte();
      short var5 = var1.readShort();
      this.itemStacks = new ItemStack[var5];

      for (int var6 = 0; var6 < var5; var6++) {
         this.itemStacks[var6] = var1.readItemStackFromBuffer();
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeByte(this.windowId);
      var1.writeShort(this.itemStacks.length);

      for (ItemStack var8 : this.itemStacks) {
         var1.writeItemStackToBuffer(var8);
      }
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleWindowItems(this);
   }

   public int func_148911_c() {
      return this.windowId;
   }

   public ItemStack[] getItemStacks() {
      return this.itemStacks;
   }
}
