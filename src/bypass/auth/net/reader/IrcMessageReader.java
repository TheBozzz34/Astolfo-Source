package bypass.auth.net.reader;

import bypass.auth.net.PacketReader;
import bypass.auth.packet.IrcMessage;
import bypass.auth.packet.IrcMessageType;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class IrcMessageReader implements PacketReader<IrcMessage> {
   private IrcMessage packet;

   @Override
   public void read(ByteBuffer byteBuffer) {
      byte[] bytes = new byte[byteBuffer.getInt()];
      byteBuffer.get(bytes);
      byte[] bytes2 = new byte[byteBuffer.getInt()];
      byteBuffer.get(bytes2);
      int i = byteBuffer.getInt();
      this.packet = new IrcMessage(IrcMessageType.values()[i], new String(bytes, StandardCharsets.UTF_8), new String(bytes2, StandardCharsets.UTF_8));
   }

   public IrcMessage getPacket() {
      return this.packet;
   }
}
