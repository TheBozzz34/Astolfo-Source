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
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (updateEvent.getState() == EventState.PRE && (Speed.mode.getValue() != SpeedMode.WATCHDOG || !client.getModuleManager().get(Speed.class).isEnabled())) {
         mc.thePlayer.setSprinting(this.canSprint());
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING && packetEvent.getPacket() instanceof C0BPacketEntityAction) {
         C0BPacketEntityAction c0BPacketEntityAction = (C0BPacketEntityAction)packetEvent.getPacket();
         if ((c0BPacketEntityAction.getAction() == C0BPacketEntityAction.Action.START_SPRINTING || c0BPacketEntityAction.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING)
            && (
               this.silent.getValue()
                  || client.getModuleManager().get(Scaffold.class).isEnabled() && Scaffold.ncpHypixel.getValue() && GameUtil.isInHypixelGame()
            )) {
            packetEvent.cancel();
         }
      }
   };
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = moveEvent -> {
      if (moveEvent.getState() == EventState.PRE
         && mc.thePlayer.isSprinting()
         && !client.getModuleManager().get(Speed.class).isEnabled()
         && this.speed.getValue() > 1.0) {
         GameUtil.setMoveSpeed(moveEvent, this.speed.getValue(), 90.0F);
      }
   };

   public Sprint(Category category) {
      super(category);
      this.setModuleColor(10984935);
      this.addSettings(this.speed, omnidirectional, this.silent);
   }

   private boolean canSprint() {
      boolean bl = mc.thePlayer.getFoodStats().getFoodLevel() > 6.0F || mc.thePlayer.capabilities.allowFlying;
      boolean bl2 = mc.thePlayer.isUsingItem() && !client.getModuleManager().get(NoSlow.class).isEnabled();
      boolean bl3 = client.getModuleManager().get(Scaffold.class).isEnabled() && !client.getModuleManager().get(Scaffold.class).getKeepSprint().getValue();
      boolean bl4 = omnidirectional.getValue() && (mc.thePlayer.movementInput.moveForward != 0.0F || mc.thePlayer.movementInput.moveStrafe != 0.0F)
         || mc.thePlayer.moveForward >= 0.8 && !omnidirectional.getValue();
      return bl
         && !bl3
         && !bl2
         && !mc.thePlayer.isDead
         && !mc.thePlayer.isSneaking()
         && !mc.thePlayer.isCollidedHorizontally
         && bl4
         && !mc.thePlayer.isPotionActive(Potion.blindness);
   }
}
