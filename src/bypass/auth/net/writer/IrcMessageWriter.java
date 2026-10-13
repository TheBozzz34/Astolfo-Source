package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import bypass.auth.packet.IrcMessage;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class IrcMessageWriter implements PacketWriter<IrcMessage> {
   private static final long a = 22648129839209L;
   private static final long b = 722578507535745036L;

   public void write(IrcMessage var1, PacketBuffer var2) {
      byte[] var7 = var1.sender.getBytes(StandardCharsets.UTF_8);
      byte[] var8 = var1.message.getBytes(StandardCharsets.UTF_8);
      ByteBuffer var9 = ByteBuffer.allocate(var7.length + var8.length + (int)b);
      var9.putInt(var7.length);
      var9.put(var7);
      var9.putInt(var8.length);
      var9.put(var8);
      var9.putInt(var1.type.ordinal());
      var2.n(var9);
   }
}
