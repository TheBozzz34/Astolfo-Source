package bypass.command;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.MoveEvent;
import java.util.Arrays;

public final class JumpCommand extends Command {
   private boolean leftGround;
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = moveEvent -> {
      switch (moveEvent.getState()) {
         case PRE:
            float f = mc.thePlayer.rotationYaw
               + (mc.thePlayer.moveForward < 0.0F ? 180 : 0)
               + (
                  mc.thePlayer.moveStrafing > 0.0F
                     ? -90.0F * (mc.thePlayer.moveForward > 0.0F ? 0.5F : (mc.thePlayer.moveForward < 0.0F ? -0.5F : 1.0F))
                     : 0.0F
               )
               - (
                  mc.thePlayer.moveStrafing < 0.0F
                     ? -90.0F * (mc.thePlayer.moveForward > 0.0F ? 0.5F : (mc.thePlayer.moveForward < 0.0F ? -0.5F : 1.0F))
                     : 0.0F
               );
            float g = (float)Math.cos((f + 90.0F) * Math.PI / 180.0);
            float h = (float)Math.sin((f + 90.0F) * Math.PI / 180.0);
            mc.thePlayer.setSprinting(true);
            mc.thePlayer.onGround = true;
            if (mc.thePlayer.isCollidedVertically) {
               mc.thePlayer.motionY = 0.42F;
            } else {
               this.leftGround = true;
               mc.thePlayer.motionX = g * 0.36;
               mc.thePlayer.motionZ = h * 0.36;
            }

            if (this.leftGround && mc.thePlayer.isCollidedVertically) {
               mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
               client.getEventBus().unsubscribe(this);
            }
      }
   };

   public JumpCommand(String string, String string2) {
      super(string, string2);
      this.setAliases(Arrays.asList("jump", "bounce"));
   }

   @Override
   public void runCommand(String[] strings) {
      client.getEventBus().subscribe(this);
      this.leftGround = false;
   }
}
