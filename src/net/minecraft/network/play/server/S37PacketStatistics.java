package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class S37PacketStatistics implements Packet<INetHandlerPlayClient> {
   private Map<StatBase, Integer> field_148976_a;
   private static final long b = 129373503764139L;
   private static final long c = 5604535101229662207L;

   public S37PacketStatistics() {
   }

   public S37PacketStatistics(Map<StatBase, Integer> var1) {
      this.field_148976_a = var1;
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleStatistics(this);
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      int var5 = var1.readVarIntFromBuffer();
      this.field_148976_a = Maps.newHashMap();

      for (int var6 = 0; var6 < var5; var6++) {
         StatBase var7 = StatList.getOneShotStat(var1.readStringFromBuffer((int)c));
         int var8 = var1.readVarIntFromBuffer();
         if (var7 != null) {
            this.field_148976_a.put(var7, var8);
         }
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeVarIntToBuffer(this.field_148976_a.size());

      for (Entry var6 : this.field_148976_a.entrySet()) {
         var1.writeString(((StatBase)var6.getKey()).statId);
         var1.writeVarIntToBuffer((Integer)var6.getValue());
      }
   }

   public Map<StatBase, Integer> func_148974_c() {
      return this.field_148976_a;
   }
}
