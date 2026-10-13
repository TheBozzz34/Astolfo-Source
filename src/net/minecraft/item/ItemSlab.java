package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSlab extends ItemBlock {
   private final BlockSlab singleSlab;
   private final BlockSlab doubleSlab;
   private static final long a = 94505333926814L;

   public ItemSlab(Block var1, BlockSlab var2, BlockSlab var3) {
      super(var1);
      this.singleSlab = var2;
      this.doubleSlab = var3;
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   @Override
   public int getMetadata(int var1) {
      return var1;
   }

   @Override
   public String getUnlocalizedName(ItemStack var1) {
      return this.singleSlab.getUnlocalizedName(var1.getMetadata());
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.stackSize == 0) {
         return false;
      }

      if (!var2.canPlayerEdit(var4.offset(var5), var5, var1)) {
         return false;
      }

      Object var12 = this.singleSlab.getVariant(var1);
      IBlockState var13 = var3.getBlockState(var4);
      if (var13.getBlock() == this.singleSlab) {
         IProperty var14 = this.singleSlab.getVariantProperty();
         Comparable var15 = var13.getValue(var14);
         BlockSlab.EnumBlockHalf var16 = var13.getValue(BlockSlab.HALF);
         if ((var5 == EnumFacing.UP && var16 == BlockSlab.EnumBlockHalf.BOTTOM || var5 == EnumFacing.DOWN && var16 == BlockSlab.EnumBlockHalf.TOP)
            && var15 == var12) {
            IBlockState var17 = this.doubleSlab.getDefaultState().withProperty(var14, var15);
            if (var3.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(var3, var4, var17)) && var3.setBlockState(var4, var17, 3)) {
               var3.playSoundEffect(
                  var4.getX() + 0.5F,
                  var4.getY() + 0.5F,
                  var4.getZ() + 0.5F,
                  this.doubleSlab.stepSound.getPlaceSound(),
                  (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F,
                  this.doubleSlab.stepSound.getFrequency() * 0.8F
               );
               var1.stackSize--;
            }

            return true;
         }
      }

      return this.tryPlace(var1, var3, var4.offset(var5), var12) ? true : super.onItemUse(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public boolean canPlaceBlockOnSide(World var1, BlockPos var2, EnumFacing var3, EntityPlayer var4, ItemStack var5) {
      BlockPos var9 = var2;
      IProperty var10 = this.singleSlab.getVariantProperty();
      Object var11 = this.singleSlab.getVariant(var5);
      IBlockState var12 = var1.getBlockState(var2);
      if (var12.getBlock() == this.singleSlab) {
         boolean var13 = var12.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;
         if ((var3 == EnumFacing.UP && !var13 || var3 == EnumFacing.DOWN && var13) && var11 == var12.getValue(var10)) {
            return true;
         }
      }

      var2 = var2.offset(var3);
      IBlockState var15 = var1.getBlockState(var2);
      return var15.getBlock() == this.singleSlab && var11 == var15.getValue(var10) ? true : super.canPlaceBlockOnSide(var1, var9, var3, var4, var5);
   }

   private boolean tryPlace(ItemStack var1, World var2, BlockPos var3, Object var4) {
      IBlockState var8 = var2.getBlockState(var3);
      if (var8.getBlock() == this.singleSlab) {
         Comparable var9 = var8.getValue((IProperty)this.singleSlab.getVariantProperty());
         if (var9 == var4) {
            IBlockState var10 = this.doubleSlab.getDefaultState().withProperty((net.minecraft.block.properties.IProperty)(this.singleSlab.getVariantProperty()), var9);
            if (var2.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(var2, var3, var10)) && var2.setBlockState(var3, var10, 3)) {
               var2.playSoundEffect(
                  var3.getX() + 0.5F,
                  var3.getY() + 0.5F,
                  var3.getZ() + 0.5F,
                  this.doubleSlab.stepSound.getPlaceSound(),
                  (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F,
                  this.doubleSlab.stepSound.getFrequency() * 0.8F
               );
               var1.stackSize--;
            }

            return true;
         }
      }

      return false;
   }
}
