package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class GeneratorBushFeature extends WorldGenerator {
   private BlockBush field_175908_a;
   private static final long a = 22109477986329L;

   public GeneratorBushFeature(BlockBush var1) {
      this.field_175908_a = var1;
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      for (int var7 = 0; var7 < 64; var7++) {
         BlockPos var8 = var3.add(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.isAirBlock(var8)
            && (!var1.provider.getHasNoSky() || var8.getY() < 255)
            && this.field_175908_a.canBlockStay(var1, var8, this.field_175908_a.getDefaultState())) {
            var1.setBlockState(var8, this.field_175908_a.getDefaultState(), 2);
         }
      }

      return true;
   }
}
