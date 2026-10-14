package bypass.auth.net;

import bypass.util.collection.ShapedMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PacketChannelBuilder {
   private ConnectionListener f;
   private String G;
   private String W;
   private ShapedMap<Short, PacketReader<?>> o = new ShapedMap<>();
   private short k;
   private final ShapedMap<Short, List<PacketListener<?>>> J = new ShapedMap<>();
   private List<PacketChunk> e;
   private PacketOutput P;
   private int I;
   private ConnectionOptions i;
   private ConnectionHandlerFactory p;
   private Consumer<IOException> j;
   private HandshakeAddresses R;
   private final String m;
   private Runnable t;
   private static final long a = 137678236868828L;
   private static final long b = 4272540870860865552L;

   public PacketChannelBuilder(String string) {
      this.i = ConnectionOptions.M((int)b);
      this.m = string;
   }

   public PacketChannelBuilder() {
      this(null);
   }

   public PacketChannelBuilder z(ConnectionListener connectionListener) {
      this.f = connectionListener;
      return this;
   }

   public PacketChannelBuilder V(short s, PacketReader<?> packetReader) {
      this.o.put(s, packetReader);
      return this;
   }

   public PacketChannelBuilder T(String string) {
      this.G = string;
      return this;
   }

   public PacketChannelBuilder J(String string) {
      this.W = string;
      return this;
   }

   public PacketChannelBuilder Z(short s, List<PacketListener<?>> list) {
      this.J.put(s, list);
      return this;
   }

   public PacketChannelBuilder n(short s) {
      this.k = s;
      return this;
   }

   public PacketChannelBuilder Q(PacketOutput packetOutput) {
      this.P = packetOutput;
      return this;
   }

   public PacketChannelBuilder M(HandshakeAddresses handshakeAddresses) {
      if (this.R != null) {
         throw new SecurityException();
      }

      this.R = handshakeAddresses;
      return this;
   }

   public PacketChannelBuilder a(ConnectionHandlerFactory connectionHandlerFactory) {
      this.p = connectionHandlerFactory;
      return this;
   }

   public PacketChannelBuilder V(short s) {
      this.o.put(s, null);
      return this;
   }

   public PacketChannelBuilder A(Consumer<IOException> consumer) {
      this.j = consumer;
      return this;
   }

   public PacketChannelBuilder X(InputStream inputStream) throws IOException {
      inputStream.close();
      return this;
   }

   public PacketChannelBuilder e(List<PacketChunk> list) {
      this.e = list;
      return this;
   }

   public PacketChannelBuilder I(PacketChunk packetChunk) {
      if (this.e == null) {
         this.e = new ArrayList<>();
      }

      this.e.add(packetChunk);
      return this;
   }

   public PacketChannelBuilder K(Runnable runnable) {
      this.t = runnable;
      return this;
   }

   public PacketChannel C() {
      return new PacketChannel(this.f, this.o, this.J, this.P, this.p, this.j, this.t);
   }
}
