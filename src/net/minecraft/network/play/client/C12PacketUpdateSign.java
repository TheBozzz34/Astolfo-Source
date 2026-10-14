package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

public class C12PacketUpdateSign implements Packet<INetHandlerPlayServer> {
   private BlockPos pos;
   public IChatComponent[] lines;
   private static final long a = 130734488680090L;
   private static final long b = 8999209430159458688L;

   public C12PacketUpdateSign() {
   }

   public C12PacketUpdateSign(BlockPos pos, IChatComponent[] lines) {
      this.pos = pos;
      this.lines = new IChatComponent[]{lines[0], lines[1], lines[2], lines[3]};
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.pos = buf.readBlockPos();
      this.lines = new IChatComponent[4];

      for (int i = 0; i < 4; i++) {
         String string = buf.readStringFromBuffer((int)b);
         IChatComponent iChatComponent = IChatComponent.Serializer.jsonToComponent(string);
         this.lines[i] = iChatComponent;
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBlockPos(this.pos);

      for (int i = 0; i < 4; i++) {
         IChatComponent iChatComponent = this.lines[i];
         String string = IChatComponent.Serializer.componentToJson(iChatComponent);
         buf.writeString(string);
      }
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processUpdateSign(this);
   }

   public BlockPos getPosition() {
      return this.pos;
   }

   public IChatComponent[] getLines() {
      return this.lines;
   }
}
