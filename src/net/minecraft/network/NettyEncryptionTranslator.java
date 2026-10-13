package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptionTranslator {
   private final Cipher cipher;
   private byte[] field_150505_b = new byte[0];
   private byte[] field_150506_c = new byte[0];
   private static final long a = 66175168037318L;

   public NettyEncryptionTranslator(Cipher var1) {
      this.cipher = var1;
   }

   private byte[] func_150502_a(ByteBuf var1) {
      int var5 = var1.readableBytes();
      if (this.field_150505_b.length < var5) {
         this.field_150505_b = new byte[var5];
      }

      var1.readBytes(this.field_150505_b, 0, var5);
      return this.field_150505_b;
   }

   public ByteBuf decipher(ChannelHandlerContext var1, ByteBuf var2) throws ShortBufferException {
      int var3 = var2.readableBytes();
      byte[] var4 = this.func_150502_a(var2);
      ByteBuf var5 = var1.alloc().heapBuffer(this.cipher.getOutputSize(var3));
      var5.writerIndex(this.cipher.update(var4, 0, var3, var5.array(), var5.arrayOffset()));
      return var5;
   }

   public void cipher(ByteBuf var1, ByteBuf var2) throws ShortBufferException {
      int var6 = var1.readableBytes();
      byte[] var7 = this.func_150502_a(var1);
      int var8 = this.cipher.getOutputSize(var6);
      if (this.field_150506_c.length < var8) {
         this.field_150506_c = new byte[var8];
      }

      var2.writeBytes(this.field_150506_c, 0, this.cipher.update(var7, 0, var6, this.field_150506_c));
   }
}
