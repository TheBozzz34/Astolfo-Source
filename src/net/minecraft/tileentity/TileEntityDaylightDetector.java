package net.minecraft.tileentity;

import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.util.ITickable;

public class TileEntityDaylightDetector extends TileEntity implements ITickable {
   private static final long a = 114129700408035L;

   @Override
   public void update() {
      if (this.worldObj != null && !this.worldObj.isRemote && this.worldObj.getTotalWorldTime() % 20L == 0L) {
         this.blockType = this.getBlockType();
         if (this.blockType instanceof BlockDaylightDetector) {
            ((BlockDaylightDetector)this.blockType).updatePower(this.worldObj, this.pos);
         }
      }
   }
}
