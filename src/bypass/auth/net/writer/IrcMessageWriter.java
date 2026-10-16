package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import bypass.auth.packet.IrcMessage;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class IrcMessageWriter implements PacketWriter<IrcMessage> {
   public void write(IrcMessage ircMessage, PacketBuffer packetBuffer) {
      byte[] bytes = ircMessage.sender.getBytes(StandardCharsets.UTF_8);
      byte[] bytes2 = ircMessage.message.getBytes(StandardCharsets.UTF_8);
      ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + bytes2.length + 12);
      byteBuffer.putInt(bytes.length);
      byteBuffer.put(bytes);
      byteBuffer.putInt(bytes2.length);
      byteBuffer.put(bytes2);
      byteBuffer.putInt(ircMessage.type.ordinal());
      packetBuffer.addChunk(byteBuffer);
   }
}
