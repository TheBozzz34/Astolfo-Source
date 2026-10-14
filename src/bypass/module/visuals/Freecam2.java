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
   public static double V;
   public static double J;
   public static double R;
   public float d;
   public float O;
   public Animation c;
   @EventHandler(MovementInputEvent.class)
   private Listener<MovementInputEvent> onMovementInput;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate;
   private static final long e = 59158032428344L;
   private static final long j = -2717862580583399374L;

   public Freecam2() {
      super("Freecam2", Category.VISUALS);
      this.c = new TimedAnimation().setDuration((int)j).setEasing(Easing.LINEAR);
      this.onMovementInput = movementInputEvent -> {
         this.O = this.d = 0.0F;
         if (mc.gameSettings.keyBindForward.isKeyDown()) {
            this.O++;
         }

         if (mc.gameSettings.keyBindBack.isKeyDown()) {
            this.O--;
         }

         if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            this.d++;
         }

         if (mc.gameSettings.keyBindRight.isKeyDown()) {
            this.d--;
         }

         movementInputEvent.cancel();
      };
      this.onUpdate = updateEvent -> {
         double e = -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw));
         double d2 = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw));
         double d3 = this.d * 1.0F * e + this.O * 1.0F * d2;
         double d4 = this.d * -1.0F * e + this.O * -1.0F * d2;
         double d5 = this.d * 1.0F * d2 - this.O * 1.0F * e;
         double d6 = this.d * -1.0F * d2 - this.O * -1.0F * e;
         if (this.O != 0.0F) {
            if (this.O > 0.0F) {
               R += d3;
            } else {
               R -= d4;
            }
         }

         if (this.d != 0.0F) {
            if (this.d > 0.0F) {
               V -= d5;
            } else {
               V += d6;
            }
         }
      };
   }

   @Override
   public void onDisable() {
      this.O = this.d = 0.0F;
      R = 0.0;
      J = 0.0;
      V = 0.0;
      super.onDisable();
   }
}
