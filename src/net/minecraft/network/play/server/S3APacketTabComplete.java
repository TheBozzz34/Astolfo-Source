package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3APacketTabComplete implements Packet<INetHandlerPlayClient> {
   private String[] matches;
   private static final long a = 113482614698713L;
   private static final long b = -3812014780663693313L;

   public S3APacketTabComplete() {
   }

   public S3APacketTabComplete(String[] var1) {
      this.matches = var1;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.matches = new String[var1.readVarIntFromBuffer()];

      for (int var5 = 0; var5 < this.matches.length; var5++) {
         this.matches[var5] = var1.readStringFromBuffer((int)b);
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeVarIntToBuffer(this.matches.length);

      for (String var8 : this.matches) {
         var1.writeString(var8);
      }
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleTabComplete(this);
   }

   public String[] func_149630_c() {
      return this.matches;
   }
}
