package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenRavine extends MapGenBase {
   private float[] field_75046_d = new float[1024];
   private static final long c = 84305987719186L;

   public void func_180707_a(
      long var1,
      int var3,
      int var4,
      ChunkPrimer var5,
      double var6,
      double var8,
      double var10,
      float var12,
      float var13,
      float var14,
      int var15,
      int var16,
      double var17
   ) {
      Random var22 = new Random(var1);
      double var23 = var3 * 16 + 8;
      double var25 = var4 * 16 + 8;
      float var27 = 0.0F;
      float var28 = 0.0F;
      if (var16 <= 0) {
         int var29 = this.range * 16 - 16;
         var16 = var29 - var22.nextInt(var29 / 4);
      }

      boolean var67 = false;
      if (var15 == -1) {
         var15 = var16 / 2;
         var67 = true;
      }

      float var30 = 1.0F;

      for (int var31 = 0; var31 < 256; var31++) {
         if (var31 == 0 || var22.nextInt(3) == 0) {
            var30 = 1.0F + var22.nextFloat() * var22.nextFloat() * 1.0F;
         }

         this.field_75046_d[var31] = var30 * var30;
      }

      for (; var15 < var16; var15++) {
         double var68 = 1.5 + MathHelper.sin(var15 * (float) Math.PI / var16) * var12 * 1.0F;
         double var33 = var68 * var17;
         var68 *= var22.nextFloat() * 0.25 + 0.75;
         var33 *= var22.nextFloat() * 0.25 + 0.75;
         float var35 = MathHelper.cos(var14);
         float var36 = MathHelper.sin(var14);
         var6 += MathHelper.cos(var13) * var35;
         var8 += var36;
         var10 += MathHelper.sin(var13) * var35;
         var14 *= 0.7F;
         var14 += var28 * 0.05F;
         var13 += var27 * 0.05F;
         var28 *= 0.8F;
         var27 *= 0.5F;
         var28 += (var22.nextFloat() - var22.nextFloat()) * var22.nextFloat() * 2.0F;
         var27 += (var22.nextFloat() - var22.nextFloat()) * var22.nextFloat() * 4.0F;
         if (var67 || var22.nextInt(4) != 0) {
            double var37 = var6 - var23;
            double var39 = var10 - var25;
            double var41 = var16 - var15;
            double var43 = var12 + 2.0F + 16.0F;
            if (var37 * var37 + var39 * var39 - var41 * var41 > var43 * var43) {
               return;
            }

            if (var6 >= var23 - 16.0 - var68 * 2.0
               && var10 >= var25 - 16.0 - var68 * 2.0
               && var6 <= var23 + 16.0 + var68 * 2.0
               && var10 <= var25 + 16.0 + var68 * 2.0) {
               int var45 = MathHelper.floor_double(var6 - var68) - var3 * 16 - 1;
               int var46 = MathHelper.floor_double(var6 + var68) - var3 * 16 + 1;
               int var47 = MathHelper.floor_double(var8 - var33) - 1;
               int var48 = MathHelper.floor_double(var8 + var33) + 1;
               int var49 = MathHelper.floor_double(var10 - var68) - var4 * 16 - 1;
               int var50 = MathHelper.floor_double(var10 + var68) - var4 * 16 + 1;
               if (var45 < 0) {
                  var45 = 0;
               }

               if (var46 > 16) {
                  var46 = 16;
               }

               if (var47 < 1) {
                  var47 = 1;
               }

               if (var48 > 248) {
                  var48 = 248;
               }

               if (var49 < 0) {
                  var49 = 0;
               }

               if (var50 > 16) {
                  var50 = 16;
               }

               boolean var51 = false;

               for (int var52 = var45; !var51 && var52 < var46; var52++) {
                  for (int var53 = var49; !var51 && var53 < var50; var53++) {
                     for (int var54 = var48 + 1; !var51 && var54 >= var47 - 1; var54--) {
                        if (var54 >= 0 && var54 < 256) {
                           IBlockState var55 = var5.getBlockState(var52, var54, var53);
                           if (var55.getBlock() == Blocks.flowing_water || var55.getBlock() == Blocks.water) {
                              var51 = true;
                           }

                           if (var54 != var47 - 1 && var52 != var45 && var52 != var46 - 1 && var53 != var49 && var53 != var50 - 1) {
                              var54 = var47;
                           }
                        }
                     }
                  }
               }

               if (!var51) {
                  BlockPos.MutableBlockPos var71 = new BlockPos.MutableBlockPos();

                  for (int var72 = var45; var72 < var46; var72++) {
                     double var73 = (var72 + var3 * 16 + 0.5 - var6) / var68;

                     for (int var56 = var49; var56 < var50; var56++) {
                        double var57 = (var56 + var4 * 16 + 0.5 - var10) / var68;
                        boolean var59 = false;
                        if (var73 * var73 + var57 * var57 < 1.0) {
                           for (int var60 = var48; var60 > var47; var60--) {
                              double var61 = (var60 - 1 + 0.5 - var8) / var33;
                              if ((var73 * var73 + var57 * var57) * this.field_75046_d[var60 - 1] + var61 * var61 / 6.0 < 1.0) {
                                 IBlockState var63 = var5.getBlockState(var72, var60, var56);
                                 if (var63.getBlock() == Blocks.grass) {
                                    var59 = true;
                                 }

                                 if (var63.getBlock() == Blocks.stone || var63.getBlock() == Blocks.dirt || var63.getBlock() == Blocks.grass) {
                                    if (var60 - 1 < 10) {
                                       var5.setBlockState(var72, var60, var56, Blocks.flowing_lava.getDefaultState());
                                    } else {
                                       var5.setBlockState(var72, var60, var56, Blocks.air.getDefaultState());
                                       if (var59 && var5.getBlockState(var72, var60 - 1, var56).getBlock() == Blocks.dirt) {
                                          var71.set(var72 + var3 * 16, 0, var56 + var4 * 16);
                                          var5.setBlockState(var72, var60 - 1, var56, this.worldObj.getBiomeGenForCoords(var71).topBlock);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (var67) {
                     break;
                  }
               }
            }
         }
      }
   }

   @Override
   public void recursiveGenerate(World var1, int var2, int var3, int var4, int var5, ChunkPrimer var6) {
      if (this.rand.nextInt(50) == 0) {
         double var10 = var2 * 16 + this.rand.nextInt(16);
         double var12 = this.rand.nextInt(this.rand.nextInt(40) + 8) + 20;
         double var14 = var3 * 16 + this.rand.nextInt(16);
         byte var16 = 1;

         for (int var17 = 0; var17 < var16; var17++) {
            float var18 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float var19 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float var20 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
            this.func_180707_a(this.rand.nextLong(), var4, var5, var6, var10, var12, var14, var20, var18, var19, 0, 0, 3.0);
         }
      }
   }
}
