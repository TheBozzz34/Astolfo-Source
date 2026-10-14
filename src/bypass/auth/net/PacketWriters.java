package bypass.auth.net;

import java.util.Map;
import java.util.function.Supplier;

public final class PacketWriters implements PacketOutput {
   private final Map<Short, PacketWriter<?>> n;
   private final Map<Short, Supplier<?>> j;
   private PacketSender u;

   public PacketWriters(PacketWritersBuilder packetWritersBuilder) {
      this.n = PacketWritersBuilder.T(packetWritersBuilder);
      this.j = PacketWritersBuilder.F(packetWritersBuilder);
   }

   @Override
   public void attach(SocketWriter socketWriter) {
      this.u = new DefaultPacketSender(this.j, this.n, socketWriter);
   }

   @Override
   public PacketSender getSender() {
      return this.u;
   }

   public static PacketWritersBuilder U() {
      return new PacketWritersBuilder();
   }
}
