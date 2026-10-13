package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S46PacketSetCompressionLevel implements Packet<INetHandlerPlayClient> {
   private int threshold;

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.threshold = var1.readVarIntFromBuffer();
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeVarIntToBuffer(this.threshold);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleSetCompressionLevel(this);
   }

   public int getThreshold() {
      return this.threshold;
   }
}
