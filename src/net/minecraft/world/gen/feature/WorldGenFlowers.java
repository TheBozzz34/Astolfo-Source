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

   public WorldGenFlowers(BlockFlower blockFlower, BlockFlower.EnumFlowerType enumFlowerType) {
      this.setGeneratedBlock(blockFlower, enumFlowerType);
   }

   public void setGeneratedBlock(BlockFlower blockFlower, BlockFlower.EnumFlowerType enumFlowerType) {
      this.flower = blockFlower;
      this.field_175915_b = blockFlower.getDefaultState().withProperty(blockFlower.getTypeProperty(), enumFlowerType);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      for (int i = 0; i < 64; i++) {
         BlockPos blockPos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.isAirBlock(blockPos) && (!worldIn.provider.getHasNoSky() || blockPos.getY() < 255) && this.flower.canBlockStay(worldIn, blockPos, this.field_175915_b)
            )
          {
            worldIn.setBlockState(blockPos, this.field_175915_b, 2);
         }
      }

      return true;
   }
}
