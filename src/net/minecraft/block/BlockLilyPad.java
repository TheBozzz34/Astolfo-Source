package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLilyPad extends BlockBush {
   private static final long e = 71516616995897L;

   public BlockLilyPad() {
      float var1 = 0.5F;
      float var2 = 0.015625F;
      this.setBlockBounds(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var2, 0.5F + var1);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public void addCollisionBoxesToList(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      if (var6 == null || !(var6 instanceof EntityBoat)) {
         super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      return new AxisAlignedBB(
         var2.getX() + this.minX, var2.getY() + this.minY, var2.getZ() + this.minZ, var2.getX() + this.maxX, var2.getY() + this.maxY, var2.getZ() + this.maxZ
      );
   }

   @Override
   public int getBlockColor() {
      return 7455580;
   }

   @Override
   public int getRenderColor(IBlockState var1) {
      return 7455580;
   }

   @Override
   public int colorMultiplier(IBlockAccess var1, BlockPos var2, int var3) {
      return 2129968;
   }

   @Override
   public boolean canPlaceBlockOn(Block var1) {
      return var1 == Blocks.water;
   }

   @Override
   public boolean canBlockStay(World var1, BlockPos var2, IBlockState var3) {
      if (var2.getY() >= 0 && var2.getY() < 256) {
         IBlockState var7 = var1.getBlockState(var2.down());
         return var7.getBlock().getMaterial() == Material.water && var7.getValue(BlockLiquid.LEVEL) == 0;
      } else {
         return false;
      }
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return 0;
   }
}
