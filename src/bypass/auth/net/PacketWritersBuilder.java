package bypass.auth.net;

import bypass.util.collection.ShapedMap;
import java.util.function.Supplier;

public class PacketWritersBuilder {
   private static final ConnectionOptions defaultOptions = ConnectionOptions.ofSize(16);
   private final ShapedMap<Short, PacketWriter<?>> writers = new ShapedMap<>();
   private final ShapedMap<Short, Supplier<?>> suppliers = new ShapedMap<>();
   private PacketSender sender;

   public PacketWritersBuilder withWriter(short s, PacketWriter<?> packetWriter) {
      this.writers.put(s, packetWriter);
      return this;
   }

   public PacketWritersBuilder withSupplier(short s, Supplier<?> supplier) {
      this.suppliers.put(s, supplier);
      return this;
   }

   public PacketWriters build() {
      return new PacketWriters(this);
   }

   public static ShapedMap getWriters(PacketWritersBuilder packetWritersBuilder) {
      return packetWritersBuilder.writers;
   }

   public static ShapedMap getSuppliers(PacketWritersBuilder packetWritersBuilder) {
      return packetWritersBuilder.suppliers;
   }
}
