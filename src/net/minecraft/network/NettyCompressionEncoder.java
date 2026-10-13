package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class NettyCompressionEncoder extends MessageToByteEncoder<ByteBuf> {
   private final byte[] buffer;
   private final Deflater deflater;
   private int treshold;
   private static final long b = 106593615627235L;
   private static final long c = 1155236431849857024L;

   public NettyCompressionEncoder(int var1) {
      this.buffer = new byte[(int)c];
      this.treshold = var1;
      this.deflater = new Deflater();
   }

   public void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      int var7 = var2.readableBytes();
      PacketBuffer var8 = new PacketBuffer(var3);
      if (var7 < this.treshold) {
         var8.writeVarIntToBuffer(0);
         var8.writeBytes(var2);
      } else {
         byte[] var9 = new byte[var7];
         var2.readBytes(var9);
         var8.writeVarIntToBuffer(var9.length);
         this.deflater.setInput(var9, 0, var7);
         this.deflater.finish();

         while (!this.deflater.finished()) {
            int var10 = this.deflater.deflate(this.buffer);
            var8.writeBytes(this.buffer, 0, var10);
         }

         this.deflater.reset();
      }
   }

   public void setCompressionTreshold(int var1) {
      this.treshold = var1;
   }
}
