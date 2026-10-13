package net.minecraft.network;

import bypass.Astolfo;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.network.ProxyChannelFactory;
import bypass.network.VehicleMovePacket;
import bypass.via.handler.VRDecodeHandler;
import bypass.via.handler.VREncodeHandler;
import bypass.via.protocol.ViaFabricHostnameProtocol;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.crypto.SecretKey;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import net.minecraft.util.Util;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.Display;

public class NetworkManager extends SimpleChannelInboundHandler<Packet> {
   private static final Logger logger = LogManager.getLogger();
   public static final Marker logMarkerNetwork = MarkerManager.getMarker("NETWORK");
   public static final Marker logMarkerPackets = MarkerManager.getMarker("NETWORK_PACKETS", logMarkerNetwork);
   public static final AttributeKey<EnumConnectionState> attrKeyConnectionState = AttributeKey.valueOf("protocol");
   public static final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENTLOOP = new LazyLoadBase<NioEventLoopGroup>() {
      private static final long a = 73612865773636L;

      public NioEventLoopGroup load() {
         return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
      }
   };
   public static final LazyLoadBase<EpollEventLoopGroup> CLIENT_EPOLL_EVENTLOOP = new LazyLoadBase<EpollEventLoopGroup>() {
      private static final long a = 31049252469656L;

      public EpollEventLoopGroup load() {
         return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
      }
   };
   public static final LazyLoadBase<LocalEventLoopGroup> CLIENT_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
      private static final long a = 102606559784122L;

