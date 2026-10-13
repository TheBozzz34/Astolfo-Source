package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.EnumSetting;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;

public final class Regen extends Module {
   private final EnumSetting<RegenMode> mode = new EnumSetting<>("Mode", "Set your Regen mode", RegenMode.VANILLA);
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> this.p();
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = var1x -> {
      if (var1x.getState() == EventState.PRE && this.v()) {
         switch ((RegenMode)this.mode.getValue()) {
            case POTION:
               if (mc.thePlayer.getActivePotionEffect(Potion.regeneration) != null) {
                  this.z();
               }
               break;
            case VANILLA:
               this.z();
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING
         && var1x.getPacket() instanceof C03PacketPlayer
         && mc.thePlayer != null
         && this.mode.getValue() == RegenMode.WAIT
         && this.v()
         && mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()
         && mc.thePlayer.ticksExisted % 2 == 0) {
         mc.thePlayer.sendQueue.u(new C03PacketPlayer(((C03PacketPlayer)var1x.getPacket()).isOnGround()));
      }
   };
   private static final long c = 19081020614194L;

   public Regen(Category var1) {
      super(var1);
      this.addSettings(this.mode);
   }

   private void z() {
      if (mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()) {
         for (int var4 = 0; var4 < mc.thePlayer.getMaxHealth() - mc.thePlayer.getHealth(); var4++) {
            mc.thePlayer.sendQueue.u(new C03PacketPlayer(mc.thePlayer.onGround));
         }
      }
   }

   private boolean v() {
      return mc.thePlayer.getFoodStats().getFoodLevel() > 16.0F
         && !mc.thePlayer.isUsingItem()
         && (mc.thePlayer.onGround || mc.thePlayer.isOnLadder() || mc.thePlayer.isInWater());
   }

   @Override
   public void p() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + RegenMode.S(this.mode.getValue()));
      super.p();
   }
}
