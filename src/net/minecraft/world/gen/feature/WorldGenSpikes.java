package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenSpikes extends WorldGenerator {
   private Block baseBlockRequired;
   private static final long a = 27259305234694L;

   public WorldGenSpikes(Block block) {
      this.baseBlockRequired = block;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (worldIn.isAirBlock(position) && worldIn.getBlockState(position.down()).getBlock() == this.baseBlockRequired) {
         int i = rand.nextInt(32) + 6;
         int j = rand.nextInt(4) + 1;
         BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

         for (int k = position.getX() - j; k <= position.getX() + j; k++) {
            for (int l = position.getZ() - j; l <= position.getZ() + j; l++) {
               int m = k - position.getX();
               int n = l - position.getZ();
               if (m * m + n * n <= j * j + 1
                  && worldIn.getBlockState(mutableBlockPos.set(k, position.getY() - 1, l)).getBlock() != this.baseBlockRequired) {
                  return false;
               }
            }
         }

         for (int i2 = position.getY(); i2 < position.getY() + i && i2 < 256; i2++) {
            for (int i3 = position.getX() - j; i3 <= position.getX() + j; i3++) {
               for (int i4 = position.getZ() - j; i4 <= position.getZ() + j; i4++) {
                  int i5 = i3 - position.getX();
                  int i6 = i4 - position.getZ();
                  if (i5 * i5 + i6 * i6 <= j * j + 1) {
                     worldIn.setBlockState(new BlockPos(i3, i2, i4), Blocks.obsidian.getDefaultState(), 2);
                  }
               }
            }
         }

         EntityEnderCrystal entityEnderCrystal = new EntityEnderCrystal(worldIn);
         entityEnderCrystal.setLocationAndAngles(position.getX() + 0.5F, position.getY() + i, position.getZ() + 0.5F, rand.nextFloat() * 360.0F, 0.0F);
         worldIn.spawnEntityInWorld(entityEnderCrystal);
         worldIn.setBlockState(position.up(i), Blocks.bedrock.getDefaultState(), 2);
         return true;
      } else {
         return false;
      }
   }
}
