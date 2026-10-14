package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class NettyCompressionDecoder extends ByteToMessageDecoder {
   private final Inflater inflater;
   private int treshold;

   public NettyCompressionDecoder(int treshold) {
      this.treshold = treshold;
      this.inflater = new Inflater();
   }

   @Override
   public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws DataFormatException, Exception {
      if (byteBuf.readableBytes() != 0) {
         PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
         int i = packetBuffer.readVarIntFromBuffer();
         if (i == 0) {
            list.add(packetBuffer.readBytes(packetBuffer.readableBytes()));
         } else {
            if (i < this.treshold) {
               throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.treshold);
            }

            if (i > 2097152) {
               throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of " + 2097152);
            }

            byte[] bytes = new byte[packetBuffer.readableBytes()];
            packetBuffer.readBytes(bytes);
            this.inflater.setInput(bytes);
            byte[] bytes2 = new byte[i];
            this.inflater.inflate(bytes2);
            list.add(Unpooled.wrappedBuffer(bytes2));
            this.inflater.reset();
         }
      }
   }

   public void setCompressionTreshold(int treshold) {
      this.treshold = treshold;
   }
}
