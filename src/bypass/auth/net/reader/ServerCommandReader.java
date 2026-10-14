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
   public void read(ByteBuffer byteBuffer) {
      byte[] bytes = new byte[byteBuffer.getInt()];
      byteBuffer.get(bytes);
      this.b = new ServerCommand(ServerCommandType.values()[byteBuffer.getInt()], new String(bytes, StandardCharsets.UTF_8));
   }

   public ServerCommand getPacket() {
      return this.b;
   }
}
