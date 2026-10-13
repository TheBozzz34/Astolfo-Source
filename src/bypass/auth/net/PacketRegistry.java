package bypass.auth.net;

import java.util.Collection;

public interface PacketRegistry {
   void D(short var1, String var2);

   Short J(String var1);

   void D(String var1, PacketReader<?> var2);

   PacketReader<?> X(String var1);

   PacketReader<?> r(short var1);

   <T> void addPacketListener(String var1, String var2, PacketListener<T> var3);

   void q(String var1, String var2);

   void S(String var1);

   boolean W(String var1, String var2);

   boolean U(String var1);

   Collection<PacketListener<?>> Y(String var1);

   Collection<PacketListener<?>> y(short var1);
}
