package bypass.auth.net;

import bypass.util.collection.ShapedMap;
import java.util.Collection;
import java.util.Map;

public class DefaultPacketRegistry implements PacketRegistry {
   private final Map<Short, PacketReader<?>> readers = new ShapedMap<>();
   private final Map<String, Short> packetIds = new ShapedMap<>();
   private final Map<Short, Map<String, PacketListener<?>>> listeners = new ShapedMap<>();

   @Override
   public void registerPacketId(short s, String string) {
      this.packetIds.putIfAbsent(string, s);
   }

   @Override
   public Short getPacketId(String string) {
      return this.packetIds.get(string);
   }

   @Override
   public void registerReader(String string, PacketReader<?> packetReader) {
      Short short2 = this.packetIds.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      }

      this.readers.put(short2, packetReader);
   }

   @Override
   public PacketReader<?> getReader(String string) {
      Short short2 = this.packetIds.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      } else {
         return this.readers.get(short2);
      }
   }

   @Override
   public PacketReader<?> getReaderById(short s) {
      return this.readers.get(s);
   }

   @Override
   public <T> void addPacketListener(String string, String string2, PacketListener<T> packetListener) {
      Short short2 = this.packetIds.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      }

      this.listeners.computeIfAbsent(short2, short3 -> new ShapedMap<>()).putIfAbsent(string2, packetListener);
   }

   @Override
   public void removePacketListener(String string, String string2) {
      Short short2 = this.packetIds.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      }

      Map map = this.listeners.get(short2);
      if (map != null) {
         map.remove(string2);
      }
   }

   @Override
   public void removePacketListeners(String string) {
      Short short2 = this.packetIds.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      }

      this.listeners.remove(short2);
   }

   @Override
   public boolean hasPacketListener(String string, String string2) {
      Short short2 = this.packetIds.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      }

      Map map = this.listeners.get(short2);
      return map == null ? false : map.containsKey(string2);
   }

   @Override
   public boolean hasPacketListeners(String string) {
      Short short2 = this.packetIds.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      } else {
         return this.listeners.containsKey(short2);
      }
   }

   @Override
   public Collection<PacketListener<?>> getPacketListeners(String string) {
      Short short2 = this.packetIds.get(string);
      if (short2 == null) {
         throw new UnknownPacketException(string);
      }

      Map map = this.listeners.get(short2);
      return map == null ? null : map.values();
   }

   @Override
   public Collection<PacketListener<?>> getPacketListenersById(short s) {
      Map map = this.listeners.get(s);
      return map == null ? null : map.values();
   }
}
