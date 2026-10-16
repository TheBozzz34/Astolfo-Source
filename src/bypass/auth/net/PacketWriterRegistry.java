package bypass.auth.net;

import java.util.function.Supplier;

public interface PacketWriterRegistry {
   void registerPacketId(short s, String string);

   Short getPacketId(String string);

   void registerWriter(String string, PacketWriter<?> packetWriter);

   PacketWriter<?> getWriter(String string);

   PacketWriter<?> getWriterById(short s);

   void registerSupplier(String string, Supplier<?> supplier);

   Supplier<?> getSupplier(String string);

   Supplier<?> getSupplierById(short s);
}
