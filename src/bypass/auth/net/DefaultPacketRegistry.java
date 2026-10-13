package bypass.auth.net;

import bypass.util.collection.ShapedMap;
import java.util.Collection;
import java.util.Map;

public class DefaultPacketRegistry implements PacketRegistry {
   private final Map<Short, PacketReader<?>> r = new ShapedMap<>();
   private final Map<String, Short> l = new ShapedMap<>();
   private final Map<Short, Map<String, PacketListener<?>>> c = new ShapedMap<>();
   private static final long a = 101568012108203L;

   @Override
   public void D(short var1, String var2) {
      this.l.putIfAbsent(var2, var1);
   }

   @Override
   public Short J(String var1) {
      return this.l.get(var1);
   }

   @Override
   public void D(String var1, PacketReader<?> var2) {
      Short var6 = this.l.get(var1);
      if (var6 == null) {
         throw new UnknownPacketException(var1);
      }

      this.r.put(var6, var2);
   }

   @Override
   public PacketReader<?> X(String var1) {
      Short var5 = this.l.get(var1);
      if (var5 == null) {
         throw new UnknownPacketException(var1);
      } else {
         return this.r.get(var5);
      }
   }

   @Override
   public PacketReader<?> r(short var1) {
      return this.r.get(var1);
   }

   @Override
   public <T> void addPacketListener(String var1, String var2, PacketListener<T> var3) {
      Short var7 = this.l.get(var1);
      if (var7 == null) {
         throw new UnknownPacketException(var1);
      }

      this.c.computeIfAbsent(var7, var0 -> new ShapedMap<>()).putIfAbsent(var2, var3);
   }

   @Override
   public void q(String var1, String var2) {
      Short var6 = this.l.get(var1);
      if (var6 == null) {
         throw new UnknownPacketException(var1);
      }

      Map var7 = this.c.get(var6);
      if (var7 != null) {
         var7.remove(var2);
      }
   }

   @Override
   public void S(String var1) {
      Short var5 = this.l.get(var1);
      if (var5 == null) {
         throw new UnknownPacketException(var1);
      }

      this.c.remove(var5);
   }

   @Override
   public boolean W(String var1, String var2) {
      Short var6 = this.l.get(var1);
      if (var6 == null) {
         throw new UnknownPacketException(var1);
      }

      Map var7 = this.c.get(var6);
      return var7 == null ? false : var7.containsKey(var2);
   }

   @Override
   public boolean U(String var1) {
      Short var4 = this.l.get(var1);
      if (var4 == null) {
         throw new UnknownPacketException(var1);
      } else {
         return this.c.containsKey(var4);
      }
   }

   @Override
   public Collection<PacketListener<?>> Y(String var1) {
      Short var5 = this.l.get(var1);
      if (var5 == null) {
         throw new UnknownPacketException(var1);
      }

      Map var6 = this.c.get(var5);
      return var6 == null ? null : var6.values();
   }

   @Override
   public Collection<PacketListener<?>> y(short var1) {
      Map var5 = this.c.get(var1);
      return var5 == null ? null : var5.values();
   }
}