      public LocalEventLoopGroup load() {
         return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
      }
   };
   private final EnumPacketDirection direction;
   private final Queue<NetworkManager.InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
   private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
   private Channel channel;
   private SocketAddress socketAddress;
   private INetHandler packetListener;
   private IChatComponent terminationReason;
   private boolean isEncrypted;
   private boolean disconnected;
   private static final long c = 100253036911086L;
   private static final long g = 6382256058223558756L;

   public NetworkManager(EnumPacketDirection var1) {
      this.direction = var1;
   }

   @Override
   public void channelActive(ChannelHandlerContext var1) throws Exception {
      super.channelActive(var1);
      this.channel = var1.channel();
      this.socketAddress = this.channel.remoteAddress();

      try {
         this.setConnectionState(EnumConnectionState.HANDSHAKING);
      } catch (Throwable var3) {
         logger.fatal(var3);
      }
   }

   public void setConnectionState(EnumConnectionState var1) {
      this.channel.attr(attrKeyConnectionState).set(var1);
      this.channel.config().setAutoRead(true);
      logger.debug("Enabled auto read");
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream"));
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      var2.printStackTrace();
      ChatComponentTranslation var6;
      if (var2 instanceof TimeoutException) {
         var6 = new ChatComponentTranslation("disconnect.timeout");
      } else {
         var6 = new ChatComponentTranslation("disconnect.genericReason", "Internal Exception: " + var2);
      }

      this.closeChannel(var6);
   }

   public void channelRead0(ChannelHandlerContext var1, Packet var2) throws Exception {
      PacketEvent var6 = new PacketEvent(PacketDirection.RECEIVED, var2);
      Astolfo.INSTANCE.getEventBus().post(var6);
      var2 = var6.getPacket();
      if (this.channel.isOpen() && !var6.isCancelled()) {
         try {
            var2.processPacket(this.packetListener);
         } catch (ThreadQuickExitException var8) {
         }
      }
   }

   public void setNetHandler(INetHandler var1) {
      Validate.notNull(var1, "packetListener");
      logger.debug("Set listener of {} to {}", this, var1);
      this.packetListener = var1;
   }

   public void sendPacket(Packet var1) {
      PacketEvent var5 = new PacketEvent(PacketDirection.OUTGOING, var1);
      Astolfo.INSTANCE.getEventBus().post(var5);
      this.w(var1);
      if (!var5.isCancelled()) {
         if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(var5.getPacket(), null);
         } else {
            this.readWriteLock.writeLock().lock();

            try {
               this.outboundPacketsQueue
                  .add(new NetworkManager.InboundHandlerTuplePacketListener(var5.getPacket(), (GenericFutureListener<? extends Future<? super Void>>[])null));
            } finally {
               this.readWriteLock.writeLock().unlock();
            }
         }
      }
   }

   public void B(Packet var1) {
      this.w(var1);
      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchPacket(var1, null);
      } else {
         this.readWriteLock.writeLock().lock();

         try {
            this.outboundPacketsQueue
               .add(new NetworkManager.InboundHandlerTuplePacketListener(var1, (GenericFutureListener<? extends Future<? super Void>>[])null));
         } finally {
            this.readWriteLock.writeLock().unlock();
         }
      }
   }

   private void w(Packet<?> var1) {
   }

   public void sendPacket(
      Packet var1, GenericFutureListener<? extends Future<? super Void>> var2, GenericFutureListener<? extends Future<? super Void>>... var3
   ) {
      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchPacket(var1, ArrayUtils.add(var3, 0, var2));
      } else {
         this.readWriteLock.writeLock().lock();

         try {
            this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(var1, ArrayUtils.add(var3, 0, var2)));
         } finally {
            this.readWriteLock.writeLock().unlock();
         }
      }
   }

   private void dispatchPacket(Packet var1, GenericFutureListener<? extends Future<? super Void>>[] var2) {
      EnumConnectionState var6;
      if (var1 instanceof VehicleMovePacket) {
         var6 = EnumConnectionState.PLAY;
      } else {
         var6 = EnumConnectionState.getFromPacket(var1);
      }

      EnumConnectionState var7 = this.channel.attr(attrKeyConnectionState).get();
      if (!Display.getTitle().equals("Minecraft 1.8.8") && Util.getOSType() == Util.EnumOS.WINDOWS) {
         this.channel.config().setMaxMessagesPerRead(-1);
         this.channel.config().setConnectTimeoutMillis((int)g);
      }

      if (var7 != var6) {
         logger.debug("Disabled auto read");
         this.channel.config().setAutoRead(false);
      }

      if (this.channel.eventLoop().inEventLoop()) {
         if (var6 != var7) {
            this.setConnectionState(var6);
         }

         ChannelFuture var8 = this.channel.writeAndFlush(var1);
         if (var2 != null) {
            var8.addListeners(var2);
         }

         var8.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      } else {
         this.channel.eventLoop().execute(() -> {
            if (var6 != var7) {
               this.setConnectionState(var6);
            }

            ChannelFuture var8x = this.channel.writeAndFlush(var1);
            if (var2 != null) {
               var8x.addListeners(var2);
            }

            var8x.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
         });
      }
   }

   private void flushOutboundQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         this.readWriteLock.readLock().lock();

         try {
            while (!this.outboundPacketsQueue.isEmpty()) {
               NetworkManager.InboundHandlerTuplePacketListener var4 = this.outboundPacketsQueue.poll();
               this.dispatchPacket(var4.packet, var4.futureListeners);
            }
         } finally {
            this.readWriteLock.readLock().unlock();
         }
      }
   }

   public void processReceivedPackets() {
      this.flushOutboundQueue();
      if (this.packetListener instanceof ITickable) {
         ((ITickable)this.packetListener).update();
      }

      this.channel.flush();
   }

   public SocketAddress getRemoteAddress() {
      return this.socketAddress;
   }

   public void closeChannel(IChatComponent var1) {
      if (this.channel.isOpen()) {
         this.channel.close().awaitUninterruptibly();
         this.terminationReason = var1;
      }
   }

   public boolean isLocalChannel() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   public static NetworkManager createNetworkManagerAndConnect(InetAddress var0, int var1, boolean var2) {
      if (Astolfo.INSTANCE.getProxy() != Proxy.NO_PROXY) {
         NetworkManager var9 = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
         Bootstrap var10 = new Bootstrap();
         OioEventLoopGroup var11 = new OioEventLoopGroup();
         var10.channelFactory(new ProxyChannelFactory(Astolfo.INSTANCE.getProxy()));
         var10.group(var11).handler(var9.new lc());
         var10.connect(var0, var1).syncUninterruptibly();
         return var9;
      }

      NetworkManager var6 = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
      Class var7;
      LazyLoadBase var8;
      if (Epoll.isAvailable() && var2) {
         var7 = EpollSocketChannel.class;
         var8 = CLIENT_EPOLL_EVENTLOOP;
      } else {
         var7 = NioSocketChannel.class;
         var8 = CLIENT_NIO_EVENTLOOP;
      }

      new Bootstrap().group((EventLoopGroup)var8.getValue()).handler(var6.new x3()).channel(var7).connect(var0, var1).syncUninterruptibly();
      return var6;
   }

   public static NetworkManager provideLocalClient(SocketAddress var0) {
      final NetworkManager var1 = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
      new Bootstrap().group(CLIENT_LOCAL_EVENTLOOP.getValue()).handler(new ChannelInitializer<Channel>() {
         private static final long a = 69422513303139L;

         @Override
         public void initChannel(Channel var1x) throws Exception {
            var1x.pipeline().addLast("packet_handler", var1);
         }
      }).channel(LocalChannel.class).connect(var0).syncUninterruptibly();
      return var1;
   }

   public void enableEncryption(SecretKey var1) {
      this.isEncrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(2, var1)));
      this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, var1)));
   }

   public boolean getIsencrypted() {
      return this.isEncrypted;
   }

   public boolean isChannelOpen() {
      return this.channel != null && this.channel.isOpen();
   }

   public boolean hasNoChannel() {
      return this.channel == null;
   }

   public INetHandler getNetHandler() {
      return this.packetListener;
   }

   public IChatComponent getExitMessage() {
      return this.terminationReason;
   }

   public void disableAutoRead() {
      this.channel.config().setAutoRead(false);
   }

   public void setCompressionTreshold(int var1) {
      if (var1 >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            ((NettyCompressionDecoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(var1);
         } else {
            this.r(this.channel.pipeline(), "decoder", "decompress", new NettyCompressionDecoder(var1));
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            ((NettyCompressionEncoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(var1);
         } else {
            this.r(this.channel.pipeline(), "encoder", "compress", new NettyCompressionEncoder(var1));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            this.channel.pipeline().remove("compress");
         }
      }
   }

   private ChannelPipeline r(ChannelPipeline var1, String var2, String var3, ChannelHandler var4) {
      switch (var2) {
         case "decoder":
            if (var1.get("via-decoder") != null) {
               var2 = "via-decoder";
            }
            break;
         case "encoder":
            if (var1.get("via-encoder") != null) {
               var2 = "via-encoder";
            }
      }

      return var1.addBefore(var2, var3, var4);
   }

   public void checkDisconnected() {
      if (this.channel != null && !this.channel.isOpen()) {
         if (!this.disconnected) {
            this.disconnected = true;
            if (this.getExitMessage() != null) {
               this.getNetHandler().onDisconnect(this.getExitMessage());
            } else if (this.getNetHandler() != null) {
               this.getNetHandler().onDisconnect(new ChatComponentText("Disconnected"));
            }
         } else {
            logger.warn("handleDisconnection() called twice");
         }
      }
   }

   public static class InboundHandlerTuplePacketListener {
      private final Packet packet;
      private final GenericFutureListener<? extends Future<? super Void>>[] futureListeners;

      public InboundHandlerTuplePacketListener(Packet var1, GenericFutureListener<? extends Future<? super Void>>... var2) {
         this.packet = var1;
         this.futureListeners = var2;
      }
   }

   public final class lc extends ChannelInitializer<Channel> {
      private static final long a = 79766163624635L;
      private static final long e = -777262516479721442L;

      @Override
      public void initChannel(Channel var1) {
         try {
            var1.config().setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
         } catch (ChannelException var5) {
         }

         var1.pipeline()
            .addLast("timeout", new ReadTimeoutHandler((int)e))
            .addLast("splitter", new MessageDeserializer2())
            .addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))
            .addLast("prepender", new MessageSerializer2())
            .addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND))
            .addLast("packet_handler", NetworkManager.this);
      }
   }

   public final class x3 extends ChannelInitializer<Channel> {
      private static final long a = 55943896174766L;
      private static final long e = 2115190527635750942L;

      @Override
      public void initChannel(Channel var1) {
         try {
            var1.config().setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
         } catch (ChannelException var5) {
         }

         var1.pipeline()
            .addLast("timeout", new ReadTimeoutHandler((int)e))
            .addLast("splitter", new MessageDeserializer2())
            .addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))
            .addLast("prepender", new MessageSerializer2())
            .addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND))
            .addLast("packet_handler", NetworkManager.this);
         if (var1 instanceof SocketChannel) {
            UserConnectionImpl var4 = new UserConnectionImpl(var1, true);
            new ProtocolPipelineImpl(var4).add(ViaFabricHostnameProtocol.i);
            var1.pipeline().addBefore("encoder", "via-encoder", new VREncodeHandler(var4));
            var1.pipeline().addBefore("decoder", "via-decoder", new VRDecodeHandler(var4));
         }
      }
   }
}
