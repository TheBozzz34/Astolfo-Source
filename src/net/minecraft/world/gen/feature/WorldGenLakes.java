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
   private static final long b = 78058737247445L;

   public WorldGenLakes(Block var1) {
      this.block = var1;
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      var3 = var3.add(-8, 0, -8);

      while (var3.getY() > 5 && var1.isAirBlock(var3)) {
         var3 = var3.down();
      }

      if (var3.getY() <= 4) {
         return false;
      }

      var3 = var3.down(4);
      boolean[] var7 = new boolean[2048];
      int var8 = var2.nextInt(4) + 4;

      for (int var9 = 0; var9 < var8; var9++) {
         double var10 = var2.nextDouble() * 6.0 + 3.0;
         double var12 = var2.nextDouble() * 4.0 + 2.0;
         double var14 = var2.nextDouble() * 6.0 + 3.0;
         double var16 = var2.nextDouble() * (16.0 - var10 - 2.0) + 1.0 + var10 / 2.0;
         double var18 = var2.nextDouble() * (8.0 - var12 - 4.0) + 2.0 + var12 / 2.0;
         double var20 = var2.nextDouble() * (16.0 - var14 - 2.0) + 1.0 + var14 / 2.0;

         for (int var22 = 1; var22 < 15; var22++) {
            for (int var23 = 1; var23 < 15; var23++) {
               for (int var24 = 1; var24 < 7; var24++) {
                  double var25 = (var22 - var16) / (var10 / 2.0);
                  double var27 = (var24 - var18) / (var12 / 2.0);
                  double var29 = (var23 - var20) / (var14 / 2.0);
                  double var31 = var25 * var25 + var27 * var27 + var29 * var29;
                  if (var31 < 1.0) {
                     var7[(var22 * 16 + var23) * 8 + var24] = true;
                  }
               }
            }
         }
      }

      for (int var35 = 0; var35 < 16; var35++) {
         for (int var40 = 0; var40 < 16; var40++) {
            for (int var11 = 0; var11 < 8; var11++) {
               boolean var49 = !var7[(var35 * 16 + var40) * 8 + var11]
                  && (
                     var35 < 15 && var7[((var35 + 1) * 16 + var40) * 8 + var11]
                        || var35 > 0 && var7[((var35 - 1) * 16 + var40) * 8 + var11]
                        || var40 < 15 && var7[(var35 * 16 + var40 + 1) * 8 + var11]
                        || var40 > 0 && var7[(var35 * 16 + (var40 - 1)) * 8 + var11]
                        || var11 < 7 && var7[(var35 * 16 + var40) * 8 + var11 + 1]
                        || var11 > 0 && var7[(var35 * 16 + var40) * 8 + (var11 - 1)]
                  );
               if (var49) {
                  Material var13 = var1.getBlockState(var3.add(var35, var11, var40)).getBlock().getMaterial();
                  if (var11 >= 4 && var13.isLiquid()) {
                     return false;
                  }

                  if (var11 < 4 && !var13.isSolid() && var1.getBlockState(var3.add(var35, var11, var40)).getBlock() != this.block) {
                     return false;
                  }
               }
            }
         }
      }

      for (int var36 = 0; var36 < 16; var36++) {
         for (int var41 = 0; var41 < 16; var41++) {
            for (int var45 = 0; var45 < 8; var45++) {
               if (var7[(var36 * 16 + var41) * 8 + var45]) {
                  var1.setBlockState(var3.add(var36, var45, var41), var45 >= 4 ? Blocks.air.getDefaultState() : this.block.getDefaultState(), 2);
               }
            }
         }
      }

      for (int var37 = 0; var37 < 16; var37++) {
         for (int var42 = 0; var42 < 16; var42++) {
            for (int var46 = 4; var46 < 8; var46++) {
               if (var7[(var37 * 16 + var42) * 8 + var46]) {
                  BlockPos var50 = var3.add(var37, var46 - 1, var42);
                  if (var1.getBlockState(var50).getBlock() == Blocks.dirt && var1.getLightFor(EnumSkyBlock.SKY, var3.add(var37, var46, var42)) > 0) {
                     BiomeGenBase var52 = var1.getBiomeGenForCoords(var50);
                     if (var52.topBlock.getBlock() == Blocks.mycelium) {
                        var1.setBlockState(var50, Blocks.mycelium.getDefaultState(), 2);
                     } else {
                        var1.setBlockState(var50, Blocks.grass.getDefaultState(), 2);
                     }
                  }
               }
            }
         }
      }

      if (this.block.getMaterial() == Material.lava) {
         for (int var38 = 0; var38 < 16; var38++) {
            for (int var43 = 0; var43 < 16; var43++) {
               for (int var47 = 0; var47 < 8; var47++) {
                  boolean var51 = !var7[(var38 * 16 + var43) * 8 + var47]
                     && (
                        var38 < 15 && var7[((var38 + 1) * 16 + var43) * 8 + var47]
                           || var38 > 0 && var7[((var38 - 1) * 16 + var43) * 8 + var47]
                           || var43 < 15 && var7[(var38 * 16 + var43 + 1) * 8 + var47]
                           || var43 > 0 && var7[(var38 * 16 + (var43 - 1)) * 8 + var47]
                           || var47 < 7 && var7[(var38 * 16 + var43) * 8 + var47 + 1]
                           || var47 > 0 && var7[(var38 * 16 + var43) * 8 + (var47 - 1)]
                     );
                  if (var51 && (var47 < 4 || var2.nextInt(2) != 0) && var1.getBlockState(var3.add(var38, var47, var43)).getBlock().getMaterial().isSolid()) {
                     var1.setBlockState(var3.add(var38, var47, var43), Blocks.stone.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      if (this.block.getMaterial() == Material.water) {
         for (int var39 = 0; var39 < 16; var39++) {
            for (int var44 = 0; var44 < 16; var44++) {
               byte var48 = 4;
               if (var1.canBlockFreezeWater(var3.add(var39, var48, var44))) {
                  var1.setBlockState(var3.add(var39, var48, var44), Blocks.ice.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }
}
