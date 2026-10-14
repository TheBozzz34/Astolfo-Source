package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import bypass.auth.packet.IrcMessage;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class IrcMessageWriter implements PacketWriter<IrcMessage> {
   private static final long a = 22648129839209L;
   private static final long b = 722578507535745036L;

   public void write(IrcMessage ircMessage, PacketBuffer packetBuffer) {
      byte[] bytes = ircMessage.sender.getBytes(StandardCharsets.UTF_8);
      byte[] bytes2 = ircMessage.message.getBytes(StandardCharsets.UTF_8);
      ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + bytes2.length + (int)b);
      byteBuffer.putInt(bytes.length);
      byteBuffer.put(bytes);
      byteBuffer.putInt(bytes2.length);
      byteBuffer.put(bytes2);
      byteBuffer.putInt(ircMessage.type.ordinal());
      packetBuffer.n(byteBuffer);
   }
}
