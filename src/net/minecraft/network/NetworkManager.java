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
      public NioEventLoopGroup load() {
         return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
      }
   };
   public static final LazyLoadBase<EpollEventLoopGroup> CLIENT_EPOLL_EVENTLOOP = new LazyLoadBase<EpollEventLoopGroup>() {
      public EpollEventLoopGroup load() {
         return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
      }
   };
   public static final LazyLoadBase<LocalEventLoopGroup> CLIENT_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
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

   public NetworkManager(EnumPacketDirection packetDirection) {
      this.direction = packetDirection;
   }

   @Override
   public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
      super.channelActive(channelHandlerContext);
      this.channel = channelHandlerContext.channel();
      this.socketAddress = this.channel.remoteAddress();

      try {
         this.setConnectionState(EnumConnectionState.HANDSHAKING);
      } catch (Throwable throwable) {
         logger.fatal(throwable);
      }
   }

   public void setConnectionState(EnumConnectionState newState) {
      this.channel.attr(attrKeyConnectionState).set(newState);
      this.channel.config().setAutoRead(true);
      logger.debug("Enabled auto read");
   }

   @Override
   public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
      this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream"));
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
      throwable.printStackTrace();
      ChatComponentTranslation chatComponentTranslation;
      if (throwable instanceof TimeoutException) {
         chatComponentTranslation = new ChatComponentTranslation("disconnect.timeout");
      } else {
         chatComponentTranslation = new ChatComponentTranslation("disconnect.genericReason", "Internal Exception: " + throwable);
      }

      this.closeChannel(chatComponentTranslation);
   }

   public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
      PacketEvent packetEvent = new PacketEvent(PacketDirection.RECEIVED, packet);
      Astolfo.INSTANCE.getEventBus().post(packetEvent);
      packet = packetEvent.getPacket();
      if (this.channel.isOpen() && !packetEvent.isCancelled()) {
         try {
            packet.processPacket(this.packetListener);
         } catch (ThreadQuickExitException threadQuickExitException) {
         }
      }
   }

   public void setNetHandler(INetHandler handler) {
      Validate.notNull(handler, "packetListener");
      logger.debug("Set listener of {} to {}", this, handler);
      this.packetListener = handler;
   }

   public void sendPacket(Packet packetIn) {
      PacketEvent packetEvent = new PacketEvent(PacketDirection.OUTGOING, packetIn);
      Astolfo.INSTANCE.getEventBus().post(packetEvent);
      this.onPacketSending(packetIn);
      if (!packetEvent.isCancelled()) {
         if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetEvent.getPacket(), null);
         } else {
            this.readWriteLock.writeLock().lock();

            try {
               this.outboundPacketsQueue
                  .add(new NetworkManager.InboundHandlerTuplePacketListener(packetEvent.getPacket(), (GenericFutureListener<? extends Future<? super Void>>[])null));
            } finally {
               this.readWriteLock.writeLock().unlock();
            }
         }
      }
   }

   public void sendPacketNoEvent(Packet packet) {
      this.onPacketSending(packet);
      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchPacket(packet, null);
      } else {
         this.readWriteLock.writeLock().lock();

         try {
            this.outboundPacketsQueue
               .add(new NetworkManager.InboundHandlerTuplePacketListener(packet, (GenericFutureListener<? extends Future<? super Void>>[])null));
         } finally {
            this.readWriteLock.writeLock().unlock();
         }
      }
   }

   private void onPacketSending(Packet<?> packet) {
   }

   public void sendPacket(
      Packet packetIn, GenericFutureListener<? extends Future<? super Void>> listener, GenericFutureListener<? extends Future<? super Void>>... listeners
   ) {
      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchPacket(packetIn, ArrayUtils.add(listeners, 0, listener));
      } else {
         this.readWriteLock.writeLock().lock();

         try {
            this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, ArrayUtils.add(listeners, 0, listener)));
         } finally {
            this.readWriteLock.writeLock().unlock();
         }
      }
   }

   private void dispatchPacket(Packet inPacket, GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
      EnumConnectionState enumConnectionState;
      if (inPacket instanceof VehicleMovePacket) {
         enumConnectionState = EnumConnectionState.PLAY;
      } else {
         enumConnectionState = EnumConnectionState.getFromPacket(inPacket);
      }

      EnumConnectionState enumConnectionState2 = this.channel.attr(attrKeyConnectionState).get();
      if (!Display.getTitle().equals("Minecraft 1.8.8") && Util.getOSType() == Util.EnumOS.WINDOWS) {
         this.channel.config().setMaxMessagesPerRead(-1);
         this.channel.config().setConnectTimeoutMillis(100);
      }

      if (enumConnectionState2 != enumConnectionState) {
         logger.debug("Disabled auto read");
         this.channel.config().setAutoRead(false);
      }

      if (this.channel.eventLoop().inEventLoop()) {
         if (enumConnectionState != enumConnectionState2) {
            this.setConnectionState(enumConnectionState);
         }

         ChannelFuture channelFuture = this.channel.writeAndFlush(inPacket);
         if (futureListeners != null) {
            channelFuture.addListeners(futureListeners);
         }

         channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      } else {
         this.channel.eventLoop().execute(() -> {
            if (enumConnectionState != enumConnectionState2) {
               this.setConnectionState(enumConnectionState);
            }

            ChannelFuture channelFuture2 = this.channel.writeAndFlush(inPacket);
            if (futureListeners != null) {
               channelFuture2.addListeners(futureListeners);
            }

            channelFuture2.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
         });
      }
   }

   private void flushOutboundQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         this.readWriteLock.readLock().lock();

         try {
            while (!this.outboundPacketsQueue.isEmpty()) {
               NetworkManager.InboundHandlerTuplePacketListener inboundHandlerTuplePacketListener = this.outboundPacketsQueue.poll();
               this.dispatchPacket(inboundHandlerTuplePacketListener.packet, inboundHandlerTuplePacketListener.futureListeners);
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

   public void closeChannel(IChatComponent message) {
      if (this.channel.isOpen()) {
         this.channel.close().awaitUninterruptibly();
         this.terminationReason = message;
      }
   }

   public boolean isLocalChannel() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   public static NetworkManager createNetworkManagerAndConnect(InetAddress address, int serverPort, boolean useNativeTransport) {
      if (Astolfo.INSTANCE.getProxy() != Proxy.NO_PROXY) {
         NetworkManager networkManager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
         Bootstrap bootstrap = new Bootstrap();
         OioEventLoopGroup oioEventLoopGroup = new OioEventLoopGroup();
         bootstrap.channelFactory(new ProxyChannelFactory(Astolfo.INSTANCE.getProxy()));
         bootstrap.group(oioEventLoopGroup).handler(networkManager.new lc());
         bootstrap.connect(address, serverPort).syncUninterruptibly();
         return networkManager;
      }

      NetworkManager networkManager2 = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
      Class class2;
      LazyLoadBase lazyLoadBase;
      if (Epoll.isAvailable() && useNativeTransport) {
         class2 = EpollSocketChannel.class;
         lazyLoadBase = CLIENT_EPOLL_EVENTLOOP;
      } else {
         class2 = NioSocketChannel.class;
         lazyLoadBase = CLIENT_NIO_EVENTLOOP;
      }

      new Bootstrap().group((EventLoopGroup)lazyLoadBase.getValue()).handler(networkManager2.new x3()).channel(class2).connect(address, serverPort).syncUninterruptibly();
      return networkManager2;
   }

   public static NetworkManager provideLocalClient(SocketAddress address) {
      final NetworkManager networkManager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
      new Bootstrap().group(CLIENT_LOCAL_EVENTLOOP.getValue()).handler(new ChannelInitializer<Channel>() {
         @Override
         public void initChannel(Channel channel2) throws Exception {
            channel2.pipeline().addLast("packet_handler", networkManager);
         }
      }).channel(LocalChannel.class).connect(address).syncUninterruptibly();
      return networkManager;
   }

   public void enableEncryption(SecretKey key) {
      this.isEncrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(2, key)));
      this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, key)));
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

   public void setCompressionTreshold(int treshold) {
      if (treshold >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            ((NettyCompressionDecoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
         } else {
            this.addHandlerBeforeVia(this.channel.pipeline(), "decoder", "decompress", new NettyCompressionDecoder(treshold));
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            ((NettyCompressionEncoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
         } else {
            this.addHandlerBeforeVia(this.channel.pipeline(), "encoder", "compress", new NettyCompressionEncoder(treshold));
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

   private ChannelPipeline addHandlerBeforeVia(ChannelPipeline channelPipeline, String string, String string2, ChannelHandler channelHandler) {
      switch (string) {
         case "decoder":
            if (channelPipeline.get("via-decoder") != null) {
               string = "via-decoder";
            }
            break;
         case "encoder":
            if (channelPipeline.get("via-encoder") != null) {
               string = "via-encoder";
            }
      }

      return channelPipeline.addBefore(string, string2, channelHandler);
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

      public InboundHandlerTuplePacketListener(Packet inPacket, GenericFutureListener<? extends Future<? super Void>>... inFutureListeners) {
         this.packet = inPacket;
         this.futureListeners = inFutureListeners;
      }
   }

   public final class lc extends ChannelInitializer<Channel> {
      @Override
      public void initChannel(Channel channel2) {
         try {
            channel2.config().setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
         } catch (ChannelException channelException) {
         }

         channel2.pipeline()
            .addLast("timeout", new ReadTimeoutHandler(30))
            .addLast("splitter", new MessageDeserializer2())
            .addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))
            .addLast("prepender", new MessageSerializer2())
            .addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND))
            .addLast("packet_handler", NetworkManager.this);
      }
   }

   public final class x3 extends ChannelInitializer<Channel> {
      @Override
      public void initChannel(Channel channel2) {
         try {
            channel2.config().setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
         } catch (ChannelException channelException) {
         }

         channel2.pipeline()
            .addLast("timeout", new ReadTimeoutHandler(30))
            .addLast("splitter", new MessageDeserializer2())
            .addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))
            .addLast("prepender", new MessageSerializer2())
            .addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND))
            .addLast("packet_handler", NetworkManager.this);
         if (channel2 instanceof SocketChannel) {
            UserConnectionImpl userConnectionImpl = new UserConnectionImpl(channel2, true);
            new ProtocolPipelineImpl(userConnectionImpl).add(ViaFabricHostnameProtocol.instance);
            channel2.pipeline().addBefore("encoder", "via-encoder", new VREncodeHandler(userConnectionImpl));
            channel2.pipeline().addBefore("decoder", "via-decoder", new VRDecodeHandler(userConnectionImpl));
         }
      }
   }
}
