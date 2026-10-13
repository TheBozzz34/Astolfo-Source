package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C17PacketCustomPayload implements Packet<INetHandlerPlayServer> {
   private String channel;
   private PacketBuffer data;
   private static final long a = 83190311601897L;

   public C17PacketCustomPayload() {
   }

   public C17PacketCustomPayload(String var1, PacketBuffer var2) {
      this.channel = var1;
      this.data = var2;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.channel = var1.readStringFromBuffer(20);
      int var5 = var1.readableBytes();
      if (var5 >= 0 && var5 <= 32767) {
         this.data = new PacketBuffer(var1.readBytes(var5));
      } else {
         throw new IOException("Payload may not be larger than 32767 bytes");
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeString(this.channel);
      var1.writeBytes(this.data);
   }

   public void processPacket(INetHandlerPlayServer var1) {
      var1.processVanilla250Packet(this);
   }

   public String getChannelName() {
      return this.channel;
   }

   public PacketBuffer getBufferData() {
      return this.data;
   }
}
