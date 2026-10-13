package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;

public class S3DPacketDisplayScoreboard implements Packet<INetHandlerPlayClient> {
   private int position;
   private String scoreName;
   private static final long a = 43682945840317L;
   private static final long b = 1523142462841815056L;

   public S3DPacketDisplayScoreboard() {
   }

   public S3DPacketDisplayScoreboard(int var1, ScoreObjective var2) {
      this.position = var1;
      if (var2 == null) {
         this.scoreName = "";
      } else {
         this.scoreName = var2.getName();
      }
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.position = var1.readByte();
      this.scoreName = var1.readStringFromBuffer((int)b);
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeByte(this.position);
      var1.writeString(this.scoreName);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleDisplayScoreboard(this);
   }

   public int func_149371_c() {
      return this.position;
   }

   public String func_149370_d() {
      return this.scoreName;
   }
}
