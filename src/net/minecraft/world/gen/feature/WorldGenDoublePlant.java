package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenDoublePlant extends WorldGenerator {
   private BlockDoublePlant.EnumPlantType field_150549_a;
   private static final long a = 89849193133944L;

   public void setPlantType(BlockDoublePlant.EnumPlantType var1) {
      this.field_150549_a = var1;
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      boolean var7 = false;

      for (int var8 = 0; var8 < 64; var8++) {
         BlockPos var9 = var3.add(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.isAirBlock(var9) && (!var1.provider.getHasNoSky() || var9.getY() < 254) && Blocks.double_plant.canPlaceBlockAt(var1, var9)) {
            Blocks.double_plant.placeAt(var1, var9, this.field_150549_a, 2);
            var7 = true;
         }
      }

      return var7;
   }
}
