package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.player.MotionEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.util.GameUtil;

public final class Water extends Module {
   private int m;
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = var1x -> {
      if (var1x.getState() == EventState.PRE && mc.thePlayer.isInWater() && GameUtil.isMoving(mc.thePlayer)) {
         this.m++;
         double var5 = this.g(0.0F)[0];
         double var7 = this.g(0.0F)[1];
         if (this.m >= 3) {
            mc.thePlayer.motionX = var5 * 0.06F;
            mc.thePlayer.motionZ = var7 * 0.06F;
            this.m = 0;
         } else {
            mc.thePlayer.motionX = var5 * 0.27F;
            mc.thePlayer.motionZ = var7 * 0.27F;
         }
      }
   };
   private static final long c = 93352456244166L;

   public Water(Category var1) {
      super(var1);
      this.setHidden(false);
   }

   private double[] g(float var1) {
      float var5 = mc.thePlayer.rotationYaw + var1;
      if (mc.thePlayer.moveForward < 0.0F) {
         var5 += 180.0F;
      }

      if (mc.thePlayer.moveStrafing > 0.0F) {
         var5 -= 90.0F * (mc.thePlayer.moveForward < 0.0F ? -0.5F : (mc.thePlayer.moveForward > 0.0F ? 0.5F : 1.0F));
      }

      if (mc.thePlayer.moveStrafing < 0.0F) {
         var5 += 90.0F * (mc.thePlayer.moveForward < 0.0F ? -0.5F : (mc.thePlayer.moveForward > 0.0F ? 0.5F : 1.0F));
      }

      float var6 = (float)Math.cos((var5 + 90.0F) * Math.PI / 180.0);
      float var7 = (float)Math.sin((var5 + 90.0F) * Math.PI / 180.0);
      return new double[]{var6, var7};
   }
}
