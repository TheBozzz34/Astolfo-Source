package bypass.via.handler;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.exception.CancelDecoderException;
import com.viaversion.viaversion.util.PipelineUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

public class VRDecodeHandler extends MessageToMessageDecoder<ByteBuf> {
   private final UserConnection K;
   private static final long a = 138098345629489L;

   public VRDecodeHandler(UserConnection var1) {
      this.K = var1;
   }

   public UserConnection I() {
      return this.K;
   }

   public void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (!this.K.checkIncomingPacket()) {
         throw CancelDecoderException.generate(null);
      }

      if (!this.K.shouldTransformPacket()) {
         var3.add(var2.retain());
      } else {
         ByteBuf var7 = var1.alloc().buffer().writeBytes(var2);

         try {
            this.K.transformIncoming(var7, CancelDecoderException::generate);
            var3.add(var7.retain());
         } finally {
            var7.release();
         }
      }
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (!PipelineUtil.containsCause(var2, CancelCodecException.class)) {
         var2.printStackTrace();
      }
   }
}
