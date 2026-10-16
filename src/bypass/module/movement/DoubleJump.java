package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.EnumSetting;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public final class DoubleJump extends Module {
   private final EnumSetting<DoubleJumpMode> mode = new EnumSetting<>("Mode", "Set your doublejump mode", DoubleJumpMode.NCP);
   private boolean jumpPending;
   private boolean airJumped;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      this.updateDisplayName();
      if (updateEvent.getState() == EventState.PRE) {
         switch ((DoubleJumpMode)this.mode.getValue()) {
            case NCP:
               if (mc.thePlayer.isCollidedHorizontally && this.isFalling()) {
                  if (mc.thePlayer.onGround) {
                     this.jumpPending = true;
                     this.airJumped = false;
                  } else if (!this.airJumped) {
                     mc.thePlayer.jump();
                     this.jumpPending = true;
                     this.airJumped = true;
                  }
               } else if (mc.thePlayer.onGround) {
                  this.airJumped = false;
               }
               break;
            case VANILLA:
               if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) && this.isFalling()) {
                  mc.thePlayer.jumpTicks = 0;
                  mc.thePlayer.onGround = true;
               }
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING
         && packetEvent.getPacket() instanceof C03PacketPlayer
         && this.jumpPending
         && this.mode.getValue() == DoubleJumpMode.NCP) {
         this.jumpPending = false;
      }
   };

   public DoubleJump(Category category) {
      super(category);
      this.addSettings(this.mode);
   }

   @Override
   public void onDisable() {
      if (mc.thePlayer != null) {
         mc.thePlayer.jumpTicks = 10;
         mc.thePlayer.onGround = false;
      }

      super.onDisable();
   }

   @Override
   public void updateDisplayName() {
      this.setDisplayName("Double Jump " + EnumChatFormatting.GRAY + DoubleJumpMode.displayNameOf(this.mode.getValue()));
      super.updateDisplayName();
   }

   private boolean isFalling() {
      return mc.thePlayer.motionY < -0.25;
   }
}
