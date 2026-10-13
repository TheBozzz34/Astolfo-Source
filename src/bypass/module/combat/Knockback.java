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
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING
         && var1x.getPacket() instanceof C02PacketUseEntity
         && ((C02PacketUseEntity)var1x.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK
         && ((C02PacketUseEntity)var1x.getPacket()).getEntityFromWorld(mc.theWorld) instanceof EntityLivingBase) {
         if (((EntityLivingBase)((C02PacketUseEntity)var1x.getPacket()).getEntityFromWorld(mc.theWorld)).hurtTime > this.hurtTime.getValue()) {
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
   private static final long c = 133201786031317L;

   public Knockback(Category var1) {
      super(var1);
      this.addSettings(this.hurtTime);
   }
}
