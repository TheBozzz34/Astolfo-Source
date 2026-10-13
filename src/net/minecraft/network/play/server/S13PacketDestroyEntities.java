package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S13PacketDestroyEntities implements Packet<INetHandlerPlayClient> {
   private int[] entityIDs;
   private static final long a = 18767926876414L;

   public S13PacketDestroyEntities() {
   }

   public S13PacketDestroyEntities(int... var1) {
      this.entityIDs = var1;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.entityIDs = new int[var1.readVarIntFromBuffer()];

      for (int var5 = 0; var5 < this.entityIDs.length; var5++) {
         this.entityIDs[var5] = var1.readVarIntFromBuffer();
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeVarIntToBuffer(this.entityIDs.length);

      for (int var5 = 0; var5 < this.entityIDs.length; var5++) {
         var1.writeVarIntToBuffer(this.entityIDs[var5]);
      }
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleDestroyEntities(this);
   }

   public int[] getEntityIDs() {
      return this.entityIDs;
   }
}
