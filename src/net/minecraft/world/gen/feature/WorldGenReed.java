package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenReed extends WorldGenerator {
   private static final long a = 2634143018499L;
   private static final long b = -8555008265841278956L;

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      for (int i = 0; i < (int)b; i++) {
         BlockPos blockPos = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));
         if (worldIn.isAirBlock(blockPos)) {
            BlockPos blockPos2 = blockPos.down();
            if (worldIn.getBlockState(blockPos2.west()).getBlock().getMaterial() == Material.water
               || worldIn.getBlockState(blockPos2.east()).getBlock().getMaterial() == Material.water
               || worldIn.getBlockState(blockPos2.north()).getBlock().getMaterial() == Material.water
               || worldIn.getBlockState(blockPos2.south()).getBlock().getMaterial() == Material.water) {
               int j = 2 + rand.nextInt(rand.nextInt(3) + 1);

               for (int k = 0; k < j; k++) {
                  if (Blocks.reeds.canBlockStay(worldIn, blockPos)) {
                     worldIn.setBlockState(blockPos.up(k), Blocks.reeds.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
