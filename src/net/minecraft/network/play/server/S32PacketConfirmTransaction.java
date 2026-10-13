package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S32PacketConfirmTransaction implements Packet<INetHandlerPlayClient> {
   private int windowId;
   private short actionNumber;
   private boolean field_148893_c;

   public S32PacketConfirmTransaction() {
   }

   public S32PacketConfirmTransaction(int var1, short var2, boolean var3) {
      this.windowId = var1;
      this.actionNumber = var2;
      this.field_148893_c = var3;
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleConfirmTransaction(this);
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.windowId = var1.readUnsignedByte();
      this.actionNumber = var1.readShort();
      this.field_148893_c = var1.readBoolean();
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeByte(this.windowId);
      var1.writeShort(this.actionNumber);
      var1.writeBoolean(this.field_148893_c);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public short getActionNumber() {
      return this.actionNumber;
   }

   public boolean func_148888_e() {
      return this.field_148893_c;
   }
}
