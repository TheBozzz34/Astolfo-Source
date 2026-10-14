package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.player.SafeWalkEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.NumberSetting;
import bypass.util.Stopwatch;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;

public class SpeedBridge extends Module {
   private boolean P;
   private final NumberSetting<Integer> unsneak = new NumberSetting<>("Unsneak", "Delay before unsneaking", 150, 0, 500, "ms");
   private final Stopwatch l = new Stopwatch();
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (updateEvent.getState() == EventState.PRE) {
         if ((
               C()
                  || mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)).getBlock() != null
                     && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)).getBlock() instanceof BlockAir
            )
            && this.D()) {
            mc.gameSettings.keyBindSneak.pressed = true;
            this.P = true;
            this.l.reset();
         } else if ((this.l.hasElapsed(this.unsneak.getValue().intValue()) || !mc.thePlayer.onGround) && this.P) {
            mc.gameSettings.keyBindSneak.pressed = false;
            this.P = false;
         }
      }
   };
   @EventHandler(SafeWalkEvent.class)
   private Listener<SafeWalkEvent> onSafeWalk = safeWalkEvent -> {
      if (mc.thePlayer != null && this.D()) {
         safeWalkEvent.setSafe(true);
         safeWalkEvent.cancel();
      }
   };
   private static final long c = 127982052360127L;

   public SpeedBridge(Category category) {
      super(category);
      this.addSettings(this.unsneak);
   }

   @Override
   public void onDisable() {
      mc.gameSettings.keyBindSneak.pressed = false;
      super.onDisable();
   }

   private boolean D() {
      return mc.thePlayer.onGround
         && mc.thePlayer.isCollidedVertically
         && mc.thePlayer.rotationPitch > 50.0F
         && mc.thePlayer.getCurrentEquippedItem() != null
         && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock;
   }

   public static boolean C() {
      double d = mc.thePlayer.motionX;
      double e = mc.thePlayer.motionY;
      double d2 = mc.thePlayer.motionZ;
      double d3 = d;
      double d4 = d2;
      double d5 = d3;
      double d6 = d4;
      double d7 = 0.05;

      while (d != 0.0 && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(d, -1.0, 0.0)).isEmpty()) {
         if (d < d7 && d >= -d7) {
            d = 0.0;
         } else if (d > 0.0) {
            d -= d7;
         } else {
            d += d7;
         }
      }

      while (d2 != 0.0 && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -1.0, d2)).isEmpty()) {
         if (d2 < d7 && d2 >= -d7) {
            d2 = 0.0;
         } else if (d2 > 0.0) {
            d2 -= d7;
         } else {
            d2 += d7;
         }
      }

      while (
         d != 0.0
            && d2 != 0.0
            && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(d, -1.0, d2)).isEmpty()
      ) {
         if (d < d7 && d >= -d7) {
            d = 0.0;
         } else if (d > 0.0) {
            d -= d7;
         } else {
            d += d7;
         }

         if (d2 < d7 && d2 >= -d7) {
            d2 = 0.0;
         } else if (d2 > 0.0) {
            d2 -= d7;
         } else {
            d2 += d7;
         }
      }

      return d5 != d || d6 != d2;
   }
}
