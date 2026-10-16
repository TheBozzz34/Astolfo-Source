package bypass.auth.net;

import bypass.ClientAccess;
import java.util.Map;
import java.util.function.Supplier;

public final class DefaultPacketSender implements PacketSender, ClientAccess {
   private final Map<Short, Supplier<?>> suppliers;
   private final Map<Short, PacketWriter<?>> writers;
   private final SocketWriter socketWriter;

   public DefaultPacketSender(Map<Short, Supplier<?>> map, Map<Short, PacketWriter<?>> map2, SocketWriter socketWriter) {
      this.suppliers = map;
      this.writers = map2;
      this.socketWriter = socketWriter;
   }

   @Override
   public void sendEmpty(short s) {
      this.send(s, null);
   }

   @Override
   public <T> void send(short s, T object) {
   }
}
