package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class QueuedFileSender extends FileSender {
   private final BlockingQueue<FileSendRequest> sendQueue = new LinkedBlockingQueue<>();
   private final Consumer<IOException> errorHandler;
   private Thread senderThread;

   public QueuedFileSender(Consumer<IOException> consumer) {
      this.errorHandler = consumer;
   }

   @Override
   public void open(SocketChannel socketChannel) throws IOException {
      super.open(socketChannel);
      this.senderThread = new Thread(() -> {
         while (!Thread.currentThread().isInterrupted()) {
            FileSendRequest fileSendRequest;
            try {
               fileSendRequest = this.sendQueue.take();
            } catch (InterruptedException interruptedException) {
               interruptedException.printStackTrace();
               return;
            }

            try {
               super.sendNamedFile(fileSendRequest.path, fileSendRequest.fileName, fileSendRequest.chunkSize);
            } catch (IOException ioException) {
               this.errorHandler.accept(ioException);
            }
         }
      });
      this.senderThread.start();
   }

   @Override
   public void sendNamedFile(Path path, String string, long l) {
      this.sendQueue.add(new FileSendRequest(string, path, l));
   }

   @Override
   public void close() throws IOException {
      this.senderThread.interrupt();
      super.close();
   }
}
