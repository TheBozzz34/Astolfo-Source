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

   public WorldGenClay(int i) {
      this.numberOfBlocks = i;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.water) {
         return false;
      }

      int i = rand.nextInt(this.numberOfBlocks - 2) + 2;
      byte b = 1;

      for (int j = position.getX() - i; j <= position.getX() + i; j++) {
         for (int k = position.getZ() - i; k <= position.getZ() + i; k++) {
            int l = j - position.getX();
            int m = k - position.getZ();
            if (l * l + m * m <= i * i) {
               for (int n = position.getY() - b; n <= position.getY() + b; n++) {
                  BlockPos blockPos = new BlockPos(j, n, k);
                  Block block = worldIn.getBlockState(blockPos).getBlock();
                  if (block == Blocks.dirt || block == Blocks.clay) {
                     worldIn.setBlockState(blockPos, this.field_150546_a.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
