package bypass.auth.net;

import bypass.util.collection.ShapedMap;
import java.util.function.Supplier;

public class PacketWritersBuilder {
   private static final ConnectionOptions n = ConnectionOptions.M(16);
   private final ShapedMap<Short, PacketWriter<?>> I = new ShapedMap<>();
   private final ShapedMap<Short, Supplier<?>> M = new ShapedMap<>();
   private PacketSender R;

   public PacketWritersBuilder y(short s, PacketWriter<?> packetWriter) {
      this.I.put(s, packetWriter);
      return this;
   }

   public PacketWritersBuilder T(short s, Supplier<?> supplier) {
      this.M.put(s, supplier);
      return this;
   }

   public PacketWriters P() {
      return new PacketWriters(this);
   }

   public static ShapedMap T(PacketWritersBuilder packetWritersBuilder) {
      return packetWritersBuilder.I;
   }

   public static ShapedMap F(PacketWritersBuilder packetWritersBuilder) {
      return packetWritersBuilder.M;
   }
}
