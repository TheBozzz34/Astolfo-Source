package bypass.auth.net;

import java.util.Collection;

public interface PacketRegistry {
   void registerPacketId(short s, String string);

   Short getPacketId(String string);

   void registerReader(String string, PacketReader<?> packetReader);

   PacketReader<?> getReader(String string);

   PacketReader<?> getReaderById(short s);

   <T> void addPacketListener(String string, String string2, PacketListener<T> packetListener);

   void removePacketListener(String string, String string2);

   void removePacketListeners(String string);

   boolean hasPacketListener(String string, String string2);

   boolean hasPacketListeners(String string);

   Collection<PacketListener<?>> getPacketListeners(String string);

   Collection<PacketListener<?>> getPacketListenersById(short s);
}
