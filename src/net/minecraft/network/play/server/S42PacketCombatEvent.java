package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.CombatTracker;

public class S42PacketCombatEvent implements Packet<INetHandlerPlayClient> {
   public S42PacketCombatEvent.Event eventType;
   public int field_179774_b;
   public int field_179775_c;
   public int field_179772_d;
   public String deathMessage;
   private static final long a = 49936597700143L;
   private static final long b = -5867519178579083265L;

   public S42PacketCombatEvent() {
   }

   public S42PacketCombatEvent(CombatTracker var1, S42PacketCombatEvent.Event var2) {
      this.eventType = var2;
      EntityLivingBase var6 = var1.func_94550_c();
      switch (var2) {
         case END_COMBAT:
            this.field_179772_d = var1.func_180134_f();
            this.field_179775_c = var6 == null ? -1 : var6.getEntityId();
            break;
         case ENTITY_DIED:
            this.field_179774_b = var1.getFighter().getEntityId();
            this.field_179775_c = var6 == null ? -1 : var6.getEntityId();
            this.deathMessage = var1.getDeathMessage().getUnformattedText();
      }
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.eventType = var1.readEnumValue(S42PacketCombatEvent.Event.class);
      if (this.eventType == S42PacketCombatEvent.Event.END_COMBAT) {
         this.field_179772_d = var1.readVarIntFromBuffer();
         this.field_179775_c = var1.readInt();
      } else if (this.eventType == S42PacketCombatEvent.Event.ENTITY_DIED) {
         this.field_179774_b = var1.readVarIntFromBuffer();
         this.field_179775_c = var1.readInt();
         this.deathMessage = var1.readStringFromBuffer((int)b);
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeEnumValue(this.eventType);
      if (this.eventType == S42PacketCombatEvent.Event.END_COMBAT) {
         var1.writeVarIntToBuffer(this.field_179772_d);
         var1.writeInt(this.field_179775_c);
      } else if (this.eventType == S42PacketCombatEvent.Event.ENTITY_DIED) {
         var1.writeVarIntToBuffer(this.field_179774_b);
         var1.writeInt(this.field_179775_c);
         var1.writeString(this.deathMessage);
      }
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleCombatEvent(this);
   }

   public enum Event {
      ENTER_COMBAT,
      END_COMBAT,
      ENTITY_DIED;
   }
}
