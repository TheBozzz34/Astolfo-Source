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

   public C12PacketUpdateSign(BlockPos var1, IChatComponent[] var2) {
      this.pos = var1;
      this.lines = new IChatComponent[]{var2[0], var2[1], var2[2], var2[3]};
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.pos = var1.readBlockPos();
      this.lines = new IChatComponent[4];

      for (int var5 = 0; var5 < 4; var5++) {
         String var6 = var1.readStringFromBuffer((int)b);
         IChatComponent var7 = IChatComponent.Serializer.jsonToComponent(var6);
         this.lines[var5] = var7;
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeBlockPos(this.pos);

      for (int var5 = 0; var5 < 4; var5++) {
         IChatComponent var6 = this.lines[var5];
         String var7 = IChatComponent.Serializer.componentToJson(var6);
         var1.writeString(var7);
      }
   }

   public void processPacket(INetHandlerPlayServer var1) {
      var1.processUpdateSign(this);
   }

   public BlockPos getPosition() {
      return this.pos;
   }

   public IChatComponent[] getLines() {
      return this.lines;
   }
}
