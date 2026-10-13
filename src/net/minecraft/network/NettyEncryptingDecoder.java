package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptingDecoder extends MessageToMessageDecoder<ByteBuf> {
   private final NettyEncryptionTranslator decryptionCodec;

   public NettyEncryptingDecoder(Cipher var1) {
      this.decryptionCodec = new NettyEncryptionTranslator(var1);
   }

   public void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws ShortBufferException, Exception {
      var3.add(this.decryptionCodec.decipher(var1, var2));
   }
}
