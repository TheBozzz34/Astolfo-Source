package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SocketConnection implements Connection {
   public final PacketConnection C;
   public final ConnectionHandlerFactory K;
   private SocketChannel T;

   public SocketConnection(
      ConnectionListener var1,
      Map<Short, PacketReader<?>> var2,
      Map<Short, List<PacketListener<?>>> var3,
      PacketOutput var4,
      ConnectionHandlerFactory var5,
      Consumer<IOException> var6,
      Runnable var7
   ) {
      this.C = new PacketConnection(var1, var2, var3, var4, () -> {
         this.O(var6);
         var7.run();
      });
      this.K = var5;
   }

   private void O(Consumer<IOException> var1) {
      try {
         this.K.y(this.C).c(this.T);
      } catch (IOException var3) {
         var1.accept(var3);
      }
   }

   @Override
   public void open(SocketChannel var1) throws IOException {
      this.T = var1;
      this.C.g(var1);
   }

   @Override
   public void close() throws IOException {
      this.C.close();
      this.T.close();
   }
}
