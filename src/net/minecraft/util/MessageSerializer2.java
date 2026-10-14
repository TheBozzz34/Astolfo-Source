package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.PacketBuffer;

public class MessageSerializer2 extends MessageToByteEncoder<ByteBuf> {
   public void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
      int i = byteBuf.readableBytes();
      int j = PacketBuffer.getVarIntSize(i);
      if (j > 3) {
         throw new IllegalArgumentException("unable to fit " + i + " into " + 3);
      }

      PacketBuffer packetBuffer = new PacketBuffer(byteBuf2);
      packetBuffer.ensureWritable(j + i);
      packetBuffer.writeVarIntToBuffer(i);
      packetBuffer.writeBytes(byteBuf, byteBuf.readerIndex(), i);
   }
}
