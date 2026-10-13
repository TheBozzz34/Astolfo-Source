package net.minecraft.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PingResponseHandler extends ChannelInboundHandlerAdapter {
   private static final Logger logger = LogManager.getLogger();
   private NetworkSystem networkSystem;
   private static final long a = 65324118855572L;

   public PingResponseHandler(NetworkSystem var1) {
      this.networkSystem = var1;
   }

   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      ByteBuf var6 = (ByteBuf)var2;
      var6.markReaderIndex();
      boolean var7 = true;

      try {
         if (var6.readUnsignedByte() == 254) {
            InetSocketAddress var8 = (InetSocketAddress)var1.channel().remoteAddress();
            MinecraftServer var9 = this.networkSystem.getServer();
            int var10 = var6.readableBytes();
            switch (var10) {
               case 0:
                  logger.debug("Ping: (<1.3.x) from {}:{}", var8.getAddress(), var8.getPort());
                  String var11 = String.format("%s§%d§%d", var9.getMOTD(), var9.getCurrentPlayerCount(), var9.getMaxPlayers());
                  this.writeAndFlush(var1, this.getStringBuffer(var11));
                  break;
               case 1:
                  if (var6.readUnsignedByte() != 1) {
                     return;
                  }

                  logger.debug("Ping: (1.4-1.5.x) from {}:{}", var8.getAddress(), var8.getPort());
                  String var12 = String.format(
                     "§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                     127,
                     var9.getMinecraftVersion(),
                     var9.getMOTD(),
                     var9.getCurrentPlayerCount(),
                     var9.getMaxPlayers()
                  );
                  this.writeAndFlush(var1, this.getStringBuffer(var12));
                  break;
               default:
                  boolean var13 = var6.readUnsignedByte() == 1;
                  var13 &= var6.readUnsignedByte() == 250;
                  var13 &= "MC|PingHost".equals(new String(var6.readBytes(var6.readShort() * 2).array(), Charsets.UTF_16BE));
                  int var14 = var6.readUnsignedShort();
                  var13 &= var6.readUnsignedByte() >= 73;
                  var13 &= 3 + var6.readBytes(var6.readShort() * 2).array().length + 4 == var14;
                  var13 &= var6.readInt() <= 65535;
                  var13 &= var6.readableBytes() == 0;
                  if (!var13) {
                     return;
                  }

                  logger.debug("Ping: (1.6) from {}:{}", var8.getAddress(), var8.getPort());
                  String var15 = String.format(
                     "§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                     127,
                     var9.getMinecraftVersion(),
                     var9.getMOTD(),
                     var9.getCurrentPlayerCount(),
                     var9.getMaxPlayers()
                  );
                  ByteBuf var16 = this.getStringBuffer(var15);

                  try {
                     this.writeAndFlush(var1, var16);
                  } finally {
                     var16.release();
                  }
            }

            var6.release();
            var7 = false;
         }
      } catch (RuntimeException var26) {
      } finally {
         if (var7) {
            var6.resetReaderIndex();
            var1.channel().pipeline().remove("legacy_query");
            var1.fireChannelRead(var2);
         }
      }
   }

   private void writeAndFlush(ChannelHandlerContext var1, ByteBuf var2) {
      var1.pipeline().firstContext().writeAndFlush(var2).addListener(ChannelFutureListener.CLOSE);
   }

   private ByteBuf getStringBuffer(String var1) {
      ByteBuf var5 = Unpooled.buffer();
      var5.writeByte(255);
      char[] var6 = var1.toCharArray();
      var5.writeShort(var6.length);

      for (char var10 : var6) {
         var5.writeChar(var10);
      }

      return var5;
   }
}
