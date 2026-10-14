package net.minecraft.util;

import bypass.network.VehicleMovePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageSerializer extends MessageToByteEncoder<Packet> {
   private static final Logger logger = LogManager.getLogger();
   private static final Marker RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_SENT", NetworkManager.logMarkerPackets);
   private final EnumPacketDirection direction;
   public static boolean o;
   private static final long a = 28651343822581L;
   private static final long e = -1752296272506650608L;

   public MessageSerializer(EnumPacketDirection direction) {
      this.direction = direction;
   }

   public void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws IOException, Exception {
      Integer integer;
      if (packet instanceof VehicleMovePacket) {
         integer = (int)e;
         o = true;
      } else {
         integer = channelHandlerContext.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacketId(this.direction, packet);
      }

      if (logger.isDebugEnabled()) {
         logger.debug(
            RECEIVED_PACKET_MARKER,
            "OUT: [{}:{}] {}",
            channelHandlerContext.channel().attr(NetworkManager.attrKeyConnectionState).get(),
            integer,
            packet.getClass().getName()
         );
      }

      if (integer == null) {
         throw new IOException("Can't serialize unregistered packet");
      }

      PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
      packetBuffer.writeVarIntToBuffer(integer);

      try {
         packet.writePacketData(packetBuffer);
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         logger.error(throwable);
      }
   }
}
