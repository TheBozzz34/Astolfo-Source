package bypass.auth.net;

import java.util.function.Supplier;

public interface PacketWriterRegistry {
   void j(short var1, String var2);

   Short S(String var1);

   void y(String var1, PacketWriter<?> var2);

   PacketWriter<?> w(String var1);

   PacketWriter<?> q(short var1);

   void D(String var1, Supplier<?> var2);

   Supplier<?> Z(String var1);

   Supplier<?> V(short var1);
}
