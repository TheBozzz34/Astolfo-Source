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

   public MessageSerializer(EnumPacketDirection var1) {
      this.direction = var1;
   }

   public void encode(ChannelHandlerContext var1, Packet var2, ByteBuf var3) throws IOException, Exception {
      Integer var7;
      if (var2 instanceof VehicleMovePacket) {
         var7 = (int)e;
         o = true;
      } else {
         var7 = var1.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacketId(this.direction, var2);
      }

      if (logger.isDebugEnabled()) {
         logger.debug(
            RECEIVED_PACKET_MARKER, "OUT: [{}:{}] {}", var1.channel().attr(NetworkManager.attrKeyConnectionState).get(), var7, var2.getClass().getName()
         );
      }

      if (var7 == null) {
         throw new IOException("Can't serialize unregistered packet");
      }

      PacketBuffer var8 = new PacketBuffer(var3);
      var8.writeVarIntToBuffer(var7);

      try {
         var2.writePacketData(var8);
      } catch (Throwable var10) {
         var10.printStackTrace();
         logger.error(var10);
      }
   }
}
