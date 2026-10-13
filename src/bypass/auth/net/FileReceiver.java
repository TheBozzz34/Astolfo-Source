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

   public FileReceiver(boolean var1) {
      this(new DefaultFileReceiverListener(), var1);
   }

   public FileReceiver(FileReceiverListener var1, boolean var2) {
      this.g = var1;
      this.r = var2;
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
         } catch (IOException var9) {
            this.g.C(this, "sel", var9);
            return;
         }

         Iterator var4 = this.c.selectedKeys().iterator();

         while (var4.hasNext()) {
            SelectionKey var5 = (SelectionKey)var4.next();
            var4.remove();
            if (var5.isValid() && var5.isReadable()) {
               FileReceiveSession var6 = (FileReceiveSession)var5.attachment();

               try {
                  var6.e((SocketChannel)var5.channel());
               } catch (IOException var8) {
                  this.g.q(this, "retrieve", var8, var6);
               }
            }
         }
      }
   }

   public void f(SocketChannel var1, FileReceiveSession var2) throws IOException {
      var1.configureBlocking(false);
      synchronized (this) {
         this.c.wakeup();
         var1.register(this.c, 1, var2);
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
