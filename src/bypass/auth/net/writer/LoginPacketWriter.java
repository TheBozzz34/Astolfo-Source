package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import bypass.auth.packet.LoginPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class LoginPacketWriter implements PacketWriter<LoginPacket> {
   private static final long a = 89930680398976L;
   private static final long b = 714501026966667284L;

   public void write(LoginPacket var1, PacketBuffer var2) {
      byte[] var7 = var1.username.getBytes(StandardCharsets.UTF_8);
      byte[] var8 = var1.password.getBytes(StandardCharsets.UTF_8);
      byte[] var9 = var1.version.getBytes(StandardCharsets.UTF_8);
      byte[] var10 = var1.hwid.getBytes(StandardCharsets.UTF_8);
      byte[] var11 = var1.token.getBytes(StandardCharsets.UTF_8);
      ByteBuffer var12 = ByteBuffer.allocate(var7.length + var8.length + var9.length + var10.length + var11.length + (int)b);
      var12.putInt(var7.length);
      var12.put(var7);
      var12.putInt(var8.length);
      var12.put(var8);
      var12.putInt(var9.length);
      var12.put(var9);
      var12.putInt(var10.length);
      var12.put(var10);
      var12.putInt(var11.length);
      var12.put(var11);
      var2.n(var12);
      PacketWriter.super.write(var1, var2);
   }
}
