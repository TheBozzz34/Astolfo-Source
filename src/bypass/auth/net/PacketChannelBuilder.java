package bypass.auth.net;

import bypass.util.collection.ShapedMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PacketChannelBuilder {
   private ConnectionListener connectionListener;
   private String label;
   private String secondaryLabel;
   private ShapedMap<Short, PacketReader<?>> readers = new ShapedMap<>();
   private short defaultPacketId;
   private final ShapedMap<Short, List<PacketListener<?>>> listeners = new ShapedMap<>();
   private List<PacketChunk> chunks;
   private PacketOutput packetOutput;
   private int bufferSize;
   private ConnectionOptions options = ConnectionOptions.ofSize(16);
   private ConnectionHandlerFactory handlerFactory;
   private Consumer<IOException> errorHandler;
   private HandshakeAddresses handshakeAddresses;
   private final String name;
   private Runnable onConnect;

   public PacketChannelBuilder(String string) {
      this.name = string;
   }

   public PacketChannelBuilder() {
      this(null);
   }

   public PacketChannelBuilder withConnectionListener(ConnectionListener connectionListener) {
      this.connectionListener = connectionListener;
      return this;
   }

   public PacketChannelBuilder withReader(short s, PacketReader<?> packetReader) {
      this.readers.put(s, packetReader);
      return this;
   }

   public PacketChannelBuilder withLabel(String string) {
      this.label = string;
      return this;
   }

   public PacketChannelBuilder withSecondaryLabel(String string) {
      this.secondaryLabel = string;
      return this;
   }

   public PacketChannelBuilder withListeners(short s, List<PacketListener<?>> list) {
      this.listeners.put(s, list);
      return this;
   }

   public PacketChannelBuilder withDefaultPacketId(short s) {
      this.defaultPacketId = s;
      return this;
   }

   public PacketChannelBuilder withPacketOutput(PacketOutput packetOutput) {
      this.packetOutput = packetOutput;
      return this;
   }

   public PacketChannelBuilder withHandshakeAddresses(HandshakeAddresses handshakeAddresses) {
      if (this.handshakeAddresses != null) {
         throw new SecurityException();
      }

      this.handshakeAddresses = handshakeAddresses;
      return this;
   }

   public PacketChannelBuilder withHandlerFactory(ConnectionHandlerFactory connectionHandlerFactory) {
      this.handlerFactory = connectionHandlerFactory;
      return this;
   }

   public PacketChannelBuilder withoutReader(short s) {
      this.readers.put(s, null);
      return this;
   }

   public PacketChannelBuilder withErrorHandler(Consumer<IOException> consumer) {
      this.errorHandler = consumer;
      return this;
   }

   public PacketChannelBuilder closeStream(InputStream inputStream) throws IOException {
      inputStream.close();
      return this;
   }

   public PacketChannelBuilder withChunks(List<PacketChunk> list) {
      this.chunks = list;
      return this;
   }

   public PacketChannelBuilder addChunk(PacketChunk packetChunk) {
      if (this.chunks == null) {
         this.chunks = new ArrayList<>();
      }

      this.chunks.add(packetChunk);
      return this;
   }

   public PacketChannelBuilder withConnectCallback(Runnable runnable) {
      this.onConnect = runnable;
      return this;
   }

   public PacketChannel build() {
      return new PacketChannel(this.connectionListener, this.readers, this.listeners, this.packetOutput, this.handlerFactory, this.errorHandler, this.onConnect);
   }
}
