package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.NumberSetting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public final class Knockback extends Module {
   private final NumberSetting<Integer> hurtTime = new NumberSetting<>("Hurt-Time", "", 10, 0, 10);
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING
         && packetEvent.getPacket() instanceof C02PacketUseEntity
         && ((C02PacketUseEntity)packetEvent.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK
         && ((C02PacketUseEntity)packetEvent.getPacket()).getEntityFromWorld(mc.theWorld) instanceof EntityLivingBase) {
         if (((EntityLivingBase)((C02PacketUseEntity)packetEvent.getPacket()).getEntityFromWorld(mc.theWorld)).hurtTime > this.hurtTime.getValue()) {
            return;
         }

         if (mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
            if (!mc.thePlayer.isSprinting()) {
               mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            }

            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            mc.thePlayer.setSprinting(true);
            mc.thePlayer.ej = true;
         }
      }
   };

   public Knockback(Category category) {
      super(category);
      this.addSettings(this.hurtTime);
   }
}
