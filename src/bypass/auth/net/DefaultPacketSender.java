package bypass.auth.net;

import bypass.ClientAccess;
import java.util.Map;
import java.util.function.Supplier;

public final class DefaultPacketSender implements PacketSender, ClientAccess {
   private final Map<Short, Supplier<?>> G;
   private final Map<Short, PacketWriter<?>> J;
   private final SocketWriter u;

   public DefaultPacketSender(Map<Short, Supplier<?>> var1, Map<Short, PacketWriter<?>> var2, SocketWriter var3) {
      this.G = var1;
      this.J = var2;
      this.u = var3;
   }

   @Override
   public void f(short var1) {
      this.send(var1, null);
   }

   @Override
   public <T> void send(short var1, T var2) {
   }
}
