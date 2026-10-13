package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.util.GameUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public final class Terrain extends Module {
   private final BooleanSetting fastIce = new BooleanSetting("Fast-Ice", "Goes fast on ice", true);
   private final BooleanSetting fastLadder = new BooleanSetting("Fast-Ladder", "Pushes you up when about to ladder", true);
   private static final Block[] e = new Block[]{Blocks.ice, Blocks.packed_ice};
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = var1x -> {
      if (var1x.getState() == EventState.PRE
         && this.fastLadder.getValue()
         && mc.thePlayer.isOnLadder()
         && mc.gameSettings.keyBindForward.pressed
         && S(mc.thePlayer) < 1) {
         mc.thePlayer.motionY = 0.5;
      }
   };
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = var1x -> {
      Block var5 = GameUtil.getBlockAtPlayer(-0.1);
      if (!this.fastIce.getValue() || !(var5 instanceof BlockIce) && !(var5 instanceof BlockPackedIce)) {
         this.f(0.98F);
      } else {
         var1x.setX(var1x.getX() * 2.0);
         var1x.setZ(var1x.getZ() * 2.0);
         this.f(0.6F);
      }
   };
   private static final long c = 1534971002335L;

   public Terrain(Category var1) {
      super(var1);
      this.addSettings(this.fastIce, this.fastLadder);
      this.setHidden(false);
   }

   @Override
   public void onDisable() {
      this.f(0.98F);
      super.onDisable();
   }

   private void f(float var1) {
      for (Block var8 : e) {
         var8.slipperiness = var1;
      }
   }

   public static int S(EntityPlayer var0) {
      for (int var4 = MathHelper.floor_double(var0.posY); var4 > 0; var4--) {
         BlockPos var5 = new BlockPos(MathHelper.floor_double(var0.posX), var4, MathHelper.floor_double(var0.posZ));
         if (V(mc.theWorld.getBlockState(var5).getBlock())) {
            return MathHelper.floor_double(var0.posY) - var4 - 1;
         }
      }

      return -1;
   }

   private static boolean V(Block var0) {
      return var0 != Blocks.air
         && var0 != Blocks.tallgrass
         && var0 != Blocks.deadbush
         && var0 != Blocks.double_plant
         && var0 != Blocks.lava
         && var0 != Blocks.water
         && var0 != Blocks.ladder;
   }
}
