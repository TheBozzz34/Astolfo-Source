package bypass.auth.net;

import bypass.util.collection.ShapedMap;
import java.util.function.Supplier;

public class PacketWritersBuilder {
   private static final ConnectionOptions n = ConnectionOptions.M(16);
   private final ShapedMap<Short, PacketWriter<?>> I = new ShapedMap<>();
   private final ShapedMap<Short, Supplier<?>> M = new ShapedMap<>();
   private PacketSender R;

   public PacketWritersBuilder y(short var1, PacketWriter<?> var2) {
      this.I.put(var1, var2);
      return this;
   }

   public PacketWritersBuilder T(short var1, Supplier<?> var2) {
      this.M.put(var1, var2);
      return this;
   }

   public PacketWriters P() {
      return new PacketWriters(this);
   }

   public static ShapedMap T(PacketWritersBuilder var0) {
      return var0.I;
   }

   public static ShapedMap F(PacketWritersBuilder var0) {
      return var0.M;
   }
}
