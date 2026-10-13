package bypass.event.world;

import bypass.event.CancellableEvent;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class DamageBlockEvent extends CancellableEvent {
   private final Block block;
   private final BlockPos pos;
   private int hitDelay;
   private double damageMultiplier;

   public DamageBlockEvent(Block var1, BlockPos var2, int var3, double var4) {
      this.block = var1;
      this.pos = var2;
      this.hitDelay = var3;
      this.damageMultiplier = var4;
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

   public void setHitDelay(int var1) {
      this.hitDelay = var1;
   }

   public double getDamageMultiplier() {
      return this.damageMultiplier;
   }

   public void setDamageMultiplier(double var1) {
      this.damageMultiplier = var1;
   }
}
