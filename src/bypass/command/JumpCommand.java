package bypass.command;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.MoveEvent;
import java.util.Arrays;

public final class JumpCommand extends Command {
   private boolean x;
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = var1x -> {
      switch (var1x.getState()) {
         case PRE:
            float var5 = mc.thePlayer.rotationYaw
               + (mc.thePlayer.moveForward < 0.0F ? (int)h : 0)
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
            float var6 = (float)Math.cos((var5 + 90.0F) * Math.PI / 180.0);
            float var7 = (float)Math.sin((var5 + 90.0F) * Math.PI / 180.0);
            mc.thePlayer.setSprinting(true);
            mc.thePlayer.onGround = true;
            if (mc.thePlayer.isCollidedVertically) {
               mc.thePlayer.motionY = 0.42F;
            } else {
               this.x = true;
               mc.thePlayer.motionX = var6 * 0.36;
               mc.thePlayer.motionZ = var7 * 0.36;
            }

            if (this.x && mc.thePlayer.isCollidedVertically) {
               mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
               client.getEventBus().unsubscribe(this);
            }
      }
   };
   private static final long d = 38054998759725L;
   private static final long h = -8914742765070319436L;

   public JumpCommand(String var1, String var2) {
      super(var1, var2);
      this.setAliases(Arrays.asList("jump", "bounce"));
   }

   @Override
   public void runCommand(String[] var1) {
      client.getEventBus().subscribe(this);
      this.x = false;
   }
}
