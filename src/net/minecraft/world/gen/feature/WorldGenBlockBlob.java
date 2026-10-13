package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenBlockBlob extends WorldGenerator {
   private final Block field_150545_a;
   private final int field_150544_b;
   private static final long a = 126520745736234L;

   public WorldGenBlockBlob(Block var1, int var2) {
      super(false);
      this.field_150545_a = var1;
      this.field_150544_b = var2;
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      while (true) {
         label50: {
            if (var3.getY() > 3) {
               if (var1.isAirBlock(var3.down())) {
                  break label50;
               }

               Block var7 = var1.getBlockState(var3.down()).getBlock();
               if (var7 != Blocks.grass && var7 != Blocks.dirt && var7 != Blocks.stone) {
                  break label50;
               }
            }

            if (var3.getY() <= 3) {
               return false;
            }

            int var15 = this.field_150544_b;

            for (int var8 = 0; var15 >= 0 && var8 < 3; var8++) {
               int var9 = var15 + var2.nextInt(2);
               int var10 = var15 + var2.nextInt(2);
               int var11 = var15 + var2.nextInt(2);
               float var12 = (var9 + var10 + var11) * 0.333F + 0.5F;

               for (BlockPos var14 : BlockPos.getAllInBox(var3.add(-var9, -var10, -var11), var3.add(var9, var10, var11))) {
                  if (var14.distanceSq(var3) <= var12 * var12) {
                     var1.setBlockState(var14, this.field_150545_a.getDefaultState(), 4);
                  }
               }

               var3 = var3.add(-(var15 + 1) + var2.nextInt(2 + var15 * 2), 0 - var2.nextInt(2), -(var15 + 1) + var2.nextInt(2 + var15 * 2));
            }

            return true;
         }

         var3 = var3.down();
      }
   }
}
