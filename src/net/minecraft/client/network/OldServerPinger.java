package net.minecraft.client.network;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldServerPinger {
   private static final Splitter PING_RESPONSE_SPLITTER = Splitter.on('\u0000').limit(6);
   private static final Logger logger = LogManager.getLogger();
   private final List<NetworkManager> pingDestinations = Collections.synchronizedList(Lists.newArrayList());
   private static final long b = 138202638132137L;

   public void ping(final ServerData var1) throws UnknownHostException {
      ServerAddress var4 = ServerAddress.fromString(var1.serverIP);
      final NetworkManager var5 = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(var4.getIP()), var4.getPort(), false);
      this.pingDestinations.add(var5);
      var1.serverMOTD = "Pinging...";
      var1.pingToServer = -1L;
      var1.playerList = null;
      var5.setNetHandler(
         new INetHandlerStatusClient() {
            private boolean field_147403_d = false;
            private boolean field_183009_e = false;
            private long field_175092_e = 0L;
            private static final long a = 109236779036732L;

            @Override
            public void handleServerInfo(S00PacketServerInfo var1x) {
               if (this.field_183009_e) {
                  var5.closeChannel(new ChatComponentText("Received unrequested status"));
               } else {
                  this.field_183009_e = true;
                  ServerStatusResponse var4x = var1x.getResponse();
                  if (var4x.getServerDescription() != null) {
                     var1.serverMOTD = var4x.getServerDescription().getFormattedText();
                  } else {
                     var1.serverMOTD = "";
                  }

                  if (var4x.getProtocolVersionInfo() != null) {
                     var1.gameVersion = var4x.getProtocolVersionInfo().getName();
                     var1.version = var4x.getProtocolVersionInfo().getProtocol();
                  } else {
                     var1.gameVersion = "Old";
                     var1.version = 0;
                  }

                  if (var4x.getPlayerCountData() == null) {
                     var1.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
                  } else {
                     var1.populationInfo = EnumChatFormatting.GRAY
                        + ""
                        + var4x.getPlayerCountData().getOnlinePlayerCount()
                        + ""
                        + EnumChatFormatting.DARK_GRAY
                        + "/"
                        + EnumChatFormatting.GRAY
                        + var4x.getPlayerCountData().getMaxPlayers();
                     if (ArrayUtils.isNotEmpty(var4x.getPlayerCountData().getPlayers())) {
                        StringBuilder var5x = new StringBuilder();

                        for (GameProfile var9 : var4x.getPlayerCountData().getPlayers()) {
                           if (var5x.length() > 0) {
                              var5x.append("\n");
                           }

                           var5x.append(var9.getName());
                        }

                        if (var4x.getPlayerCountData().getPlayers().length < var4x.getPlayerCountData().getOnlinePlayerCount()) {
                           if (var5x.length() > 0) {
                              var5x.append("\n");
                           }

                           var5x.append("... and ")
                              .append(var4x.getPlayerCountData().getOnlinePlayerCount() - var4x.getPlayerCountData().getPlayers().length)
                              .append(" more ...");
                        }

                        var1.playerList = var5x.toString();
                     }
                  }

                  if (var4x.getFavicon() != null) {
                     String var10 = var4x.getFavicon();
                     if (var10.startsWith("data:image/png;base64,")) {
                        var1.setBase64EncodedIconData(var10.substring("data:image/png;base64,".length()));
                     } else {
                        OldServerPinger.logger.error("Invalid server icon (unknown format)");
                     }
                  } else {
                     var1.setBase64EncodedIconData((String)null);
                  }

                  this.field_175092_e = Minecraft.getSystemTime();
                  var5.sendPacket(new C01PacketPing(this.field_175092_e));
                  this.field_147403_d = true;
               }
            }

            @Override
            public void handlePong(S01PacketPong var1x) {
               long var2 = this.field_175092_e;
               long var4x = Minecraft.getSystemTime();
               var1.pingToServer = var4x - var2;
               var5.closeChannel(new ChatComponentText("Finished"));
            }

            @Override
            public void onDisconnect(IChatComponent var1x) {
               if (!this.field_147403_d) {
                  OldServerPinger.logger.error("Can't ping " + var1.serverIP + ": " + var1x.getUnformattedText());
                  var1.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                  var1.populationInfo = "";
                  OldServerPinger.this.tryCompatibilityPing(var1);
               }
            }
         }
      );

      try {
         var5.sendPacket(new C00Handshake(47, var4.getIP(), var4.getPort(), EnumConnectionState.STATUS));
         var5.sendPacket(new C00PacketServerQuery());
      } catch (Throwable var7) {
         logger.error(var7);
      }
   }

   private void tryCompatibilityPing(final ServerData var1) {
      final ServerAddress var2 = ServerAddress.fromString(var1.serverIP);
      new Bootstrap().group(NetworkManager.CLIENT_NIO_EVENTLOOP.getValue()).handler(new ChannelInitializer<Channel>() {
         @Override
         public void initChannel(Channel var1x) throws Exception {
            try {
               var1x.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException var3) {
            }

            var1x.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
               private static final long a = 129847495498971L;

               @Override
               public void channelActive(ChannelHandlerContext var1x) throws Exception {
                  super.channelActive(var1x);
                  ByteBuf var4 = Unpooled.buffer();

                  try {
                     var4.writeByte(254);
                     var4.writeByte(1);
                     var4.writeByte(250);
                     char[] var5 = "MC|PingHost".toCharArray();
                     var4.writeShort(var5.length);

                     for (char var9 : var5) {
                        var4.writeChar(var9);
                     }

                     var4.writeShort(7 + 2 * var2.getIP().length());
                     var4.writeByte(127);
                     var5 = var2.getIP().toCharArray();
                     var4.writeShort(var5.length);

                     for (char var17 : var5) {
                        var4.writeChar(var17);
                     }

                     var4.writeInt(var2.getPort());
                     var1x.channel().writeAndFlush(var4).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                  } finally {
                     var4.release();
                  }
               }

               public void channelRead0(ChannelHandlerContext var1x, ByteBuf var2x) throws Exception {
                  short var5 = var2x.readUnsignedByte();
                  if (var5 == 255) {
                     String var6 = new String(var2x.readBytes(var2x.readShort() * 2).array(), Charsets.UTF_16BE);
                     String[] var7 = Iterables.toArray(OldServerPinger.PING_RESPONSE_SPLITTER.split(var6), String.class);
                     if ("§1".equals(var7[0])) {
                        int var8 = MathHelper.parseIntWithDefault(var7[1], 0);
                        String var9 = var7[2];
                        String var10 = var7[3];
                        int var11 = MathHelper.parseIntWithDefault(var7[4], -1);
                        int var12 = MathHelper.parseIntWithDefault(var7[5], -1);
                        var1.version = -1;
                        var1.gameVersion = var9;
                        var1.serverMOTD = var10;
                        var1.populationInfo = EnumChatFormatting.GRAY + "" + var11 + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + var12;
                     }
                  }

                  var1x.close();
               }

               @Override
               public void exceptionCaught(ChannelHandlerContext var1x, Throwable var2x) throws Exception {
                  var1x.close();
               }
            });
         }
      }).channel(NioSocketChannel.class).connect(var2.getIP(), var2.getPort());
   }

   public void pingPendingNetworks() {
      synchronized (this.pingDestinations) {
         Iterator var2 = this.pingDestinations.iterator();

         while (var2.hasNext()) {
            NetworkManager var3 = (NetworkManager)var2.next();
            if (var3.isChannelOpen()) {
               var3.processReceivedPackets();
            } else {
               var2.remove();
               var3.checkDisconnected();
            }
         }
      }
   }

   public void clearPendingNetworks() {
      synchronized (this.pingDestinations) {
         Iterator var2 = this.pingDestinations.iterator();

         while (var2.hasNext()) {
            NetworkManager var3 = (NetworkManager)var2.next();
            if (var3.isChannelOpen()) {
               var2.remove();
               var3.closeChannel(new ChatComponentText("Cancelled"));
            }
         }
      }
   }
}
