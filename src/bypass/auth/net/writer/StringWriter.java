package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class StringWriter implements PacketWriter<String> {
   public void write(String string, PacketBuffer packetBuffer) {
      byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
      ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + 4);
      byteBuffer.putInt(bytes.length);
      byteBuffer.put(bytes);
      packetBuffer.n(byteBuffer);
      PacketWriter.super.write(string, packetBuffer);
   }
}
