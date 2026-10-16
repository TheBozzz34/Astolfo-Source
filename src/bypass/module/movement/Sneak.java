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
   private boolean serverSneaking;
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> this.serverSneaking = false;
   @EventHandler(PlayerModelVisibilityEvent.class)
   private Listener<PlayerModelVisibilityEvent> onPlayerModelVisibility = playerModelVisibilityEvent -> {
      if (playerModelVisibilityEvent.getEntity() == mc.thePlayer && this.serverSneaking) {
         playerModelVisibilityEvent.cancel();
      }
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> this.updateDisplayName();
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = this::handleMotion;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING && packetEvent.getPacket() instanceof C0BPacketEntityAction) {
         C0BPacketEntityAction c0BPacketEntityAction = (C0BPacketEntityAction)packetEvent.getPacket();
         if (c0BPacketEntityAction.getAction() == C0BPacketEntityAction.Action.START_SNEAKING) {
            if (this.serverSneaking) {
               packetEvent.cancel();
            }

            this.serverSneaking = true;
         }

         if (c0BPacketEntityAction.getAction() == C0BPacketEntityAction.Action.STOP_SNEAKING) {
            if (!this.serverSneaking) {
               packetEvent.cancel();
            }

            this.serverSneaking = false;
         }
      }
   };

   public Sneak(Category category) {
      super(category);
      this.addSettings(this.mode, hold);
   }

   @Override
   public void updateDisplayName() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + SneakMode.displayNameOf(this.mode.getValue()));
      super.updateDisplayName();
   }

   @Override
   public void onDisable() {
      if (this.serverSneaking) {
         mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
      }

      super.onDisable();
   }

   private void handleMotion(MotionEvent motionEvent) {
      boolean bl = GameUtil.isMoving(mc.thePlayer);
      switch ((SneakMode)this.mode.getValue()) {
         case WATCHDOG:
            if (GameUtil.isInHypixelGame()) {
               if (!hold.getValue() || mc.gameSettings.keyBindSneak.pressed) {
                  switch (motionEvent.getState()) {
                     case PRE:
                        if (!bl) {
                           if (!this.serverSneaking) {
                              mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                           }
                        } else if (this.serverSneaking) {
                           mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                        }
                        break;
                     case POST:
                        if (bl && !this.serverSneaking) {
                           mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                        }
                  }
               } else if (this.serverSneaking) {
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
