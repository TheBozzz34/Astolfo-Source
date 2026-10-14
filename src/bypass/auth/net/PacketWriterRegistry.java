package bypass.auth.net;

import java.util.function.Supplier;

public interface PacketWriterRegistry {
   void j(short s, String string);

   Short S(String string);

   void y(String string, PacketWriter<?> packetWriter);

   PacketWriter<?> w(String string);

   PacketWriter<?> q(short s);

   void D(String string, Supplier<?> supplier);

   Supplier<?> Z(String string);

   Supplier<?> V(short s);
}
