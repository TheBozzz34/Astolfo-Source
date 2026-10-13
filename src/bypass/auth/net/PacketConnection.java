package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PacketConnection extends AbstractNioConnection {
   private final ConnectionListener v;
   private final Map<Short, PacketReader<?>> x;
   private final Map<Short, List<PacketListener<?>>> I;
   private final PacketOutput c;
   private final Runnable M;
   private SocketWriter V;
   private PacketReceiver A;
   private SocketChannel Z;
   private static final long a = 34632761044039L;

   public PacketConnection(
      ConnectionListener var1, Map<Short, PacketReader<?>> var2, Map<Short, List<PacketListener<?>>> var3, PacketOutput var4, Runnable var5
   ) {
      this.v = var1;
      this.x = var2;
      this.I = var3;
      this.c = var4;
      this.M = var5;
   }

   @Override
   public void g(SocketChannel var1) throws IOException {
      super.g(var1);
      this.Z = var1;
      this.V = new SocketWriter(var1, this.K);
      this.A = new PacketReceiver(var1, this.x, this.I);
      this.c.attach(this.V);
      this.M.run();

      while (true) {
         try {
            this.K.select();
         } catch (IOException | ClosedSelectorException var10) {
            this.v.onError(this, "sel", var10);
            return;
         }

         Iterator var5 = this.K.selectedKeys().iterator();

         while (var5.hasNext()) {
            SelectionKey var6 = (SelectionKey)var5.next();
            var5.remove();
            if (var6.isValid()) {
               if (var6.isWritable()) {
                  try {
                     this.V.j();
                  } catch (IOException var8) {
                     this.v.onError(this, "write", var8);
                  }
               } else if (var6.isReadable()) {
                  try {
                     if (this.A.receive() == ReceiveResult.C) {
                        this.v.onClose(this, "eof");
                     }
                  } catch (IOException var9) {
                     this.v.onError(this, "retrieve", var9);
                  }
               }
            }
         }
      }
   }

   @Override
   public void close() throws IOException {
      try {
         this.K.close();
      } finally {
         this.Z.close();
      }
   }
}
