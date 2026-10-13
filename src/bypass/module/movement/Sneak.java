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
   private Listener<WorldLoadEvent> onWorldLoad = var1x -> this.V = false;
   @EventHandler(PlayerModelVisibilityEvent.class)
   private Listener<PlayerModelVisibilityEvent> onPlayerModelVisibility = var1x -> {
      if (var1x.getEntity() == mc.thePlayer && this.V) {
         var1x.cancel();
      }
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> this.p();
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = this::S;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING && var1x.getPacket() instanceof C0BPacketEntityAction) {
         C0BPacketEntityAction var5 = (C0BPacketEntityAction)var1x.getPacket();
         if (var5.getAction() == C0BPacketEntityAction.Action.START_SNEAKING) {
            if (this.V) {
               var1x.cancel();
            }

            this.V = true;
         }

         if (var5.getAction() == C0BPacketEntityAction.Action.STOP_SNEAKING) {
            if (!this.V) {
               var1x.cancel();
            }

            this.V = false;
         }
      }
   };
   private static final long c = 13967002925836L;

   public Sneak(Category var1) {
      super(var1);
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

   private void S(MotionEvent var1) {
      long var2 = c ^ 84868902129426L;
      long var4 = var2 ^ 130790967314861L;
      boolean var7 = GameUtil.isMoving(mc.thePlayer);
      switch ((SneakMode)this.mode.getValue()) {
         case WATCHDOG:
            if (GameUtil.isInHypixelGame()) {
               if (!hold.getValue() || mc.gameSettings.keyBindSneak.pressed) {
                  switch (var1.getState()) {
                     case PRE:
                        if (!var7) {
                           if (!this.V) {
                              mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                           }
                        } else if (this.V) {
                           mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                        }
                        break;
                     case POST:
                        if (var7 && !this.V) {
                           mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                        }
                  }
               } else if (this.V) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
               }
            }
            break;
         case ALWAYS:
            if (var1.getState() == EventState.POST && !client.getModuleManager().get(Freecam.class).isEnabled()) {
               mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            }
      }
   }
}
