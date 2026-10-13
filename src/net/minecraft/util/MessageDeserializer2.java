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
   public void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      var2.markReaderIndex();
      byte[] var7 = new byte[3];

      for (int var8 = 0; var8 < var7.length; var8++) {
         if (!var2.isReadable()) {
            var2.resetReaderIndex();
            return;
         }

         var7[var8] = var2.readByte();
         if (var7[var8] >= 0) {
            PacketBuffer var9 = new PacketBuffer(Unpooled.wrappedBuffer(var7));

            try {
               int var10 = var9.readVarIntFromBuffer();
               if (var2.readableBytes() < var10) {
                  var2.resetReaderIndex();
                  return;
               }

               var3.add(var2.readBytes(var10));
            } finally {
               var9.release();
            }

            return;
         }
      }

      throw new CorruptedFrameException("length wider than 21-bit");
   }
}
