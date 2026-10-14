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

   public S20PacketEntityProperties() {
   }

   public S20PacketEntityProperties(int entityIdIn, Collection<IAttributeInstance> collection) {
      this.entityId = entityIdIn;

      for (IAttributeInstance iAttributeInstance : collection) {
         this.field_149444_b
            .add(new S20PacketEntityProperties.Snapshot(iAttributeInstance.getAttribute().getAttributeUnlocalizedName(), iAttributeInstance.getBaseValue(), iAttributeInstance.func_111122_c()));
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarIntFromBuffer();
      int i = buf.readInt();

      for (int j = 0; j < i; j++) {
         String string = buf.readStringFromBuffer(64);
         double d = buf.readDouble();
         ArrayList arrayList = Lists.newArrayList();
         int k = buf.readVarIntFromBuffer();

         for (int l = 0; l < k; l++) {
            UUID uuid = buf.readUuid();
            arrayList.add(new AttributeModifier(uuid, "Unknown synced attribute modifier", buf.readDouble(), buf.readByte()));
         }

         this.field_149444_b.add(new S20PacketEntityProperties.Snapshot(string, d, arrayList));
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityId);
      buf.writeInt(this.field_149444_b.size());

      for (S20PacketEntityProperties.Snapshot snapshot : this.field_149444_b) {
         buf.writeString(snapshot.func_151409_a());
         buf.writeDouble(snapshot.func_151410_b());
         buf.writeVarIntToBuffer(snapshot.func_151408_c().size());

         for (AttributeModifier attributeModifier : snapshot.func_151408_c()) {
            buf.writeUuid(attributeModifier.getID());
            buf.writeDouble(attributeModifier.getAmount());
            buf.writeByte(attributeModifier.getOperation());
         }
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityProperties(this);
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

      public Snapshot(String string, double d, Collection<AttributeModifier> collection) {
         this.field_151412_b = string;
         this.field_151413_c = d;
         this.field_151411_d = collection;
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
