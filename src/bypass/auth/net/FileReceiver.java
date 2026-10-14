package bypass.auth.net;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class FileReceiver implements Closeable {
   public final FileReceiverListener g;
   public final boolean r;
   public Selector c;
   public Thread h;
   private static final long a = 26433994730827L;

   public FileReceiver(boolean bl) {
      this(new DefaultFileReceiverListener(), bl);
   }

   public FileReceiver(FileReceiverListener fileReceiverListener, boolean bl) {
      this.g = fileReceiverListener;
      this.r = bl;
   }

   public void R() throws IOException {
      this.c = Selector.open();
      if (this.r) {
         this.h = new Thread(this::M);
         this.h.start();
      } else {
         this.M();
      }
   }

   private void M() {
      while (!Thread.currentThread().isInterrupted()) {
         synchronized (this) {
            ;
         }

         try {
            this.c.select();
         } catch (IOException ioException) {
            this.g.C(this, "sel", ioException);
            return;
         }

         Iterator iterator2 = this.c.selectedKeys().iterator();

         while (iterator2.hasNext()) {
            SelectionKey selectionKey = (SelectionKey)iterator2.next();
            iterator2.remove();
            if (selectionKey.isValid() && selectionKey.isReadable()) {
               FileReceiveSession fileReceiveSession = (FileReceiveSession)selectionKey.attachment();

               try {
                  fileReceiveSession.e((SocketChannel)selectionKey.channel());
               } catch (IOException ioException2) {
                  this.g.q(this, "retrieve", ioException2, fileReceiveSession);
               }
            }
         }
      }
   }

   public void f(SocketChannel socketChannel, FileReceiveSession fileReceiveSession) throws IOException {
      socketChannel.configureBlocking(false);
      synchronized (this) {
         this.c.wakeup();
         socketChannel.register(this.c, 1, fileReceiveSession);
      }
   }

   @Override
   public void close() throws IOException {
      if (this.h != null) {
         this.h.interrupt();
      }

      this.c.close();
   }
}
