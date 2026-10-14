package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.network.PacketBuffer;

public class MessageDeserializer2 extends ByteToMessageDecoder {
   private static final long a = 8957413458737L;

   @Override
   public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
      byteBuf.markReaderIndex();
      byte[] bytes = new byte[3];

      for (int i = 0; i < bytes.length; i++) {
         if (!byteBuf.isReadable()) {
            byteBuf.resetReaderIndex();
            return;
         }

         bytes[i] = byteBuf.readByte();
         if (bytes[i] >= 0) {
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(bytes));

            try {
               int j = packetBuffer.readVarIntFromBuffer();
               if (byteBuf.readableBytes() < j) {
                  byteBuf.resetReaderIndex();
                  return;
               }

               list.add(byteBuf.readBytes(j));
            } finally {
               packetBuffer.release();
            }

            return;
         }
      }

      throw new CorruptedFrameException("length wider than 21-bit");
   }
}
