package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.MovementInputEvent;
import bypass.event.player.UpdateEvent;
import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.module.Category;
import bypass.module.Module;

public class Freecam2 extends Module {
   public static double cameraOffsetX;
   public static double cameraOffsetY;
   public static double cameraOffsetZ;
   public float strafeInput;
   public float forwardInput;
   public Animation moveAnimation = new TimedAnimation().setDuration(50).setEasing(Easing.LINEAR);
   @EventHandler(MovementInputEvent.class)
   private Listener<MovementInputEvent> onMovementInput = movementInputEvent -> {
      this.forwardInput = this.strafeInput = 0.0F;
      if (mc.gameSettings.keyBindForward.isKeyDown()) {
         this.forwardInput++;
      }

      if (mc.gameSettings.keyBindBack.isKeyDown()) {
         this.forwardInput--;
      }

      if (mc.gameSettings.keyBindLeft.isKeyDown()) {
         this.strafeInput++;
      }

      if (mc.gameSettings.keyBindRight.isKeyDown()) {
         this.strafeInput--;
      }

      movementInputEvent.cancel();
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      double d = -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw));
      double e = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw));
      double d2 = this.strafeInput * 1.0F * d + this.forwardInput * 1.0F * e;
      double d3 = this.strafeInput * -1.0F * d + this.forwardInput * -1.0F * e;
      double d4 = this.strafeInput * 1.0F * e - this.forwardInput * 1.0F * d;
      double d5 = this.strafeInput * -1.0F * e - this.forwardInput * -1.0F * d;
      if (this.forwardInput != 0.0F) {
         if (this.forwardInput > 0.0F) {
            cameraOffsetZ += d2;
         } else {
            cameraOffsetZ -= d3;
         }
      }

      if (this.strafeInput != 0.0F) {
         if (this.strafeInput > 0.0F) {
            cameraOffsetX -= d4;
         } else {
            cameraOffsetX += d5;
         }
      }
   };

   public Freecam2() {
      super("Freecam2", Category.VISUALS);
   }

   @Override
   public void onDisable() {
      this.forwardInput = this.strafeInput = 0.0F;
      cameraOffsetZ = 0.0;
      cameraOffsetY = 0.0;
      cameraOffsetX = 0.0;
      super.onDisable();
   }
}
