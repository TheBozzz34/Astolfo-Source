package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageDeserializer extends ByteToMessageDecoder {
   private static final Logger logger = LogManager.getLogger();
   private static final Marker RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.logMarkerPackets);
   private final EnumPacketDirection direction;
   private static final long a = 87833422011271L;

   public MessageDeserializer(EnumPacketDirection direction) {
      this.direction = direction;
   }

   @Override
   public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws IOException, InstantiationException, IllegalAccessException, Exception {
      if (byteBuf.readableBytes() != 0) {
         PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
         int i = packetBuffer.readVarIntFromBuffer();
         Packet packet2 = channelHandlerContext.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacket(this.direction, i);
         if (packet2 == null) {
            throw new IOException("Bad packet id " + i);
         }

         packet2.readPacketData(packetBuffer);
         if (packetBuffer.readableBytes() > 0) {
            throw new IOException(
               "Packet "
                  + channelHandlerContext.channel().attr(NetworkManager.attrKeyConnectionState).get().getId()
                  + "/"
                  + i
                  + " ("
                  + packet2.getClass().getSimpleName()
                  + ") was larger than I expected, found "
                  + packetBuffer.readableBytes()
                  + " bytes extra whilst reading packet "
                  + i
            );
         }

         list.add(packet2);
         if (logger.isDebugEnabled()) {
            logger.debug(
               RECEIVED_PACKET_MARKER,
               " IN: [{}:{}] {}",
               channelHandlerContext.channel().attr(NetworkManager.attrKeyConnectionState).get(),
               i,
               packet2.getClass().getName()
            );
         }
      }
   }
}
