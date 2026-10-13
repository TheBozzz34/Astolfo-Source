package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenClay extends WorldGenerator {
   private Block field_150546_a = Blocks.clay;
   private int numberOfBlocks;
   private static final long a = 46988927874484L;

   public WorldGenClay(int var1) {
      this.numberOfBlocks = var1;
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      if (var1.getBlockState(var3).getBlock().getMaterial() != Material.water) {
         return false;
      }

      int var7 = var2.nextInt(this.numberOfBlocks - 2) + 2;
      byte var8 = 1;

      for (int var9 = var3.getX() - var7; var9 <= var3.getX() + var7; var9++) {
         for (int var10 = var3.getZ() - var7; var10 <= var3.getZ() + var7; var10++) {
            int var11 = var9 - var3.getX();
            int var12 = var10 - var3.getZ();
            if (var11 * var11 + var12 * var12 <= var7 * var7) {
               for (int var13 = var3.getY() - var8; var13 <= var3.getY() + var8; var13++) {
                  BlockPos var14 = new BlockPos(var9, var13, var10);
                  Block var15 = var1.getBlockState(var14).getBlock();
                  if (var15 == Blocks.dirt || var15 == Blocks.clay) {
                     var1.setBlockState(var14, this.field_150546_a.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
