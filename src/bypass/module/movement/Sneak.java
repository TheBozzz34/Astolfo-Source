package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.render.PlayerModelVisibilityEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.player.Freecam;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.util.GameUtil;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.EnumChatFormatting;

public final class Sneak extends Module {
   private final EnumSetting<SneakMode> mode = new EnumSetting<>("Mode", "Set your Sneak mode", SneakMode.WATCHDOG);
   public static final BooleanSetting hold = new BooleanSetting("Hold", "Only works when pressing sneak key", false);
   private boolean V;
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> this.V = false;
   @EventHandler(PlayerModelVisibilityEvent.class)
   private Listener<PlayerModelVisibilityEvent> onPlayerModelVisibility = playerModelVisibilityEvent -> {
      if (playerModelVisibilityEvent.getEntity() == mc.thePlayer && this.V) {
         playerModelVisibilityEvent.cancel();
      }
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> this.p();
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = this::S;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING && packetEvent.getPacket() instanceof C0BPacketEntityAction) {
         C0BPacketEntityAction c0BPacketEntityAction = (C0BPacketEntityAction)packetEvent.getPacket();
         if (c0BPacketEntityAction.getAction() == C0BPacketEntityAction.Action.START_SNEAKING) {
            if (this.V) {
               packetEvent.cancel();
            }

            this.V = true;
         }

         if (c0BPacketEntityAction.getAction() == C0BPacketEntityAction.Action.STOP_SNEAKING) {
            if (!this.V) {
               packetEvent.cancel();
            }

            this.V = false;
         }
      }
   };

   public Sneak(Category category) {
      super(category);
      this.addSettings(this.mode, hold);
   }

   @Override
   public void p() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + SneakMode.T(this.mode.getValue()));
      super.p();
   }

   @Override
   public void onDisable() {
      if (this.V) {
         mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
      }

      super.onDisable();
   }

   private void S(MotionEvent motionEvent) {
      boolean bl = GameUtil.isMoving(mc.thePlayer);
      switch ((SneakMode)this.mode.getValue()) {
         case WATCHDOG:
            if (GameUtil.isInHypixelGame()) {
               if (!hold.getValue() || mc.gameSettings.keyBindSneak.pressed) {
                  switch (motionEvent.getState()) {
                     case PRE:
                        if (!bl) {
                           if (!this.V) {
                              mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                           }
                        } else if (this.V) {
                           mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                        }
                        break;
                     case POST:
                        if (bl && !this.V) {
                           mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                        }
                  }
               } else if (this.V) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
               }
            }
            break;
         case ALWAYS:
            if (motionEvent.getState() == EventState.POST && !client.getModuleManager().get(Freecam.class).isEnabled()) {
               mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            }
      }
   }
}
