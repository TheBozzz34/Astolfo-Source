package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDoor extends Item {
   private Block block;
   private static final long a = 114344992133231L;

   public ItemDoor(Block var1) {
      this.block = var1;
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var5 != EnumFacing.UP) {
         return false;
      }

      IBlockState var12 = var3.getBlockState(var4);
      Block var13 = var12.getBlock();
      if (!var13.isReplaceable(var3, var4)) {
         var4 = var4.offset(var5);
      }

      if (!var2.canPlayerEdit(var4, var5, var1)) {
         return false;
      }

      if (!this.block.canPlaceBlockAt(var3, var4)) {
         return false;
      }

      placeDoor(var3, var4, EnumFacing.fromAngle(var2.rotationYaw), this.block);
      var1.stackSize--;
      return true;
   }

   public static void placeDoor(World var0, BlockPos var1, EnumFacing var2, Block var3) {
      BlockPos var7 = var1.offset(var2.rotateY());
      BlockPos var8 = var1.offset(var2.rotateYCCW());
      int var9 = (var0.getBlockState(var8).getBlock().isNormalCube() ? 1 : 0) + (var0.getBlockState(var8.up()).getBlock().isNormalCube() ? 1 : 0);
      int var10 = (var0.getBlockState(var7).getBlock().isNormalCube() ? 1 : 0) + (var0.getBlockState(var7.up()).getBlock().isNormalCube() ? 1 : 0);
      boolean var11 = var0.getBlockState(var8).getBlock() == var3 || var0.getBlockState(var8.up()).getBlock() == var3;
      boolean var12 = var0.getBlockState(var7).getBlock() == var3 || var0.getBlockState(var7.up()).getBlock() == var3;
      boolean var13 = false;
      if (var11 && !var12 || var10 > var9) {
         var13 = true;
      }

      BlockPos var14 = var1.up();
      IBlockState var15 = var3.getDefaultState()
         .withProperty(BlockDoor.FACING, var2)
         .withProperty(BlockDoor.HINGE, var13 ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT);
      var0.setBlockState(var1, var15.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER), 2);
      var0.setBlockState(var14, var15.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 2);
      var0.notifyNeighborsOfStateChange(var1, var3);
      var0.notifyNeighborsOfStateChange(var14, var3);
   }
}
