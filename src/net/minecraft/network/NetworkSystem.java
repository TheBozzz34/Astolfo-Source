package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.network.NetHandlerHandshakeMemory;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerHandshakeTCP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {
   private static final Logger logger = LogManager.getLogger();
   public static final LazyLoadBase<NioEventLoopGroup> eventLoops = new LazyLoadBase<NioEventLoopGroup>() {
      private static final long a = 76088953134290L;

      public NioEventLoopGroup load() {
         return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build());
      }
   };
   public static final LazyLoadBase<EpollEventLoopGroup> SERVER_EPOLL_EVENTLOOP = new LazyLoadBase<EpollEventLoopGroup>() {
      private static final long a = 33813493627961L;

      public EpollEventLoopGroup load() {
         return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
      }
   };
   public static final LazyLoadBase<LocalEventLoopGroup> SERVER_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
      private static final long a = 28341276148751L;

      public LocalEventLoopGroup load() {
         return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Server IO #%d").setDaemon(true).build());
      }
   };
   private final MinecraftServer mcServer;
   public volatile boolean isAlive;
   private final List<ChannelFuture> endpoints = Collections.synchronizedList(Lists.newArrayList());
   private final List<NetworkManager> networkManagers = Collections.synchronizedList(Lists.newArrayList());
   private static final long a = 110037823986427L;

   public NetworkSystem(MinecraftServer var1) {
      this.mcServer = var1;
      this.isAlive = true;
   }

   public void addLanEndpoint(InetAddress var1, int var2) throws IOException {
      synchronized (this.endpoints) {
         Class var7;
         LazyLoadBase var8;
         if (Epoll.isAvailable() && this.mcServer.shouldUseNativeTransport()) {
            var7 = EpollServerSocketChannel.class;
            var8 = SERVER_EPOLL_EVENTLOOP;
            logger.info("Using epoll channel type");
         } else {
            var7 = NioServerSocketChannel.class;
            var8 = eventLoops;
            logger.info("Using default channel type");
         }

         this.endpoints
            .add(
               new ServerBootstrap()
                  .channel(var7)
                  .childHandler(
                     new ChannelInitializer<Channel>() {
                        private static final long a = 123987738429221L;
                        private static final long f = 8343289244554362910L;

                        @Override
                        public void initChannel(Channel var1) throws Exception {
                           try {
                              var1.config().setOption(ChannelOption.TCP_NODELAY, true);
                           } catch (ChannelException var6) {
                           }

                           var1.pipeline()
                              .addLast("timeout", new ReadTimeoutHandler((int)f))
                              .addLast("legacy_query", new PingResponseHandler(NetworkSystem.this))
                              .addLast("splitter", new MessageDeserializer2())
                              .addLast("decoder", new MessageDeserializer(EnumPacketDirection.SERVERBOUND))
                              .addLast("prepender", new MessageSerializer2())
                              .addLast("encoder", new MessageSerializer(EnumPacketDirection.CLIENTBOUND));
                           NetworkManager var5 = new NetworkManager(EnumPacketDirection.SERVERBOUND);
                           NetworkSystem.this.networkManagers.add(var5);
                           var1.pipeline().addLast("packet_handler", var5);
                           var5.setNetHandler(new NetHandlerHandshakeTCP(NetworkSystem.this.mcServer, var5));
                        }
                     }
                  )
                  .group((EventLoopGroup)var8.getValue())
                  .localAddress(var1, var2)
                  .bind()
                  .syncUninterruptibly()
            );
      }
   }

   public SocketAddress addLocalEndpoint() {
      ChannelFuture var1;
      synchronized (this.endpoints) {
         var1 = new ServerBootstrap().channel(LocalServerChannel.class).childHandler(new ChannelInitializer<Channel>() {
            private static final long a = 106018327987801L;

            @Override
            public void initChannel(Channel var1) throws Exception {
               NetworkManager var4 = new NetworkManager(EnumPacketDirection.SERVERBOUND);
               var4.setNetHandler(new NetHandlerHandshakeMemory(NetworkSystem.this.mcServer, var4));
               NetworkSystem.this.networkManagers.add(var4);
               var1.pipeline().addLast("packet_handler", var4);
            }
         }).group(eventLoops.getValue()).localAddress(LocalAddress.ANY).bind().syncUninterruptibly();
         this.endpoints.add(var1);
      }

      return var1.channel().localAddress();
   }

   public void terminateEndpoints() {
      this.isAlive = false;

      for (ChannelFuture var5 : this.endpoints) {
         try {
            var5.channel().close().sync();
         } catch (InterruptedException var7) {
            logger.error("Interrupted whilst closing channel");
         }
      }
   }

   public void networkTick() {
      synchronized (this.networkManagers) {
         Iterator var5 = this.networkManagers.iterator();

         while (var5.hasNext()) {
            final NetworkManager var6 = (NetworkManager)var5.next();
            if (!var6.hasNoChannel()) {
               if (!var6.isChannelOpen()) {
                  var5.remove();
                  var6.checkDisconnected();
               } else {
                  try {
                     var6.processReceivedPackets();
                  } catch (Exception var11) {
                     if (var6.isLocalChannel()) {
                        CrashReport var13 = CrashReport.makeCrashReport(var11, "Ticking memory connection");
                        CrashReportCategory var9 = var13.makeCategory("Ticking connection");
                        var9.addCrashSectionCallable("Connection", new Callable<String>() {
                           public String call() throws Exception {
                              return var6.toString();
                           }
                        });
                        throw new ReportedException(var13);
                     }

                     logger.warn("Failed to handle packet for " + var6.getRemoteAddress(), var11);
                     final ChatComponentText var8 = new ChatComponentText("Internal server error");
                     var6.sendPacket(new S40PacketDisconnect(var8), new GenericFutureListener<Future<? super Void>>() {
                        @Override
                        public void operationComplete(Future<? super Void> var1) throws Exception {
                           var6.closeChannel(var8);
                        }
                     });
                     var6.disableAutoRead();
                  }
               }
            }
         }
      }
   }

   public MinecraftServer getServer() {
      return this.mcServer;
   }
}
