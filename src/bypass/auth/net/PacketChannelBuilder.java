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

   public PacketChannelBuilder(String var1) {
      this.i = ConnectionOptions.M((int)b);
      this.m = var1;
   }

   public PacketChannelBuilder() {
      this(null);
   }

   public PacketChannelBuilder z(ConnectionListener var1) {
      this.f = var1;
      return this;
   }

   public PacketChannelBuilder V(short var1, PacketReader<?> var2) {
      this.o.put(var1, var2);
      return this;
   }

   public PacketChannelBuilder T(String var1) {
      this.G = var1;
      return this;
   }

   public PacketChannelBuilder J(String var1) {
      this.W = var1;
      return this;
   }

   public PacketChannelBuilder Z(short var1, List<PacketListener<?>> var2) {
      this.J.put(var1, var2);
      return this;
   }

   public PacketChannelBuilder n(short var1) {
      this.k = var1;
      return this;
   }

   public PacketChannelBuilder Q(PacketOutput var1) {
      this.P = var1;
      return this;
   }

   public PacketChannelBuilder M(HandshakeAddresses var1) {
      if (this.R != null) {
         throw new SecurityException();
      }

      this.R = var1;
      return this;
   }

   public PacketChannelBuilder a(ConnectionHandlerFactory var1) {
      this.p = var1;
      return this;
   }

   public PacketChannelBuilder V(short var1) {
      this.o.put(var1, null);
      return this;
   }

   public PacketChannelBuilder A(Consumer<IOException> var1) {
      this.j = var1;
      return this;
   }

   public PacketChannelBuilder X(InputStream var1) throws IOException {
      var1.close();
      return this;
   }

   public PacketChannelBuilder e(List<PacketChunk> var1) {
      this.e = var1;
      return this;
   }

   public PacketChannelBuilder I(PacketChunk var1) {
      if (this.e == null) {
         this.e = new ArrayList<>();
      }

      this.e.add(var1);
      return this;
   }

   public PacketChannelBuilder K(Runnable var1) {
      this.t = var1;
      return this;
   }

   public PacketChannel C() {
      return new PacketChannel(this.f, this.o, this.J, this.P, this.p, this.j, this.t);
   }
}
