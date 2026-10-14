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

   public PacketConnection(
      ConnectionListener connectionListener,
      Map<Short, PacketReader<?>> map,
      Map<Short, List<PacketListener<?>>> map2,
      PacketOutput packetOutput,
      Runnable runnable
   ) {
      this.v = connectionListener;
      this.x = map;
      this.I = map2;
      this.c = packetOutput;
      this.M = runnable;
   }

   @Override
   public void g(SocketChannel socketChannel) throws IOException {
      super.g(socketChannel);
      this.Z = socketChannel;
      this.V = new SocketWriter(socketChannel, this.K);
      this.A = new PacketReceiver(socketChannel, this.x, this.I);
      this.c.attach(this.V);
      this.M.run();

      while (true) {
         try {
            this.K.select();
         } catch (IOException | ClosedSelectorException value) {
            this.v.onError(this, "sel", value);
            return;
         }

         Iterator iterator2 = this.K.selectedKeys().iterator();

         while (iterator2.hasNext()) {
            SelectionKey selectionKey = (SelectionKey)iterator2.next();
            iterator2.remove();
            if (selectionKey.isValid()) {
               if (selectionKey.isWritable()) {
                  try {
                     this.V.j();
                  } catch (IOException ioException) {
                     this.v.onError(this, "write", ioException);
                  }
               } else if (selectionKey.isReadable()) {
                  try {
                     if (this.A.receive() == ReceiveResult.C) {
                        this.v.onClose(this, "eof");
                     }
                  } catch (IOException ioException2) {
                     this.v.onError(this, "retrieve", ioException2);
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
