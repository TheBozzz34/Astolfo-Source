package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenLiquids extends WorldGenerator {
   private Block block;
   private static final long a = 135598474088620L;

   public WorldGenLiquids(Block var1) {
      this.block = var1;
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      if (var1.getBlockState(var3.up()).getBlock() != Blocks.stone) {
         return false;
      }

      if (var1.getBlockState(var3.down()).getBlock() != Blocks.stone) {
         return false;
      }

      if (var1.getBlockState(var3).getBlock().getMaterial() != Material.air && var1.getBlockState(var3).getBlock() != Blocks.stone) {
         return false;
      }

      int var7 = 0;
      if (var1.getBlockState(var3.west()).getBlock() == Blocks.stone) {
         var7++;
      }

      if (var1.getBlockState(var3.east()).getBlock() == Blocks.stone) {
         var7++;
      }

      if (var1.getBlockState(var3.north()).getBlock() == Blocks.stone) {
         var7++;
      }

      if (var1.getBlockState(var3.south()).getBlock() == Blocks.stone) {
         var7++;
      }

      int var8 = 0;
      if (var1.isAirBlock(var3.west())) {
         var8++;
      }

      if (var1.isAirBlock(var3.east())) {
         var8++;
      }

      if (var1.isAirBlock(var3.north())) {
         var8++;
      }

      if (var1.isAirBlock(var3.south())) {
         var8++;
      }

      if (var7 == 3 && var8 == 1) {
         var1.setBlockState(var3, this.block.getDefaultState(), 2);
         var1.forceBlockUpdateTick(this.block, var3, var2);
      }

      return true;
   }
}
