package bypass.auth.net;

import java.util.Map;
import java.util.function.Supplier;

public final class PacketWriters implements PacketOutput {
   private final Map<Short, PacketWriter<?>> n;
   private final Map<Short, Supplier<?>> j;
   private PacketSender u;

   public PacketWriters(PacketWritersBuilder var1) {
      this.n = PacketWritersBuilder.T(var1);
      this.j = PacketWritersBuilder.F(var1);
   }

   @Override
   public void attach(SocketWriter var1) {
      this.u = new DefaultPacketSender(this.j, this.n, var1);
   }

   @Override
   public PacketSender getSender() {
      return this.u;
   }

   public static PacketWritersBuilder U() {
      return new PacketWritersBuilder();
   }
}
