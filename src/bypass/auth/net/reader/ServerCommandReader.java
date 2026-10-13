package bypass.auth.net.reader;

import bypass.auth.net.PacketReader;
import bypass.auth.packet.ServerCommand;
import bypass.auth.packet.ServerCommandType;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ServerCommandReader implements PacketReader<ServerCommand> {
   private ServerCommand b;
   private static final long a = 132347013873187L;

   @Override
   public void read(ByteBuffer var1) {
      byte[] var6 = new byte[var1.getInt()];
      var1.get(var6);
      this.b = new ServerCommand(ServerCommandType.values()[var1.getInt()], new String(var6, StandardCharsets.UTF_8));
   }

   public ServerCommand getPacket() {
      return this.b;
   }
}
