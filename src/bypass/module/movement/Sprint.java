package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MoveEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.player.Scaffold;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;

public final class Sprint extends Module {
   public static final BooleanSetting omnidirectional = new BooleanSetting("Omnidirectional", "omnidirectional sprinting", true);
   private final BooleanSetting silent = new BooleanSetting("Silent", "Disables sprinting particles for other players", false);
   private final NumberSetting<Double> speed = new NumberSetting<>("Speed", "Distance of blocks", 1.0, 1.0, 3.0);
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      long var2 = d ^ 52934584675659L;
      long var4 = var2 ^ 130582491503218L;
      if (var1x.getState() == EventState.PRE && (Speed.mode.getValue() != SpeedMode.WATCHDOG || !client.getModuleManager().get(Speed.class).isEnabled())) {
         mc.thePlayer.setSprinting(this.Z());
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      long var2 = d ^ 77008104429481L;
      long var4 = var2 ^ 1024495263888L;
      if (var1x.getDirection() == PacketDirection.OUTGOING && var1x.getPacket() instanceof C0BPacketEntityAction) {
         C0BPacketEntityAction var7 = (C0BPacketEntityAction)var1x.getPacket();
         if ((var7.getAction() == C0BPacketEntityAction.Action.START_SPRINTING || var7.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING)
            && (
               this.silent.getValue()
                  || client.getModuleManager().get(Scaffold.class).isEnabled() && Scaffold.ncpHypixel.getValue() && GameUtil.isInHypixelGame()
            )) {
            var1x.cancel();
         }
      }
   };
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = var1x -> {
      long var2 = d ^ 126499147232298L;
      long var4 = var2 ^ 59286397158163L;
      if (var1x.getState() == EventState.PRE
         && mc.thePlayer.isSprinting()
         && !client.getModuleManager().get(Speed.class).isEnabled()
         && this.speed.getValue() > 1.0) {
         GameUtil.setMoveSpeed(var1x, this.speed.getValue(), 90.0F);
      }
   };
   private static final long d = 124865768180362L;
   private static final long j = 8147618630572940775L;

   public Sprint(Category var1) {
      super(var1);
      this.B((int)j);
      this.addSettings(this.speed, omnidirectional, this.silent);
   }

   private boolean Z() {
      long var1 = d ^ 129541700543528L;
      long var3 = var1 ^ 56307408425745L;
      boolean var6 = mc.thePlayer.getFoodStats().getFoodLevel() > 6.0F || mc.thePlayer.capabilities.allowFlying;
      boolean var7 = mc.thePlayer.isUsingItem() && !client.getModuleManager().get(NoSlow.class).isEnabled();
      boolean var8 = client.getModuleManager().get(Scaffold.class).isEnabled() && !client.getModuleManager().get(Scaffold.class).getKeepSprint().getValue();
      boolean var9 = omnidirectional.getValue() && (mc.thePlayer.movementInput.moveForward != 0.0F || mc.thePlayer.movementInput.moveStrafe != 0.0F)
         || mc.thePlayer.moveForward >= 0.8 && !omnidirectional.getValue();
      return var6
         && !var8
         && !var7
         && !mc.thePlayer.isDead
         && !mc.thePlayer.isSneaking()
         && !mc.thePlayer.isCollidedHorizontally
         && var9
         && !mc.thePlayer.isPotionActive(Potion.blindness);
   }
}
