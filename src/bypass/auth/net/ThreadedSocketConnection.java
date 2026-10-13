package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ThreadedSocketConnection extends SocketConnection {
   private final Consumer<IOException> o;
   private Thread V;
   private static final long a = 52925930189940L;

   public ThreadedSocketConnection(
      ConnectionListener var1,
      Map<Short, PacketReader<?>> var2,
      Map<Short, List<PacketListener<?>>> var3,
      PacketOutput var4,
      ConnectionHandlerFactory var5,
      Consumer<IOException> var6,
      Consumer<IOException> var7,
      Runnable var8
   ) {
      super(var1, var2, var3, var4, var5, var6, var8);
      this.o = var7;
   }

   @Override
   public void open(SocketChannel var1) {
      this.V = new Thread(() -> {
         try {
            super.open(var1);
         } catch (IOException var3) {
            this.o.accept(var3);
         }
      });
      this.V.start();
   }

   @Override
   public void close() throws IOException {
      if (this.V != null) {
         this.V.interrupt();
      }

      super.close();
   }
}
