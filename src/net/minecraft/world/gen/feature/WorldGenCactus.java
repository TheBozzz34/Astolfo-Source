package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenCactus extends WorldGenerator {
   private static final long a = 30572182166201L;

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      for (int var7 = 0; var7 < 10; var7++) {
         BlockPos var8 = var3.add(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.isAirBlock(var8)) {
            int var9 = 1 + var2.nextInt(var2.nextInt(3) + 1);

            for (int var10 = 0; var10 < var9; var10++) {
               if (Blocks.cactus.canBlockStay(var1, var8)) {
                  var1.setBlockState(var8.up(var10), Blocks.cactus.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }
}
