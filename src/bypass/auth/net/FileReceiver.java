package bypass.auth.net;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class FileReceiver implements Closeable {
   public final FileReceiverListener listener;
   public final boolean threaded;
   public Selector selector;
   public Thread selectThread;

   public FileReceiver(boolean bl) {
      this(new DefaultFileReceiverListener(), bl);
   }

   public FileReceiver(FileReceiverListener fileReceiverListener, boolean bl) {
      this.listener = fileReceiverListener;
      this.threaded = bl;
   }

   public void start() throws IOException {
      this.selector = Selector.open();
      if (this.threaded) {
         this.selectThread = new Thread(this::selectLoop);
         this.selectThread.start();
      } else {
         this.selectLoop();
      }
   }

   private void selectLoop() {
      while (!Thread.currentThread().isInterrupted()) {
         synchronized (this) {
            ;
         }

         try {
            this.selector.select();
         } catch (IOException ioException) {
            this.listener.onReceiverError(this, "sel", ioException);
            return;
         }

         Iterator iterator2 = this.selector.selectedKeys().iterator();

         while (iterator2.hasNext()) {
            SelectionKey selectionKey = (SelectionKey)iterator2.next();
            iterator2.remove();
            if (selectionKey.isValid() && selectionKey.isReadable()) {
               FileReceiveSession fileReceiveSession = (FileReceiveSession)selectionKey.attachment();

               try {
                  fileReceiveSession.handleRead((SocketChannel)selectionKey.channel());
               } catch (IOException ioException2) {
                  this.listener.onSessionError(this, "retrieve", ioException2, fileReceiveSession);
               }
            }
         }
      }
   }

   public void addSession(SocketChannel socketChannel, FileReceiveSession fileReceiveSession) throws IOException {
      socketChannel.configureBlocking(false);
      synchronized (this) {
         this.selector.wakeup();
         socketChannel.register(this.selector, 1, fileReceiveSession);
      }
   }

   @Override
   public void close() throws IOException {
      if (this.selectThread != null) {
         this.selectThread.interrupt();
      }

      this.selector.close();
   }
}
