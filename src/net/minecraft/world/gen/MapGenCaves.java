package net.minecraft.world.gen;

import com.google.common.base.Objects;
import java.util.Random;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenCaves extends MapGenBase {
   private static final long c = 122376795312720L;

   public void func_180703_a(long var1, int var3, int var4, ChunkPrimer var5, double var6, double var8, double var10) {
      this.func_180702_a(var1, var3, var4, var5, var6, var8, var10, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
   }

   public void func_180702_a(
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
      double var22 = var3 * 16 + 8;
      double var24 = var4 * 16 + 8;
      float var26 = 0.0F;
      float var27 = 0.0F;
      Random var28 = new Random(var1);
      if (var16 <= 0) {
         int var29 = this.range * 16 - 16;
         var16 = var29 - var28.nextInt(var29 / 4);
      }

      boolean var69 = false;
      if (var15 == -1) {
         var15 = var16 / 2;
         var69 = true;
      }

      int var30 = var28.nextInt(var16 / 2) + var16 / 4;
      boolean var31 = var28.nextInt(6) == 0;

      while (var15 < var16) {
         double var32 = 1.5 + MathHelper.sin(var15 * (float) Math.PI / var16) * var12 * 1.0F;
         double var34 = var32 * var17;
         float var36 = MathHelper.cos(var14);
         float var37 = MathHelper.sin(var14);
         var6 += MathHelper.cos(var13) * var36;
         var8 += var37;
         var10 += MathHelper.sin(var13) * var36;
         if (var31) {
            var14 *= 0.92F;
         } else {
            var14 *= 0.7F;
         }

         var14 += var27 * 0.1F;
         var13 += var26 * 0.1F;
         var27 *= 0.9F;
         var26 *= 0.75F;
         var27 += (var28.nextFloat() - var28.nextFloat()) * var28.nextFloat() * 2.0F;
         var26 += (var28.nextFloat() - var28.nextFloat()) * var28.nextFloat() * 4.0F;
         if (!var69 && var15 == var30 && var12 > 1.0F && var16 > 0) {
            this.func_180702_a(
               var28.nextLong(),
               var3,
               var4,
               var5,
               var6,
               var8,
               var10,
               var28.nextFloat() * 0.5F + 0.5F,
               var13 - (float) (Math.PI / 2),
               var14 / 3.0F,
               var15,
               var16,
               1.0
            );
            this.func_180702_a(
               var28.nextLong(),
               var3,
               var4,
               var5,
               var6,
               var8,
               var10,
               var28.nextFloat() * 0.5F + 0.5F,
               var13 + (float) (Math.PI / 2),
               var14 / 3.0F,
               var15,
               var16,
               1.0
            );
            return;
         }

         if (var69 || var28.nextInt(4) != 0) {
            double var38 = var6 - var22;
            double var40 = var10 - var24;
            double var42 = var16 - var15;
            double var44 = var12 + 2.0F + 16.0F;
            if (var38 * var38 + var40 * var40 - var42 * var42 > var44 * var44) {
               return;
            }

            if (var6 >= var22 - 16.0 - var32 * 2.0
               && var10 >= var24 - 16.0 - var32 * 2.0
               && var6 <= var22 + 16.0 + var32 * 2.0
               && var10 <= var24 + 16.0 + var32 * 2.0) {
               int var46 = MathHelper.floor_double(var6 - var32) - var3 * 16 - 1;
               int var47 = MathHelper.floor_double(var6 + var32) - var3 * 16 + 1;
               int var48 = MathHelper.floor_double(var8 - var34) - 1;
               int var49 = MathHelper.floor_double(var8 + var34) + 1;
               int var50 = MathHelper.floor_double(var10 - var32) - var4 * 16 - 1;
               int var51 = MathHelper.floor_double(var10 + var32) - var4 * 16 + 1;
               if (var46 < 0) {
                  var46 = 0;
               }

               if (var47 > 16) {
                  var47 = 16;
               }

               if (var48 < 1) {
                  var48 = 1;
               }

               if (var49 > 248) {
                  var49 = 248;
               }

               if (var50 < 0) {
                  var50 = 0;
               }

               if (var51 > 16) {
                  var51 = 16;
               }

               boolean var52 = false;

               for (int var53 = var46; !var52 && var53 < var47; var53++) {
                  for (int var54 = var50; !var52 && var54 < var51; var54++) {
                     for (int var55 = var49 + 1; !var52 && var55 >= var48 - 1; var55--) {
                        if (var55 >= 0 && var55 < 256) {
                           IBlockState var56 = var5.getBlockState(var53, var55, var54);
                           if (var56.getBlock() == Blocks.flowing_water || var56.getBlock() == Blocks.water) {
                              var52 = true;
                           }

                           if (var55 != var48 - 1 && var53 != var46 && var53 != var47 - 1 && var54 != var50 && var54 != var51 - 1) {
                              var55 = var48;
                           }
                        }
                     }
                  }
               }

               if (!var52) {
                  BlockPos.MutableBlockPos var70 = new BlockPos.MutableBlockPos();

                  for (int var71 = var46; var71 < var47; var71++) {
                     double var72 = (var71 + var3 * 16 + 0.5 - var6) / var32;

                     for (int var57 = var50; var57 < var51; var57++) {
                        double var58 = (var57 + var4 * 16 + 0.5 - var10) / var32;
                        boolean var60 = false;
                        if (var72 * var72 + var58 * var58 < 1.0) {
                           for (int var61 = var49; var61 > var48; var61--) {
                              double var62 = (var61 - 1 + 0.5 - var8) / var34;
                              if (var62 > -0.7 && var72 * var72 + var62 * var62 + var58 * var58 < 1.0) {
                                 IBlockState var64 = var5.getBlockState(var71, var61, var57);
                                 IBlockState var65 = Objects.firstNonNull(var5.getBlockState(var71, var61 + 1, var57), Blocks.air.getDefaultState());
                                 if (var64.getBlock() == Blocks.grass || var64.getBlock() == Blocks.mycelium) {
                                    var60 = true;
                                 }

                                 if (this.func_175793_a(var64, var65)) {
                                    if (var61 - 1 < 10) {
                                       var5.setBlockState(var71, var61, var57, Blocks.lava.getDefaultState());
                                    } else {
                                       var5.setBlockState(var71, var61, var57, Blocks.air.getDefaultState());
                                       if (var65.getBlock() == Blocks.sand) {
                                          var5.setBlockState(
                                             var71,
                                             var61 + 1,
                                             var57,
                                             var65.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND
                                                ? Blocks.red_sandstone.getDefaultState()
                                                : Blocks.sandstone.getDefaultState()
                                          );
                                       }

                                       if (var60 && var5.getBlockState(var71, var61 - 1, var57).getBlock() == Blocks.dirt) {
                                          var70.set(var71 + var3 * 16, 0, var57 + var4 * 16);
                                          var5.setBlockState(
                                             var71, var61 - 1, var57, this.worldObj.getBiomeGenForCoords(var70).topBlock.getBlock().getDefaultState()
                                          );
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (var69) {
                     break;
                  }
               }
            }
         }

         var15++;
      }
   }

   public boolean func_175793_a(IBlockState var1, IBlockState var2) {
      return var1.getBlock() == Blocks.stone
         ? true
         : (
            var1.getBlock() == Blocks.dirt
               ? true
               : (
                  var1.getBlock() == Blocks.grass
                     ? true
                     : (
                        var1.getBlock() == Blocks.hardened_clay
                           ? true
                           : (
                              var1.getBlock() == Blocks.stained_hardened_clay
                                 ? true
                                 : (
                                    var1.getBlock() == Blocks.sandstone
                                       ? true
                                       : (
                                          var1.getBlock() == Blocks.red_sandstone
                                             ? true
                                             : (
                                                var1.getBlock() == Blocks.mycelium
                                                   ? true
                                                   : (
                                                      var1.getBlock() == Blocks.snow_layer
                                                         ? true
                                                         : (var1.getBlock() == Blocks.sand || var1.getBlock() == Blocks.gravel)
                                                            && var2.getBlock().getMaterial() != Material.water
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   @Override
   public void recursiveGenerate(World var1, int var2, int var3, int var4, int var5, ChunkPrimer var6) {
      int var10 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(15) + 1) + 1);
      if (this.rand.nextInt(7) != 0) {
         var10 = 0;
      }

      for (int var11 = 0; var11 < var10; var11++) {
         double var12 = var2 * 16 + this.rand.nextInt(16);
         double var14 = this.rand.nextInt(this.rand.nextInt(120) + 8);
         double var16 = var3 * 16 + this.rand.nextInt(16);
         int var18 = 1;
         if (this.rand.nextInt(4) == 0) {
            this.func_180703_a(this.rand.nextLong(), var4, var5, var6, var12, var14, var16);
            var18 += this.rand.nextInt(4);
         }

         for (int var19 = 0; var19 < var18; var19++) {
            float var20 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float var21 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float var22 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
            if (this.rand.nextInt(10) == 0) {
               var22 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
            }

            this.func_180702_a(this.rand.nextLong(), var4, var5, var6, var12, var14, var16, var22, var20, var21, 0, 0, 1.0);
         }
      }
   }
}
