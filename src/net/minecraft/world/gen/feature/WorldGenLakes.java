package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class WorldGenLakes extends WorldGenerator {
   private Block block;

   public WorldGenLakes(Block blockIn) {
      this.block = blockIn;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      position = position.add(-8, 0, -8);

      while (position.getY() > 5 && worldIn.isAirBlock(position)) {
         position = position.down();
      }

      if (position.getY() <= 4) {
         return false;
      }

      position = position.down(4);
      boolean[] booleans = new boolean[2048];
      int i = rand.nextInt(4) + 4;

      for (int j = 0; j < i; j++) {
         double d = rand.nextDouble() * 6.0 + 3.0;
         double e = rand.nextDouble() * 4.0 + 2.0;
         double d2 = rand.nextDouble() * 6.0 + 3.0;
         double d3 = rand.nextDouble() * (16.0 - d - 2.0) + 1.0 + d / 2.0;
         double d4 = rand.nextDouble() * (8.0 - e - 4.0) + 2.0 + e / 2.0;
         double d5 = rand.nextDouble() * (16.0 - d2 - 2.0) + 1.0 + d2 / 2.0;

         for (int k = 1; k < 15; k++) {
            for (int l = 1; l < 15; l++) {
               for (int m = 1; m < 7; m++) {
                  double d6 = (k - d3) / (d / 2.0);
                  double d7 = (m - d4) / (e / 2.0);
                  double d8 = (l - d5) / (d2 / 2.0);
                  double d9 = d6 * d6 + d7 * d7 + d8 * d8;
                  if (d9 < 1.0) {
                     booleans[(k * 16 + l) * 8 + m] = true;
                  }
               }
            }
         }
      }

      for (int n = 0; n < 16; n++) {
         for (int i2 = 0; i2 < 16; i2++) {
            for (int i3 = 0; i3 < 8; i3++) {
               boolean bl = !booleans[(n * 16 + i2) * 8 + i3]
                  && (
                     n < 15 && booleans[((n + 1) * 16 + i2) * 8 + i3]
                        || n > 0 && booleans[((n - 1) * 16 + i2) * 8 + i3]
                        || i2 < 15 && booleans[(n * 16 + i2 + 1) * 8 + i3]
                        || i2 > 0 && booleans[(n * 16 + (i2 - 1)) * 8 + i3]
                        || i3 < 7 && booleans[(n * 16 + i2) * 8 + i3 + 1]
                        || i3 > 0 && booleans[(n * 16 + i2) * 8 + (i3 - 1)]
                  );
               if (bl) {
                  Material material = worldIn.getBlockState(position.add(n, i3, i2)).getBlock().getMaterial();
                  if (i3 >= 4 && material.isLiquid()) {
                     return false;
                  }

                  if (i3 < 4 && !material.isSolid() && worldIn.getBlockState(position.add(n, i3, i2)).getBlock() != this.block) {
                     return false;
                  }
               }
            }
         }
      }

      for (int i4 = 0; i4 < 16; i4++) {
         for (int i5 = 0; i5 < 16; i5++) {
            for (int i6 = 0; i6 < 8; i6++) {
               if (booleans[(i4 * 16 + i5) * 8 + i6]) {
                  worldIn.setBlockState(position.add(i4, i6, i5), i6 >= 4 ? Blocks.air.getDefaultState() : this.block.getDefaultState(), 2);
               }
            }
         }
      }

      for (int i7 = 0; i7 < 16; i7++) {
         for (int i8 = 0; i8 < 16; i8++) {
            for (int i9 = 4; i9 < 8; i9++) {
               if (booleans[(i7 * 16 + i8) * 8 + i9]) {
                  BlockPos blockPos = position.add(i7, i9 - 1, i8);
                  if (worldIn.getBlockState(blockPos).getBlock() == Blocks.dirt && worldIn.getLightFor(EnumSkyBlock.SKY, position.add(i7, i9, i8)) > 0) {
                     BiomeGenBase biomeGenBase = worldIn.getBiomeGenForCoords(blockPos);
                     if (biomeGenBase.topBlock.getBlock() == Blocks.mycelium) {
                        worldIn.setBlockState(blockPos, Blocks.mycelium.getDefaultState(), 2);
                     } else {
                        worldIn.setBlockState(blockPos, Blocks.grass.getDefaultState(), 2);
                     }
                  }
               }
            }
         }
      }

      if (this.block.getMaterial() == Material.lava) {
         for (int i10 = 0; i10 < 16; i10++) {
            for (int i11 = 0; i11 < 16; i11++) {
               for (int i12 = 0; i12 < 8; i12++) {
                  boolean bl2 = !booleans[(i10 * 16 + i11) * 8 + i12]
                     && (
                        i10 < 15 && booleans[((i10 + 1) * 16 + i11) * 8 + i12]
                           || i10 > 0 && booleans[((i10 - 1) * 16 + i11) * 8 + i12]
                           || i11 < 15 && booleans[(i10 * 16 + i11 + 1) * 8 + i12]
                           || i11 > 0 && booleans[(i10 * 16 + (i11 - 1)) * 8 + i12]
                           || i12 < 7 && booleans[(i10 * 16 + i11) * 8 + i12 + 1]
                           || i12 > 0 && booleans[(i10 * 16 + i11) * 8 + (i12 - 1)]
                     );
                  if (bl2
                     && (i12 < 4 || rand.nextInt(2) != 0)
                     && worldIn.getBlockState(position.add(i10, i12, i11)).getBlock().getMaterial().isSolid()) {
                     worldIn.setBlockState(position.add(i10, i12, i11), Blocks.stone.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      if (this.block.getMaterial() == Material.water) {
         for (int i13 = 0; i13 < 16; i13++) {
            for (int i14 = 0; i14 < 16; i14++) {
               byte b = 4;
               if (worldIn.canBlockFreezeWater(position.add(i13, b, i14))) {
                  worldIn.setBlockState(position.add(i13, b, i14), Blocks.ice.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }
}
