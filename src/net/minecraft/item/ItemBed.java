package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBed extends Item {
   private static final long a = 87270199721378L;

   public ItemBed() {
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var3.isRemote) {
         return true;
      }

      if (var5 != EnumFacing.UP) {
         return false;
      }

      IBlockState var12 = var3.getBlockState(var4);
      Block var13 = var12.getBlock();
      boolean var14 = var13.isReplaceable(var3, var4);
      if (!var14) {
         var4 = var4.up();
      }

      int var15 = MathHelper.floor_double(var2.rotationYaw * 4.0F / 360.0F + 0.5) & 3;
      EnumFacing var16 = EnumFacing.getHorizontal(var15);
      BlockPos var17 = var4.offset(var16);
      if (var2.canPlayerEdit(var4, var5, var1) && var2.canPlayerEdit(var17, var5, var1)) {
         boolean var18 = var3.getBlockState(var17).getBlock().isReplaceable(var3, var17);
         boolean var19 = var14 || var3.isAirBlock(var4);
         boolean var20 = var18 || var3.isAirBlock(var17);
         if (var19 && var20 && World.doesBlockHaveSolidTopSurface(var3, var4.down()) && World.doesBlockHaveSolidTopSurface(var3, var17.down())) {
            IBlockState var21 = Blocks.bed
               .getDefaultState()
               .withProperty(BlockBed.OCCUPIED, false)
               .withProperty(BlockBed.FACING, var16)
               .withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);
            if (var3.setBlockState(var4, var21, 3)) {
               IBlockState var22 = var21.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
               var3.setBlockState(var17, var22, 3);
            }

            var1.stackSize--;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
