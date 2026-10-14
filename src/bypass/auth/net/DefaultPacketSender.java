package bypass.auth.net;

import bypass.ClientAccess;
import java.util.Map;
import java.util.function.Supplier;

public final class DefaultPacketSender implements PacketSender, ClientAccess {
   private final Map<Short, Supplier<?>> G;
   private final Map<Short, PacketWriter<?>> J;
   private final SocketWriter u;

   public DefaultPacketSender(Map<Short, Supplier<?>> map, Map<Short, PacketWriter<?>> map2, SocketWriter socketWriter) {
      this.G = map;
      this.J = map2;
      this.u = socketWriter;
   }

   @Override
   public void f(short s) {
      this.send(s, null);
   }

   @Override
   public <T> void send(short s, T object) {
   }
}
