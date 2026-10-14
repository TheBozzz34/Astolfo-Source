package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import bypass.auth.packet.LoginPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class LoginPacketWriter implements PacketWriter<LoginPacket> {
   public void write(LoginPacket loginPacket, PacketBuffer packetBuffer) {
      byte[] bytes = loginPacket.username.getBytes(StandardCharsets.UTF_8);
      byte[] bytes2 = loginPacket.password.getBytes(StandardCharsets.UTF_8);
      byte[] bytes3 = loginPacket.version.getBytes(StandardCharsets.UTF_8);
      byte[] bytes4 = loginPacket.hwid.getBytes(StandardCharsets.UTF_8);
      byte[] bytes5 = loginPacket.token.getBytes(StandardCharsets.UTF_8);
      ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + bytes2.length + bytes3.length + bytes4.length + bytes5.length + 20);
      byteBuffer.putInt(bytes.length);
      byteBuffer.put(bytes);
      byteBuffer.putInt(bytes2.length);
      byteBuffer.put(bytes2);
      byteBuffer.putInt(bytes3.length);
      byteBuffer.put(bytes3);
      byteBuffer.putInt(bytes4.length);
      byteBuffer.put(bytes4);
      byteBuffer.putInt(bytes5.length);
      byteBuffer.put(bytes5);
      packetBuffer.n(byteBuffer);
      PacketWriter.super.write(loginPacket, packetBuffer);
   }
}
