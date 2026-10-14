package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenSand extends WorldGenerator {
   private Block block;
   private int radius;
   private static final long a = 41701229110994L;

   public WorldGenSand(Block block, int i) {
      this.block = block;
      this.radius = i;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.water) {
         return false;
      }

      int i = rand.nextInt(this.radius - 2) + 2;
      byte b = 2;

      for (int j = position.getX() - i; j <= position.getX() + i; j++) {
         for (int k = position.getZ() - i; k <= position.getZ() + i; k++) {
            int l = j - position.getX();
            int m = k - position.getZ();
            if (l * l + m * m <= i * i) {
               for (int n = position.getY() - b; n <= position.getY() + b; n++) {
                  BlockPos blockPos = new BlockPos(j, n, k);
                  Block block2 = worldIn.getBlockState(blockPos).getBlock();
                  if (block2 == Blocks.dirt || block2 == Blocks.grass) {
                     worldIn.setBlockState(blockPos, this.block.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
