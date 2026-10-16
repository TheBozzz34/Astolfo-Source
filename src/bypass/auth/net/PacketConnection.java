package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PacketConnection extends AbstractNioConnection {
   private final ConnectionListener connectionListener;
   private final Map<Short, PacketReader<?>> readers;
   private final Map<Short, List<PacketListener<?>>> listeners;
   private final PacketOutput packetOutput;
   private final Runnable onOpen;
   private SocketWriter socketWriter;
   private PacketReceiver receiver;
   private SocketChannel socketChannel;

   public PacketConnection(
      ConnectionListener connectionListener,
      Map<Short, PacketReader<?>> map,
      Map<Short, List<PacketListener<?>>> map2,
      PacketOutput packetOutput,
      Runnable runnable
   ) {
      this.connectionListener = connectionListener;
      this.readers = map;
      this.listeners = map2;
      this.packetOutput = packetOutput;
      this.onOpen = runnable;
   }

   @Override
   public void start(SocketChannel socketChannel) throws IOException {
      super.start(socketChannel);
      this.socketChannel = socketChannel;
      this.socketWriter = new SocketWriter(socketChannel, this.selector);
      this.receiver = new PacketReceiver(socketChannel, this.readers, this.listeners);
      this.packetOutput.attach(this.socketWriter);
      this.onOpen.run();

      while (true) {
         try {
            this.selector.select();
         } catch (IOException | ClosedSelectorException value) {
            this.connectionListener.onError(this, "sel", value);
            return;
         }

         Iterator iterator2 = this.selector.selectedKeys().iterator();

         while (iterator2.hasNext()) {
            SelectionKey selectionKey = (SelectionKey)iterator2.next();
            iterator2.remove();
            if (selectionKey.isValid()) {
               if (selectionKey.isWritable()) {
                  try {
                     this.socketWriter.flush();
                  } catch (IOException ioException) {
                     this.connectionListener.onError(this, "write", ioException);
                  }
               } else if (selectionKey.isReadable()) {
                  try {
                     if (this.receiver.receive() == ReceiveResult.C) {
                        this.connectionListener.onClose(this, "eof");
                     }
                  } catch (IOException ioException2) {
                     this.connectionListener.onError(this, "retrieve", ioException2);
                  }
               }
            }
         }
      }
   }

   @Override
   public void close() throws IOException {
      try {
         this.selector.close();
      } finally {
         this.socketChannel.close();
      }
   }
}
