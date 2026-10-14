package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenDoublePlant extends WorldGenerator {
   private BlockDoublePlant.EnumPlantType field_150549_a;

   public void setPlantType(BlockDoublePlant.EnumPlantType enumPlantType) {
      this.field_150549_a = enumPlantType;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      boolean bl = false;

      for (int i = 0; i < 64; i++) {
         BlockPos blockPos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.isAirBlock(blockPos) && (!worldIn.provider.getHasNoSky() || blockPos.getY() < 254) && Blocks.double_plant.canPlaceBlockAt(worldIn, blockPos)) {
            Blocks.double_plant.placeAt(worldIn, blockPos, this.field_150549_a, 2);
            bl = true;
         }
      }

      return bl;
   }
}
