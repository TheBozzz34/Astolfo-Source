package bypass.event.world;

import bypass.event.CancellableEvent;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class DamageBlockEvent extends CancellableEvent {
   private final Block block;
   private final BlockPos pos;
   private int hitDelay;
   private double damageMultiplier;

   public DamageBlockEvent(Block block, BlockPos blockPos, int i, double d) {
      this.block = block;
      this.pos = blockPos;
      this.hitDelay = i;
      this.damageMultiplier = d;
   }

   public Block getBlock() {
      return this.block;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getHitDelay() {
      return this.hitDelay;
   }

   public void setHitDelay(int i) {
      this.hitDelay = i;
   }

   public double getDamageMultiplier() {
      return this.damageMultiplier;
   }

   public void setDamageMultiplier(double d) {
      this.damageMultiplier = d;
   }
}
