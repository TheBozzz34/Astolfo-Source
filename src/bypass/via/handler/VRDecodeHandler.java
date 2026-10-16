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
   private final UserConnection userConnection;

   public VRDecodeHandler(UserConnection userConnection) {
      this.userConnection = userConnection;
   }

   public UserConnection getUserConnection() {
      return this.userConnection;
   }

   public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
      if (!this.userConnection.checkIncomingPacket()) {
         throw CancelDecoderException.generate(null);
      }

      if (!this.userConnection.shouldTransformPacket()) {
         list.add(byteBuf.retain());
      } else {
         ByteBuf byteBuf2 = channelHandlerContext.alloc().buffer().writeBytes(byteBuf);

         try {
            this.userConnection.transformIncoming(byteBuf2, CancelDecoderException::generate);
            list.add(byteBuf2.retain());
         } finally {
            byteBuf2.release();
         }
      }
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
      if (!PipelineUtil.containsCause(throwable, CancelCodecException.class)) {
         throwable.printStackTrace();
      }
   }
}
