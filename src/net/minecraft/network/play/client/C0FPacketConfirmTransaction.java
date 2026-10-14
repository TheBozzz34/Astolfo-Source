package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0FPacketConfirmTransaction implements Packet<INetHandlerPlayServer> {
   public int windowId;
   public short uid;
   public boolean accepted;

   public C0FPacketConfirmTransaction() {
   }

   public C0FPacketConfirmTransaction(int windowId, short uid, boolean accepted) {
      this.windowId = windowId;
      this.uid = uid;
      this.accepted = accepted;
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processConfirmTransaction(this);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readByte();
      this.uid = buf.readShort();
      this.accepted = buf.readByte() != 0;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeShort(this.uid);
      buf.writeByte(this.accepted ? 1 : 0);
   }

   public void i(short s) {
      this.uid = s;
   }

   public void J(boolean bl) {
      this.accepted = bl;
   }

   public void k(int i) {
      this.windowId = i;
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
