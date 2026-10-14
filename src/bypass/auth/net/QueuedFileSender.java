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

   public QueuedFileSender(Consumer<IOException> consumer) {
      this.c = consumer;
   }

   @Override
   public void open(SocketChannel socketChannel) throws IOException {
      super.open(socketChannel);
      this.u = new Thread(() -> {
         while (!Thread.currentThread().isInterrupted()) {
            FileSendRequest fileSendRequest;
            try {
               fileSendRequest = this.z.take();
            } catch (InterruptedException interruptedException) {
               interruptedException.printStackTrace();
               return;
            }

            try {
               super.y(fileSendRequest.q, fileSendRequest.X, fileSendRequest.g);
            } catch (IOException ioException) {
               this.c.accept(ioException);
            }
         }
      });
      this.u.start();
   }

   @Override
   public void y(Path path, String string, long l) {
      this.z.add(new FileSendRequest(string, path, l));
   }

   @Override
   public void close() throws IOException {
      this.u.interrupt();
      super.close();
   }
}
