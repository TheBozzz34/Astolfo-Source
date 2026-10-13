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
   public boolean generate(World var1, Random var2, BlockPos var3) {
      for (int var7 = 0; var7 < (int)b; var7++) {
         BlockPos var8 = var3.add(var2.nextInt(4) - var2.nextInt(4), 0, var2.nextInt(4) - var2.nextInt(4));
         if (var1.isAirBlock(var8)) {
            BlockPos var9 = var8.down();
            if (var1.getBlockState(var9.west()).getBlock().getMaterial() == Material.water
               || var1.getBlockState(var9.east()).getBlock().getMaterial() == Material.water
               || var1.getBlockState(var9.north()).getBlock().getMaterial() == Material.water
               || var1.getBlockState(var9.south()).getBlock().getMaterial() == Material.water) {
               int var10 = 2 + var2.nextInt(var2.nextInt(3) + 1);

               for (int var11 = 0; var11 < var10; var11++) {
                  if (Blocks.reeds.canBlockStay(var1, var8)) {
                     var1.setBlockState(var8.up(var11), Blocks.reeds.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
