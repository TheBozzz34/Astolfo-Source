package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C16PacketClientStatus implements Packet<INetHandlerPlayServer> {
   private C16PacketClientStatus.EnumState status;

   public C16PacketClientStatus() {
   }

   public C16PacketClientStatus(C16PacketClientStatus.EnumState var1) {
      this.status = var1;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.status = var1.readEnumValue(C16PacketClientStatus.EnumState.class);
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeEnumValue(this.status);
   }

   public void processPacket(INetHandlerPlayServer var1) {
      var1.processClientStatus(this);
   }

   public C16PacketClientStatus.EnumState getStatus() {
      return this.status;
   }

   public void X(C16PacketClientStatus.EnumState var1) {
      this.status = var1;
   }

   public enum EnumState {
      PERFORM_RESPAWN,
      REQUEST_STATS,
      OPEN_INVENTORY_ACHIEVEMENT;
   }
}
