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

   public void ping(final ServerData server) throws UnknownHostException {
      ServerAddress serverAddress = ServerAddress.fromString(server.serverIP);
      final NetworkManager networkManager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(serverAddress.getIP()), serverAddress.getPort(), false);
      this.pingDestinations.add(networkManager);
      server.serverMOTD = "Pinging...";
      server.pingToServer = -1L;
      server.playerList = null;
      networkManager.setNetHandler(
         new INetHandlerStatusClient() {
            private boolean field_147403_d = false;
            private boolean field_183009_e = false;
            private long field_175092_e = 0L;

            @Override
            public void handleServerInfo(S00PacketServerInfo packetIn) {
               if (this.field_183009_e) {
                  networkManager.closeChannel(new ChatComponentText("Received unrequested status"));
               } else {
                  this.field_183009_e = true;
                  ServerStatusResponse serverStatusResponse = packetIn.getResponse();
                  if (serverStatusResponse.getServerDescription() != null) {
                     server.serverMOTD = serverStatusResponse.getServerDescription().getFormattedText();
                  } else {
                     server.serverMOTD = "";
                  }

                  if (serverStatusResponse.getProtocolVersionInfo() != null) {
                     server.gameVersion = serverStatusResponse.getProtocolVersionInfo().getName();
                     server.version = serverStatusResponse.getProtocolVersionInfo().getProtocol();
                  } else {
                     server.gameVersion = "Old";
                     server.version = 0;
                  }

                  if (serverStatusResponse.getPlayerCountData() == null) {
                     server.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
                  } else {
                     server.populationInfo = EnumChatFormatting.GRAY
                        + ""
                        + serverStatusResponse.getPlayerCountData().getOnlinePlayerCount()
                        + ""
                        + EnumChatFormatting.DARK_GRAY
                        + "/"
                        + EnumChatFormatting.GRAY
                        + serverStatusResponse.getPlayerCountData().getMaxPlayers();
                     if (ArrayUtils.isNotEmpty(serverStatusResponse.getPlayerCountData().getPlayers())) {
                        StringBuilder stringBuilder = new StringBuilder();

                        for (GameProfile gameProfile : serverStatusResponse.getPlayerCountData().getPlayers()) {
                           if (stringBuilder.length() > 0) {
                              stringBuilder.append("\n");
                           }

                           stringBuilder.append(gameProfile.getName());
                        }

                        if (serverStatusResponse.getPlayerCountData().getPlayers().length < serverStatusResponse.getPlayerCountData().getOnlinePlayerCount()) {
                           if (stringBuilder.length() > 0) {
                              stringBuilder.append("\n");
                           }

                           stringBuilder.append("... and ")
                              .append(serverStatusResponse.getPlayerCountData().getOnlinePlayerCount() - serverStatusResponse.getPlayerCountData().getPlayers().length)
                              .append(" more ...");
                        }

                        server.playerList = stringBuilder.toString();
                     }
                  }

                  if (serverStatusResponse.getFavicon() != null) {
                     String string = serverStatusResponse.getFavicon();
                     if (string.startsWith("data:image/png;base64,")) {
                        server.setBase64EncodedIconData(string.substring("data:image/png;base64,".length()));
                     } else {
                        OldServerPinger.logger.error("Invalid server icon (unknown format)");
                     }
                  } else {
                     server.setBase64EncodedIconData((String)null);
                  }

                  this.field_175092_e = Minecraft.getSystemTime();
                  networkManager.sendPacket(new C01PacketPing(this.field_175092_e));
                  this.field_147403_d = true;
               }
            }

            @Override
            public void handlePong(S01PacketPong packetIn) {
               long l = this.field_175092_e;
               long m = Minecraft.getSystemTime();
               server.pingToServer = m - l;
               networkManager.closeChannel(new ChatComponentText("Finished"));
            }

            @Override
            public void onDisconnect(IChatComponent reason) {
               if (!this.field_147403_d) {
                  OldServerPinger.logger.error("Can't ping " + server.serverIP + ": " + reason.getUnformattedText());
                  server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                  server.populationInfo = "";
                  OldServerPinger.this.tryCompatibilityPing(server);
               }
            }
         }
      );

      try {
         networkManager.sendPacket(new C00Handshake(47, serverAddress.getIP(), serverAddress.getPort(), EnumConnectionState.STATUS));
         networkManager.sendPacket(new C00PacketServerQuery());
      } catch (Throwable throwable) {
         logger.error(throwable);
      }
   }

   private void tryCompatibilityPing(final ServerData server) {
      final ServerAddress serverAddress = ServerAddress.fromString(server.serverIP);
      new Bootstrap()
         .group(NetworkManager.CLIENT_NIO_EVENTLOOP.getValue())
         .handler(
            new ChannelInitializer<Channel>() {
               @Override
               public void initChannel(Channel channel) throws Exception {
                  try {
                     channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                  } catch (ChannelException channelException) {
                  }

                  channel.pipeline()
                     .addLast(
                        new SimpleChannelInboundHandler<ByteBuf>() {
                           @Override
                           public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
                              super.channelActive(channelHandlerContext);
                              ByteBuf byteBuf2 = Unpooled.buffer();

                              try {
                                 byteBuf2.writeByte(254);
                                 byteBuf2.writeByte(1);
                                 byteBuf2.writeByte(250);
                                 char[] chars = "MC|PingHost".toCharArray();
                                 byteBuf2.writeShort(chars.length);

                                 for (char c : chars) {
                                    byteBuf2.writeChar(c);
                                 }

                                 byteBuf2.writeShort(7 + 2 * serverAddress.getIP().length());
                                 byteBuf2.writeByte(127);
                                 chars = serverAddress.getIP().toCharArray();
                                 byteBuf2.writeShort(chars.length);

                                 for (char c2 : chars) {
                                    byteBuf2.writeChar(c2);
                                 }

                                 byteBuf2.writeInt(serverAddress.getPort());
                                 channelHandlerContext.channel().writeAndFlush(byteBuf2).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                              } finally {
                                 byteBuf2.release();
                              }
                           }

                           public void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                              short s = byteBuf.readUnsignedByte();
                              if (s == 255) {
                                 String string = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), Charsets.UTF_16BE);
                                 String[] strings = Iterables.toArray(OldServerPinger.PING_RESPONSE_SPLITTER.split(string), String.class);
                                 if ("§1".equals(strings[0])) {
                                    int i = MathHelper.parseIntWithDefault(strings[1], 0);
                                    String string2 = strings[2];
                                    String string3 = strings[3];
                                    int j = MathHelper.parseIntWithDefault(strings[4], -1);
                                    int k = MathHelper.parseIntWithDefault(strings[5], -1);
                                    server.version = -1;
                                    server.gameVersion = string2;
                                    server.serverMOTD = string3;
                                    server.populationInfo = EnumChatFormatting.GRAY
                                       + ""
                                       + j
                                       + ""
                                       + EnumChatFormatting.DARK_GRAY
                                       + "/"
                                       + EnumChatFormatting.GRAY
                                       + k;
                                 }
                              }

                              channelHandlerContext.close();
                           }

                           @Override
                           public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
                              channelHandlerContext.close();
                           }
                        }
                     );
               }
            }
         )
         .channel(NioSocketChannel.class)
         .connect(serverAddress.getIP(), serverAddress.getPort());
   }

   public void pingPendingNetworks() {
      synchronized (this.pingDestinations) {
         Iterator iterator2 = this.pingDestinations.iterator();

         while (iterator2.hasNext()) {
            NetworkManager networkManager = (NetworkManager)iterator2.next();
            if (networkManager.isChannelOpen()) {
               networkManager.processReceivedPackets();
            } else {
               iterator2.remove();
               networkManager.checkDisconnected();
            }
         }
      }
   }

   public void clearPendingNetworks() {
      synchronized (this.pingDestinations) {
         Iterator iterator2 = this.pingDestinations.iterator();

         while (iterator2.hasNext()) {
            NetworkManager networkManager = (NetworkManager)iterator2.next();
            if (networkManager.isChannelOpen()) {
               iterator2.remove();
               networkManager.closeChannel(new ChatComponentText("Cancelled"));
            }
         }
      }
   }
}
