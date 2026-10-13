package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSnow extends ItemBlock {
   private static final long a = 109821809611813L;
   private static final long d = -6940690476303384569L;

   public ItemSnow(Block var1) {
      super(var1);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.stackSize == 0) {
         return false;
      }

      if (!var2.canPlayerEdit(var4, var5, var1)) {
         return false;
      }

      IBlockState var12 = var3.getBlockState(var4);
      Block var13 = var12.getBlock();
      BlockPos var14 = var4;
      if ((var5 != EnumFacing.UP || var13 != this.block) && !var13.isReplaceable(var3, var4)) {
         var14 = var4.offset(var5);
         var12 = var3.getBlockState(var14);
         var13 = var12.getBlock();
      }

      if (var13 == this.block) {
         int var15 = var12.getValue(BlockSnow.LAYERS);
         if (var15 <= (int)d) {
            IBlockState var16 = var12.withProperty(BlockSnow.LAYERS, var15 + 1);
            AxisAlignedBB var17 = this.block.getCollisionBoundingBox(var3, var14, var16);
            if (var17 != null && var3.checkNoEntityCollision(var17) && var3.setBlockState(var14, var16, 2)) {
               var3.playSoundEffect(
                  var14.getX() + 0.5F,
                  var14.getY() + 0.5F,
                  var14.getZ() + 0.5F,
                  this.block.stepSound.getPlaceSound(),
                  (this.block.stepSound.getVolume() + 1.0F) / 2.0F,
                  this.block.stepSound.getFrequency() * 0.8F
               );
               var1.stackSize--;
               return true;
            }
         }
      }

      return super.onItemUse(var1, var2, var3, var14, var5, var6, var7, var8);
   }

   @Override
   public int getMetadata(int var1) {
      return var1;
   }
}
