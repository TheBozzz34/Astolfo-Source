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
   private Listener<UpdateEvent> onUpdate = var1x -> {
      if (var1x.getState() == EventState.PRE) {
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
   private Listener<SafeWalkEvent> onSafeWalk = var1x -> {
      if (mc.thePlayer != null && this.D()) {
         var1x.setSafe(true);
         var1x.cancel();
      }
   };
   private static final long c = 127982052360127L;

   public SpeedBridge(Category var1) {
      super(var1);
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
      double var3 = mc.thePlayer.motionX;
      double var5 = mc.thePlayer.motionY;
      double var7 = mc.thePlayer.motionZ;
      double var9 = var3;
      double var13 = var7;
      double var15 = var9;
      double var17 = var13;
      double var19 = 0.05;

      while (var3 != 0.0 && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(var3, -1.0, 0.0)).isEmpty()) {
         if (var3 < var19 && var3 >= -var19) {
            var3 = 0.0;
         } else if (var3 > 0.0) {
            var3 -= var19;
         } else {
            var3 += var19;
         }
      }

      while (var7 != 0.0 && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -1.0, var7)).isEmpty()) {
         if (var7 < var19 && var7 >= -var19) {
            var7 = 0.0;
         } else if (var7 > 0.0) {
            var7 -= var19;
         } else {
            var7 += var19;
         }
      }

      while (
         var3 != 0.0
            && var7 != 0.0
            && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(var3, -1.0, var7)).isEmpty()
      ) {
         if (var3 < var19 && var3 >= -var19) {
            var3 = 0.0;
         } else if (var3 > 0.0) {
            var3 -= var19;
         } else {
            var3 += var19;
         }

         if (var7 < var19 && var7 >= -var19) {
            var7 = 0.0;
         } else if (var7 > 0.0) {
            var7 -= var19;
         } else {
            var7 += var19;
         }
      }

      return var15 != var3 || var17 != var7;
   }
}
