package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenFlowers extends WorldGenerator {
   private BlockFlower flower;
   private IBlockState field_175915_b;
   private static final long a = 136945969494405L;

   public WorldGenFlowers(BlockFlower var1, BlockFlower.EnumFlowerType var2) {
      this.setGeneratedBlock(var1, var2);
   }

   public void setGeneratedBlock(BlockFlower var1, BlockFlower.EnumFlowerType var2) {
      this.flower = var1;
      this.field_175915_b = var1.getDefaultState().withProperty(var1.getTypeProperty(), var2);
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      for (int var7 = 0; var7 < 64; var7++) {
         BlockPos var8 = var3.add(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.isAirBlock(var8) && (!var1.provider.getHasNoSky() || var8.getY() < 255) && this.flower.canBlockStay(var1, var8, this.field_175915_b)) {
            var1.setBlockState(var8, this.field_175915_b, 2);
         }
      }

      return true;
   }
}
