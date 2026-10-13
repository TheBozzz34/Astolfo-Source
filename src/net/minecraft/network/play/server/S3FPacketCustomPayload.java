package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3FPacketCustomPayload implements Packet<INetHandlerPlayClient> {
   private String channel;
   private PacketBuffer data;
   private static final long a = 134600879501773L;

   public S3FPacketCustomPayload() {
   }

   public S3FPacketCustomPayload(String var1, PacketBuffer var2) {
      this.channel = var1;
      this.data = var2;
      if (var2.writerIndex() > 1048576) {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.channel = var1.readStringFromBuffer(20);
      int var5 = var1.readableBytes();
      if (var5 >= 0 && var5 <= 1048576) {
         this.data = new PacketBuffer(var1.readBytes(var5));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeString(this.channel);
      var1.writeBytes(this.data);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleCustomPayload(this);
   }

   public String getChannelName() {
      return this.channel;
   }

   public PacketBuffer getBufferData() {
      return this.data;
   }
}
