package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenTallGrass extends WorldGenerator {
   private final IBlockState tallGrassState;
   private static final long a = 128083234366648L;

   public WorldGenTallGrass(BlockTallGrass.EnumType var1) {
      this.tallGrassState = Blocks.tallgrass.getDefaultState().withProperty(BlockTallGrass.TYPE, var1);
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      Block var7;
      while (((var7 = var1.getBlockState(var3).getBlock()).getMaterial() == Material.air || var7.getMaterial() == Material.leaves) && var3.getY() > 0) {
         var3 = var3.down();
      }

      for (int var8 = 0; var8 < 128; var8++) {
         BlockPos var9 = var3.add(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.isAirBlock(var9) && Blocks.tallgrass.canBlockStay(var1, var9, this.tallGrassState)) {
            var1.setBlockState(var9, this.tallGrassState, 2);
         }
      }

      return true;
   }
}
