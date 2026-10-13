package bypass.via.handler;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.exception.CancelEncoderException;
import com.viaversion.viaversion.util.PipelineUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import net.minecraft.util.MessageSerializer;

public class VREncodeHandler extends MessageToMessageEncoder<ByteBuf> {
   private final UserConnection H;
   private static final long a = 139446059215589L;

   public VREncodeHandler(UserConnection var1) {
      this.H = var1;
   }

   public void encode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (!this.H.checkOutgoingPacket()) {
         throw CancelEncoderException.generate(null);
      }

      if (this.H.shouldTransformPacket() && !MessageSerializer.o) {
         ByteBuf var7 = var1.alloc().buffer().writeBytes(var2);

         try {
            this.H.transformOutgoing(var7, CancelEncoderException::generate);
            var3.add(var7.retain());
         } finally {
            var7.release();
         }
      } else {
         var3.add(var2.retain());
         MessageSerializer.o = false;
      }
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (!PipelineUtil.containsCause(var2, CancelCodecException.class)) {
         super.exceptionCaught(var1, var2);
      }
   }
}
