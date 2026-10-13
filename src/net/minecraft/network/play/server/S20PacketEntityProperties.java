package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S20PacketEntityProperties implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private final List<S20PacketEntityProperties.Snapshot> field_149444_b = Lists.newArrayList();
   private static final long a = 7612424071979L;
   private static final long c = 4231077174980902976L;

   public S20PacketEntityProperties() {
   }

   public S20PacketEntityProperties(int var1, Collection<IAttributeInstance> var2) {
      this.entityId = var1;

      for (IAttributeInstance var7 : var2) {
         this.field_149444_b
            .add(new S20PacketEntityProperties.Snapshot(var7.getAttribute().getAttributeUnlocalizedName(), var7.getBaseValue(), var7.func_111122_c()));
      }
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.entityId = var1.readVarIntFromBuffer();
      int var5 = var1.readInt();

      for (int var6 = 0; var6 < var5; var6++) {
         String var7 = var1.readStringFromBuffer((int)c);
         double var8 = var1.readDouble();
         ArrayList var10 = Lists.newArrayList();
         int var11 = var1.readVarIntFromBuffer();

         for (int var12 = 0; var12 < var11; var12++) {
            UUID var13 = var1.readUuid();
            var10.add(new AttributeModifier(var13, "Unknown synced attribute modifier", var1.readDouble(), var1.readByte()));
         }

         this.field_149444_b.add(new S20PacketEntityProperties.Snapshot(var7, var8, var10));
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeVarIntToBuffer(this.entityId);
      var1.writeInt(this.field_149444_b.size());

      for (S20PacketEntityProperties.Snapshot var6 : this.field_149444_b) {
         var1.writeString(var6.func_151409_a());
         var1.writeDouble(var6.func_151410_b());
         var1.writeVarIntToBuffer(var6.func_151408_c().size());

         for (AttributeModifier var8 : var6.func_151408_c()) {
            var1.writeUuid(var8.getID());
            var1.writeDouble(var8.getAmount());
            var1.writeByte(var8.getOperation());
         }
      }
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleEntityProperties(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public List<S20PacketEntityProperties.Snapshot> func_149441_d() {
      return this.field_149444_b;
   }

   public class Snapshot {
      private final String field_151412_b;
      private final double field_151413_c;
      private final Collection<AttributeModifier> field_151411_d;

      public Snapshot(String var2, double var3, Collection<AttributeModifier> var5) {
         this.field_151412_b = var2;
         this.field_151413_c = var3;
         this.field_151411_d = var5;
      }

      public String func_151409_a() {
         return this.field_151412_b;
      }

      public double func_151410_b() {
         return this.field_151413_c;
      }

      public Collection<AttributeModifier> func_151408_c() {
         return this.field_151411_d;
      }
   }
}
