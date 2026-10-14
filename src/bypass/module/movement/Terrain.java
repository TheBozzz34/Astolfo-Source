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
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.PRE
         && this.fastLadder.getValue()
         && mc.thePlayer.isOnLadder()
         && mc.gameSettings.keyBindForward.pressed
         && S(mc.thePlayer) < 1) {
         mc.thePlayer.motionY = 0.5;
      }
   };
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = moveEvent -> {
      Block block = GameUtil.getBlockAtPlayer(-0.1);
      if (!this.fastIce.getValue() || !(block instanceof BlockIce) && !(block instanceof BlockPackedIce)) {
         this.f(0.98F);
      } else {
         moveEvent.setX(moveEvent.getX() * 2.0);
         moveEvent.setZ(moveEvent.getZ() * 2.0);
         this.f(0.6F);
      }
   };

   public Terrain(Category category) {
      super(category);
      this.addSettings(this.fastIce, this.fastLadder);
      this.setHidden(false);
   }

   @Override
   public void onDisable() {
      this.f(0.98F);
      super.onDisable();
   }

   private void f(float f) {
      for (Block block : e) {
         block.slipperiness = f;
      }
   }

   public static int S(EntityPlayer entityPlayer) {
      for (int i = MathHelper.floor_double(entityPlayer.posY); i > 0; i--) {
         BlockPos blockPos = new BlockPos(MathHelper.floor_double(entityPlayer.posX), i, MathHelper.floor_double(entityPlayer.posZ));
         if (V(mc.theWorld.getBlockState(blockPos).getBlock())) {
            return MathHelper.floor_double(entityPlayer.posY) - i - 1;
         }
      }

      return -1;
   }

   private static boolean V(Block block) {
      return block != Blocks.air
         && block != Blocks.tallgrass
         && block != Blocks.deadbush
         && block != Blocks.double_plant
         && block != Blocks.lava
         && block != Blocks.water
         && block != Blocks.ladder;
   }
}
