package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0FPacketConfirmTransaction implements Packet<INetHandlerPlayServer> {
   public int windowId;
   public short uid;
   public boolean accepted;
   private static final long a = 130315075654604L;

   public C0FPacketConfirmTransaction() {
   }

   public C0FPacketConfirmTransaction(int var1, short var2, boolean var3) {
      this.windowId = var1;
      this.uid = var2;
      this.accepted = var3;
   }

   public void processPacket(INetHandlerPlayServer var1) {
      var1.processConfirmTransaction(this);
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.windowId = var1.readByte();
      this.uid = var1.readShort();
      this.accepted = var1.readByte() != 0;
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeByte(this.windowId);
      var1.writeShort(this.uid);
      var1.writeByte(this.accepted ? 1 : 0);
   }

   public void i(short var1) {
      this.uid = var1;
   }

   public void J(boolean var1) {
      this.accepted = var1;
   }

   public void k(int var1) {
      this.windowId = var1;
   }

   public int getWindowId() {
      return this.windowId;
   }

   public boolean N() {
      return this.accepted;
   }

   public short getUid() {
      return this.uid;
   }
}
