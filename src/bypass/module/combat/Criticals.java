package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.exploits.RiderGod;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.Stopwatch;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.EnumChatFormatting;

public final class Criticals extends Module {
   public static final EnumSetting<CriticalsMode> mode = new EnumSetting<>("Mode", "Set your Critical mode", CriticalsMode.PACKET);
   public static final NumberSetting<Float> offset = new NumberSetting<>("Offset", "Offset used for Edit mode", 0.1337F, 0.0627F, 0.42F);
   public static final NumberSetting<Integer> ticks = new NumberSetting<>("Ticks", "Wait ticks", 15, 0, 20);
   private final BooleanSetting safe = new BooleanSetting("Safe", "Crits when the target isnt hurt", false);
   private final Stopwatch B = new Stopwatch();
   public static boolean j;
   private int J;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      this.p();
      if (!GameUtil.N()) {
         j = false;
         this.J = 0;
      } else if (this.J >= ticks.getValue() || ticks.getValue() == 0) {
         j = true;
         this.J = 0;
      } else if (!j) {
         this.J++;
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      long var2 = c ^ 59773817807538L;
      long var4 = var2 ^ 93658388479323L;
      if (var1x.getDirection() == PacketDirection.OUTGOING) {
         if (mc.theWorld == null) {
            return;
         }

         if (client.getModuleManager().get(RiderGod.class).isEnabled()) {
            return;
         }

         if (var1x.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity var7 = (C02PacketUseEntity)var1x.getPacket();
            if (var7.getAction().equals(C02PacketUseEntity.Action.ATTACK)
               && GameUtil.N()
               && mode.getValue() != CriticalsMode.WATCHDOG
               && var7.getEntityFromWorld(mc.theWorld) instanceof EntityLivingBase
               && (j || this.safe.getValue() && this.B.hasElapsed(350.0F) && ((EntityLivingBase)var7.getEntityFromWorld(mc.theWorld)).hurtTime == 0)) {
               this.v((EntityLivingBase)var7.getEntityFromWorld(mc.theWorld));
            }
         }
      }
   };
   private final double[] Y = new double[]{0.06252F + Math.random() / 1.0E7, 1.73E-11, 1.73E-11};
   private static final long c = 92377835217498L;

   public Criticals(Category var1) {
      super(var1);
      this.addSettings(mode, offset, ticks, this.safe);
   }

   private void v(EntityLivingBase var1) {
      switch ((CriticalsMode)mode.getValue()) {
         case PACKET:
            mc.thePlayer
               .sendQueue
               .u(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.06251 + 1.0E-5, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.u(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
         case WATCHDOG:
         default:
            this.B.reset();
            j = false;
            this.J = 0;
      }
   }

   @Override
   public void p() {
      this.setDisplayName(
         this.getName() + " " + EnumChatFormatting.GRAY + CriticalsMode.p(mode.getValue()) + " " + (this.safe.getValue() ? "Safe" : ticks.getValue())
      );
      super.p();
   }
}
