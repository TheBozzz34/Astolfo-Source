package bypass.auth.net;

import java.util.Map;
import java.util.function.Supplier;

public final class PacketWriters implements PacketOutput {
   private final Map<Short, PacketWriter<?>> writers;
   private final Map<Short, Supplier<?>> suppliers;
   private PacketSender sender;

   public PacketWriters(PacketWritersBuilder packetWritersBuilder) {
      this.writers = PacketWritersBuilder.getWriters(packetWritersBuilder);
      this.suppliers = PacketWritersBuilder.getSuppliers(packetWritersBuilder);
   }

   @Override
   public void attach(SocketWriter socketWriter) {
      this.sender = new DefaultPacketSender(this.suppliers, this.writers, socketWriter);
   }

   @Override
   public PacketSender getSender() {
      return this.sender;
   }

   public static PacketWritersBuilder newBuilder() {
      return new PacketWritersBuilder();
   }
}
