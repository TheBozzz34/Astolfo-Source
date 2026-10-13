package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.PacketBuffer;

public class MessageSerializer2 extends MessageToByteEncoder<ByteBuf> {
   private static final long a = 102392642139813L;

   public void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      int var6 = var2.readableBytes();
      int var7 = PacketBuffer.getVarIntSize(var6);
      if (var7 > 3) {
         throw new IllegalArgumentException("unable to fit " + var6 + " into " + 3);
      }

      PacketBuffer var8 = new PacketBuffer(var3);
      var8.ensureWritable(var7 + var6);
      var8.writeVarIntToBuffer(var6);
      var8.writeBytes(var2, var2.readerIndex(), var6);
   }
}
