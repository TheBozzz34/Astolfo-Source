package net.minecraft.world.gen.feature;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMinable extends WorldGenerator {
   private final IBlockState oreBlock;
   private final int numberOfBlocks;
   private final Predicate<IBlockState> predicate;
   private static final long a = 92200702009520L;

   public WorldGenMinable(IBlockState state, int blockCount) {
      this(state, blockCount, BlockHelper.forBlock(Blocks.stone));
   }

   public WorldGenMinable(IBlockState state, int blockCount, Predicate<IBlockState> predicate) {
      this.oreBlock = state;
      this.numberOfBlocks = blockCount;
      this.predicate = predicate;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      float f = rand.nextFloat() * (float) Math.PI;
      double d = position.getX() + 8 + MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
      double e = position.getX() + 8 - MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
      double d2 = position.getZ() + 8 + MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
      double d3 = position.getZ() + 8 - MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
      double d4 = position.getY() + rand.nextInt(3) - 2;
      double d5 = position.getY() + rand.nextInt(3) - 2;

      for (int i = 0; i < this.numberOfBlocks; i++) {
         float g = (float)i / this.numberOfBlocks;
         double d6 = d + (e - d) * g;
         double d7 = d4 + (d5 - d4) * g;
         double d8 = d2 + (d3 - d2) * g;
         double d9 = rand.nextDouble() * this.numberOfBlocks / 16.0;
         double d10 = (MathHelper.sin((float) Math.PI * g) + 1.0F) * d9 + 1.0;
         double d11 = (MathHelper.sin((float) Math.PI * g) + 1.0F) * d9 + 1.0;
         int j = MathHelper.floor_double(d6 - d10 / 2.0);
         int k = MathHelper.floor_double(d7 - d11 / 2.0);
         int l = MathHelper.floor_double(d8 - d10 / 2.0);
         int m = MathHelper.floor_double(d6 + d10 / 2.0);
         int n = MathHelper.floor_double(d7 + d11 / 2.0);
         int i2 = MathHelper.floor_double(d8 + d10 / 2.0);

         for (int i3 = j; i3 <= m; i3++) {
            double d12 = (i3 + 0.5 - d6) / (d10 / 2.0);
            if (d12 * d12 < 1.0) {
               for (int i4 = k; i4 <= n; i4++) {
                  double d13 = (i4 + 0.5 - d7) / (d11 / 2.0);
                  if (d12 * d12 + d13 * d13 < 1.0) {
                     for (int i5 = l; i5 <= i2; i5++) {
                        double d14 = (i5 + 0.5 - d8) / (d10 / 2.0);
                        if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0) {
                           BlockPos blockPos = new BlockPos(i3, i4, i5);
                           if (this.predicate.apply(worldIn.getBlockState(blockPos))) {
                              worldIn.setBlockState(blockPos, this.oreBlock, 2);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
