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
   private boolean d;
   private boolean T;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      this.p();
      if (var1x.getState() == EventState.PRE) {
         switch ((DoubleJumpMode)this.mode.getValue()) {
            case NCP:
               if (mc.thePlayer.isCollidedHorizontally && this.e()) {
                  if (mc.thePlayer.onGround) {
                     this.d = true;
                     this.T = false;
                  } else if (!this.T) {
                     mc.thePlayer.jump();
                     this.d = true;
                     this.T = true;
                  }
               } else if (mc.thePlayer.onGround) {
                  this.T = false;
               }
               break;
            case VANILLA:
               if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) && this.e()) {
                  mc.thePlayer.jumpTicks = 0;
                  mc.thePlayer.onGround = true;
               }
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING
         && var1x.getPacket() instanceof C03PacketPlayer
         && this.d
         && this.mode.getValue() == DoubleJumpMode.NCP) {
         this.d = false;
      }
   };
   private static final long c = 79526476327355L;
   private static final long j = 7500476456166752266L;

   public DoubleJump(Category var1) {
      super(var1);
      this.addSettings(this.mode);
   }

   @Override
   public void onDisable() {
      if (mc.thePlayer != null) {
         mc.thePlayer.jumpTicks = (int)j;
         mc.thePlayer.onGround = false;
      }

      super.onDisable();
   }

   @Override
   public void p() {
      this.setDisplayName("Double Jump " + EnumChatFormatting.GRAY + DoubleJumpMode.T(this.mode.getValue()));
      super.p();
   }

   private boolean e() {
      return mc.thePlayer.motionY < -0.25;
   }
}
