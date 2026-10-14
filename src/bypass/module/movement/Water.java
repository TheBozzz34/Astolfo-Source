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
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.PRE && mc.thePlayer.isInWater() && GameUtil.isMoving(mc.thePlayer)) {
         this.m++;
         double d = this.g(0.0F)[0];
         double e = this.g(0.0F)[1];
         if (this.m >= 3) {
            mc.thePlayer.motionX = d * 0.06F;
            mc.thePlayer.motionZ = e * 0.06F;
            this.m = 0;
         } else {
            mc.thePlayer.motionX = d * 0.27F;
            mc.thePlayer.motionZ = e * 0.27F;
         }
      }
   };
   private static final long c = 93352456244166L;

   public Water(Category category) {
      super(category);
      this.setHidden(false);
   }

   private double[] g(float f) {
      float h = mc.thePlayer.rotationYaw + f;
      if (mc.thePlayer.moveForward < 0.0F) {
         h += 180.0F;
      }

      if (mc.thePlayer.moveStrafing > 0.0F) {
         h -= 90.0F * (mc.thePlayer.moveForward < 0.0F ? -0.5F : (mc.thePlayer.moveForward > 0.0F ? 0.5F : 1.0F));
      }

      if (mc.thePlayer.moveStrafing < 0.0F) {
         h += 90.0F * (mc.thePlayer.moveForward < 0.0F ? -0.5F : (mc.thePlayer.moveForward > 0.0F ? 0.5F : 1.0F));
      }

      float f2 = (float)Math.cos((h + 90.0F) * Math.PI / 180.0);
      float f3 = (float)Math.sin((h + 90.0F) * Math.PI / 180.0);
      return new double[]{f2, f3};
   }
}
