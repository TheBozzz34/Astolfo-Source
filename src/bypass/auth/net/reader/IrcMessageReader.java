package bypass.auth.net.reader;

import bypass.auth.net.PacketReader;
import bypass.auth.packet.IrcMessage;
import bypass.auth.packet.IrcMessageType;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class IrcMessageReader implements PacketReader<IrcMessage> {
   private IrcMessage s;
   private static final long a = 103849567665028L;

   @Override
   public void read(ByteBuffer var1) {
      byte[] var5 = new byte[var1.getInt()];
      var1.get(var5);
      byte[] var6 = new byte[var1.getInt()];
      var1.get(var6);
      int var7 = var1.getInt();
      this.s = new IrcMessage(IrcMessageType.values()[var7], new String(var5, StandardCharsets.UTF_8), new String(var6, StandardCharsets.UTF_8));
   }

   public IrcMessage getPacket() {
      return this.s;
   }
}
