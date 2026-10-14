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
   public void D(short s, String string) {
      this.l.putIfAbsent(string, s);
   }

   @Override
   public Short J(String string) {
      return this.l.get(string);
   }

   @Override
   public void D(String string, PacketReader<?> packetReader) {
      Short short2 = this.l.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      }

      this.r.put(short2, packetReader);
   }

   @Override
   public PacketReader<?> X(String string) {
      Short short2 = this.l.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      } else {
         return this.r.get(short2);
      }
   }

   @Override
   public PacketReader<?> r(short s) {
      return this.r.get(s);
   }

   @Override
   public <T> void addPacketListener(String string, String string2, PacketListener<T> packetListener) {
      Short short2 = this.l.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      }

      this.c.computeIfAbsent(short2, short3 -> new ShapedMap<>()).putIfAbsent(string2, packetListener);
   }

   @Override
   public void q(String string, String string2) {
      Short short2 = this.l.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      }

      Map map = this.c.get(short2);
      if (map != null) {
         map.remove(string2);
      }
   }

   @Override
   public void S(String string) {
      Short short2 = this.l.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      }

      this.c.remove(short2);
   }

   @Override
   public boolean W(String string, String string2) {
      Short short2 = this.l.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      }

      Map map = this.c.get(short2);
      return map == null ? false : map.containsKey(string2);
   }

   @Override
   public boolean U(String string) {
      Short short2 = this.l.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      } else {
         return this.c.containsKey(short2);
      }
   }

   @Override
   public Collection<PacketListener<?>> Y(String string) {
      Short short2 = this.l.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      }

      Map map = this.c.get(short2);
      return map == null ? null : map.values();
   }

   @Override
   public Collection<PacketListener<?>> y(short s) {
      Map map = this.c.get(s);
      return map == null ? null : map.values();
   }
}
