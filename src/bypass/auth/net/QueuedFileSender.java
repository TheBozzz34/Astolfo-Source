package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class QueuedFileSender extends FileSender {
   private final BlockingQueue<FileSendRequest> z = new LinkedBlockingQueue<>();
   private final Consumer<IOException> c;
   private Thread u;
   private static final long d = 12741492726765L;

   public QueuedFileSender(Consumer<IOException> var1) {
      this.c = var1;
   }

   @Override
   public void open(SocketChannel var1) throws IOException {
      super.open(var1);
      this.u = new Thread(() -> {
         while (!Thread.currentThread().isInterrupted()) {
            FileSendRequest var4;
            try {
               var4 = this.z.take();
            } catch (InterruptedException var7) {
               var7.printStackTrace();
               return;
            }

            try {
               super.y(var4.q, var4.X, var4.g);
            } catch (IOException var6) {
               this.c.accept(var6);
            }
         }
      });
      this.u.start();
   }

   @Override
   public void y(Path var1, String var2, long var3) {
      this.z.add(new FileSendRequest(var2, var1, var3));
   }

   @Override
   public void close() throws IOException {
      this.u.interrupt();
      super.close();
   }
}
