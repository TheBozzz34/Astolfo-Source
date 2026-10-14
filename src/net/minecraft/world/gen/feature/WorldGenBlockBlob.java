package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenBlockBlob extends WorldGenerator {
   private final Block field_150545_a;
   private final int field_150544_b;

   public WorldGenBlockBlob(Block block, int i) {
      super(false);
      this.field_150545_a = block;
      this.field_150544_b = i;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      while (true) {
         label50: {
            if (position.getY() > 3) {
               if (worldIn.isAirBlock(position.down())) {
                  break label50;
               }

               Block block = worldIn.getBlockState(position.down()).getBlock();
               if (block != Blocks.grass && block != Blocks.dirt && block != Blocks.stone) {
                  break label50;
               }
            }

            if (position.getY() <= 3) {
               return false;
            }

            int i = this.field_150544_b;

            for (int j = 0; i >= 0 && j < 3; j++) {
               int k = i + rand.nextInt(2);
               int l = i + rand.nextInt(2);
               int m = i + rand.nextInt(2);
               float f = (k + l + m) * 0.333F + 0.5F;

               for (BlockPos blockPos : BlockPos.getAllInBox(position.add(-k, -l, -m), position.add(k, l, m))) {
                  if (blockPos.distanceSq(position) <= f * f) {
                     worldIn.setBlockState(blockPos, this.field_150545_a.getDefaultState(), 4);
                  }
               }

               position = position.add(-(i + 1) + rand.nextInt(2 + i * 2), 0 - rand.nextInt(2), -(i + 1) + rand.nextInt(2 + i * 2));
            }

            return true;
         }

         position = position.down();
      }
   }
}
