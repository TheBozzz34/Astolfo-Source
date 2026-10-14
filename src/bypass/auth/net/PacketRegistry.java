package bypass.auth.net;

import java.util.Collection;

public interface PacketRegistry {
   void D(short s, String string);

   Short J(String string);

   void D(String string, PacketReader<?> packetReader);

   PacketReader<?> X(String string);

   PacketReader<?> r(short s);

   <T> void addPacketListener(String string, String string2, PacketListener<T> packetListener);

   void q(String string, String string2);

   void S(String string);

   boolean W(String string, String string2);

   boolean U(String string);

   Collection<PacketListener<?>> Y(String string);

   Collection<PacketListener<?>> y(short s);
}
