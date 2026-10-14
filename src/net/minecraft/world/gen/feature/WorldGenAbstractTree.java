package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenAbstractTree extends WorldGenerator {
   public WorldGenAbstractTree(boolean notify) {
      super(notify);
   }

   public boolean func_150523_a(Block block) {
      Material material = block.getMaterial();
      return material == Material.air
         || material == Material.leaves
         || block == Blocks.grass
         || block == Blocks.dirt
         || block == Blocks.log
         || block == Blocks.log2
         || block == Blocks.sapling
         || block == Blocks.vine;
   }

   public void func_180711_a(World worldIn, Random random2, BlockPos blockPos) {
   }

   public void func_175921_a(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getBlock() != Blocks.dirt) {
         this.setBlockAndNotifyAdequately(worldIn, pos, Blocks.dirt.getDefaultState());
      }
   }
}
