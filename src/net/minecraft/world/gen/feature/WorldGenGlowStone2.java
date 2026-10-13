package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenGlowStone2 extends WorldGenerator {
   private static final long a = 124207561199017L;

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      if (!var1.isAirBlock(var3)) {
         return false;
      }

      if (var1.getBlockState(var3.up()).getBlock() != Blocks.netherrack) {
         return false;
      }

      var1.setBlockState(var3, Blocks.glowstone.getDefaultState(), 2);

      for (int var7 = 0; var7 < 1500; var7++) {
         BlockPos var8 = var3.add(var2.nextInt(8) - var2.nextInt(8), -var2.nextInt(12), var2.nextInt(8) - var2.nextInt(8));
         if (var1.getBlockState(var8).getBlock().getMaterial() == Material.air) {
            int var9 = 0;

            for (EnumFacing var13 : EnumFacing.values()) {
               if (var1.getBlockState(var8.offset(var13)).getBlock() == Blocks.glowstone) {
                  var9++;
               }

               if (var9 > 1) {
                  break;
               }
            }

            if (var9 == 1) {
               var1.setBlockState(var8, Blocks.glowstone.getDefaultState(), 2);
            }
         }
      }

      return true;
   }
}
