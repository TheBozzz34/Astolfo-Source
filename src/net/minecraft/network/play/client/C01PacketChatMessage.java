package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C01PacketChatMessage implements Packet<INetHandlerPlayServer> {
   private String message;
   private static final long a = 111212618613026L;

   public C01PacketChatMessage() {
   }

   public C01PacketChatMessage(String var1) {
      if (var1.length() > 100) {
         var1 = var1.substring(0, 100);
      }

      this.message = var1;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.message = var1.readStringFromBuffer(100);
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeString(this.message);
   }

   public void processPacket(INetHandlerPlayServer var1) {
      var1.processChatMessage(this);
   }

   public String getMessage() {
      return this.message;
   }

   public void b(String var1) {
      this.message = var1;
   }
}
