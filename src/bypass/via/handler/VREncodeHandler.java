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

   public VREncodeHandler(UserConnection userConnection) {
      this.H = userConnection;
   }

   public void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
      if (!this.H.checkOutgoingPacket()) {
         throw CancelEncoderException.generate(null);
      }

      if (this.H.shouldTransformPacket() && !MessageSerializer.o) {
         ByteBuf byteBuf2 = channelHandlerContext.alloc().buffer().writeBytes(byteBuf);

         try {
            this.H.transformOutgoing(byteBuf2, CancelEncoderException::generate);
            list.add(byteBuf2.retain());
         } finally {
            byteBuf2.release();
         }
      } else {
         list.add(byteBuf.retain());
         MessageSerializer.o = false;
      }
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
      if (!PipelineUtil.containsCause(throwable, CancelCodecException.class)) {
         super.exceptionCaught(channelHandlerContext, throwable);
      }
   }
}
