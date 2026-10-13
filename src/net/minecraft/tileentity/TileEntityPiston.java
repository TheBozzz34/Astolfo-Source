package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityPiston extends TileEntity implements ITickable {
   private IBlockState pistonState;
   private EnumFacing pistonFacing;
   private boolean extending;
   private boolean shouldHeadBeRendered;
   private float progress;
   private float lastProgress;
   private List<Entity> field_174933_k = Lists.newArrayList();
   private static final long c = 83372279557944L;

   public TileEntityPiston() {
   }

   public TileEntityPiston(IBlockState var1, EnumFacing var2, boolean var3, boolean var4) {
      this.pistonState = var1;
      this.pistonFacing = var2;
      this.extending = var3;
      this.shouldHeadBeRendered = var4;
   }

   public IBlockState getPistonState() {
      return this.pistonState;
   }

   @Override
   public int getBlockMetadata() {
      return 0;
   }

   public boolean isExtending() {
      return this.extending;
   }

   public EnumFacing getFacing() {
      return this.pistonFacing;
   }

   public boolean shouldPistonHeadBeRendered() {
      return this.shouldHeadBeRendered;
   }

   public float getProgress(float var1) {
      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      return this.lastProgress + (this.progress - this.lastProgress) * var1;
   }

   public float getOffsetX(float var1) {
      return this.extending
         ? (this.getProgress(var1) - 1.0F) * this.pistonFacing.getFrontOffsetX()
         : (1.0F - this.getProgress(var1)) * this.pistonFacing.getFrontOffsetX();
   }

   public float getOffsetY(float var1) {
      return this.extending
         ? (this.getProgress(var1) - 1.0F) * this.pistonFacing.getFrontOffsetY()
         : (1.0F - this.getProgress(var1)) * this.pistonFacing.getFrontOffsetY();
   }

   public float getOffsetZ(float var1) {
      return this.extending
         ? (this.getProgress(var1) - 1.0F) * this.pistonFacing.getFrontOffsetZ()
         : (1.0F - this.getProgress(var1)) * this.pistonFacing.getFrontOffsetZ();
   }

   private void launchWithSlimeBlock(float var1, float var2) {
      if (this.extending) {
         var1 = 1.0F - var1;
      } else {
         var1--;
      }

      AxisAlignedBB var6 = Blocks.piston_extension.getBoundingBox(this.worldObj, this.pos, this.pistonState, var1, this.pistonFacing);
      if (var6 != null) {
         List var7 = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, var6);
         if (!var7.isEmpty()) {
            this.field_174933_k.addAll(var7);

            for (Entity var9 : this.field_174933_k) {
               if (this.pistonState.getBlock() == Blocks.slime_block && this.extending) {
                  switch (this.pistonFacing.getAxis()) {
                     case X:
                        var9.motionX = this.pistonFacing.getFrontOffsetX();
                        break;
                     case Y:
                        var9.motionY = this.pistonFacing.getFrontOffsetY();
                        break;
                     case Z:
                        var9.motionZ = this.pistonFacing.getFrontOffsetZ();
                  }
               } else {
                  var9.moveEntity(
                     var2 * this.pistonFacing.getFrontOffsetX(), var2 * this.pistonFacing.getFrontOffsetY(), var2 * this.pistonFacing.getFrontOffsetZ()
                  );
               }
            }

            this.field_174933_k.clear();
         }
      }
   }

   public void clearPistonTileEntity() {
      if (this.lastProgress < 1.0F && this.worldObj != null) {
         this.lastProgress = this.progress = 1.0F;
         this.worldObj.removeTileEntity(this.pos);
         this.invalidate();
         if (this.worldObj.getBlockState(this.pos).getBlock() == Blocks.piston_extension) {
            this.worldObj.setBlockState(this.pos, this.pistonState, 3);
            this.worldObj.notifyBlockOfStateChange(this.pos, this.pistonState.getBlock());
         }
      }
   }

   @Override
   public void update() {
      this.lastProgress = this.progress;
      if (this.lastProgress >= 1.0F) {
         this.launchWithSlimeBlock(1.0F, 0.25F);
         this.worldObj.removeTileEntity(this.pos);
         this.invalidate();
         if (this.worldObj.getBlockState(this.pos).getBlock() == Blocks.piston_extension) {
            this.worldObj.setBlockState(this.pos, this.pistonState, 3);
            this.worldObj.notifyBlockOfStateChange(this.pos, this.pistonState.getBlock());
         }
      } else {
         this.progress += 0.5F;
         if (this.progress >= 1.0F) {
            this.progress = 1.0F;
         }

         if (this.extending) {
            this.launchWithSlimeBlock(this.progress, this.progress - this.lastProgress + 0.0625F);
         }
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      this.pistonState = Block.getBlockById(var1.getInteger("blockId")).getStateFromMeta(var1.getInteger("blockData"));
      this.pistonFacing = EnumFacing.getFront(var1.getInteger("facing"));
      this.lastProgress = this.progress = var1.getFloat("progress");
      this.extending = var1.getBoolean("extending");
   }

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      var1.setInteger("blockId", Block.getIdFromBlock(this.pistonState.getBlock()));
      var1.setInteger("blockData", this.pistonState.getBlock().getMetaFromState(this.pistonState));
      var1.setInteger("facing", this.pistonFacing.getIndex());
      var1.setFloat("progress", this.lastProgress);
      var1.setBoolean("extending", this.extending);
   }
}
