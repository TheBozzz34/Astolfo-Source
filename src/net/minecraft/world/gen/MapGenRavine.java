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

   public void func_180707_a(long l, int i, int j, ChunkPrimer chunkPrimer, double d, double d2, double d3, float f, float g, float h, int k, int m, double d4) {
      Random random = new Random(l);
      double e = i * 16 + 8;
      double d5 = j * 16 + 8;
      float f2 = 0.0F;
      float f3 = 0.0F;
      if (m <= 0) {
         int n = this.range * 16 - 16;
         m = n - random.nextInt(n / 4);
      }

      boolean bl = false;
      if (k == -1) {
         k = m / 2;
         bl = true;
      }

      float f4 = 1.0F;

      for (int i2 = 0; i2 < 256; i2++) {
         if (i2 == 0 || random.nextInt(3) == 0) {
            f4 = 1.0F + random.nextFloat() * random.nextFloat() * 1.0F;
         }

         this.field_75046_d[i2] = f4 * f4;
      }

      for (; k < m; k++) {
         double d6 = 1.5 + MathHelper.sin(k * (float) Math.PI / m) * f * 1.0F;
         double d7 = d6 * d4;
         d6 *= random.nextFloat() * 0.25 + 0.75;
         d7 *= random.nextFloat() * 0.25 + 0.75;
         float f5 = MathHelper.cos(h);
         float f6 = MathHelper.sin(h);
         d += MathHelper.cos(g) * f5;
         d2 += f6;
         d3 += MathHelper.sin(g) * f5;
         h *= 0.7F;
         h += f3 * 0.05F;
         g += f2 * 0.05F;
         f3 *= 0.8F;
         f2 *= 0.5F;
         f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         f2 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
         if (bl || random.nextInt(4) != 0) {
            double d8 = d - e;
            double d9 = d3 - d5;
            double d10 = m - k;
            double d11 = f + 2.0F + 16.0F;
            if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11) {
               return;
            }

            if (d >= e - 16.0 - d6 * 2.0 && d3 >= d5 - 16.0 - d6 * 2.0 && d <= e + 16.0 + d6 * 2.0 && d3 <= d5 + 16.0 + d6 * 2.0) {
               int i3 = MathHelper.floor_double(d - d6) - i * 16 - 1;
               int i4 = MathHelper.floor_double(d + d6) - i * 16 + 1;
               int i5 = MathHelper.floor_double(d2 - d7) - 1;
               int i6 = MathHelper.floor_double(d2 + d7) + 1;
               int i7 = MathHelper.floor_double(d3 - d6) - j * 16 - 1;
               int i8 = MathHelper.floor_double(d3 + d6) - j * 16 + 1;
               if (i3 < 0) {
                  i3 = 0;
               }

               if (i4 > 16) {
                  i4 = 16;
               }

               if (i5 < 1) {
                  i5 = 1;
               }

               if (i6 > 248) {
                  i6 = 248;
               }

               if (i7 < 0) {
                  i7 = 0;
               }

               if (i8 > 16) {
                  i8 = 16;
               }

               boolean bl2 = false;

               for (int i9 = i3; !bl2 && i9 < i4; i9++) {
                  for (int i10 = i7; !bl2 && i10 < i8; i10++) {
                     for (int i11 = i6 + 1; !bl2 && i11 >= i5 - 1; i11--) {
                        if (i11 >= 0 && i11 < 256) {
                           IBlockState iBlockState = chunkPrimer.getBlockState(i9, i11, i10);
                           if (iBlockState.getBlock() == Blocks.flowing_water || iBlockState.getBlock() == Blocks.water) {
                              bl2 = true;
                           }

                           if (i11 != i5 - 1 && i9 != i3 && i9 != i4 - 1 && i10 != i7 && i10 != i8 - 1) {
                              i11 = i5;
                           }
                        }
                     }
                  }
               }

               if (!bl2) {
                  BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

                  for (int i12 = i3; i12 < i4; i12++) {
                     double d12 = (i12 + i * 16 + 0.5 - d) / d6;

                     for (int i13 = i7; i13 < i8; i13++) {
                        double d13 = (i13 + j * 16 + 0.5 - d3) / d6;
                        boolean bl3 = false;
                        if (d12 * d12 + d13 * d13 < 1.0) {
                           for (int i14 = i6; i14 > i5; i14--) {
                              double d14 = (i14 - 1 + 0.5 - d2) / d7;
                              if ((d12 * d12 + d13 * d13) * this.field_75046_d[i14 - 1] + d14 * d14 / 6.0 < 1.0) {
                                 IBlockState iBlockState2 = chunkPrimer.getBlockState(i12, i14, i13);
                                 if (iBlockState2.getBlock() == Blocks.grass) {
                                    bl3 = true;
                                 }

                                 if (iBlockState2.getBlock() == Blocks.stone || iBlockState2.getBlock() == Blocks.dirt || iBlockState2.getBlock() == Blocks.grass) {
                                    if (i14 - 1 < 10) {
                                       chunkPrimer.setBlockState(i12, i14, i13, Blocks.flowing_lava.getDefaultState());
                                    } else {
                                       chunkPrimer.setBlockState(i12, i14, i13, Blocks.air.getDefaultState());
                                       if (bl3 && chunkPrimer.getBlockState(i12, i14 - 1, i13).getBlock() == Blocks.dirt) {
                                          mutableBlockPos.set(i12 + i * 16, 0, i13 + j * 16);
                                          chunkPrimer.setBlockState(i12, i14 - 1, i13, this.worldObj.getBiomeGenForCoords(mutableBlockPos).topBlock);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (bl) {
                     break;
                  }
               }
            }
         }
      }
   }

   @Override
   public void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int i, int j, ChunkPrimer chunkPrimerIn) {
      if (this.rand.nextInt(50) == 0) {
         double d = chunkX * 16 + this.rand.nextInt(16);
         double e = this.rand.nextInt(this.rand.nextInt(40) + 8) + 20;
         double d2 = chunkZ * 16 + this.rand.nextInt(16);
         byte b = 1;

         for (int k = 0; k < b; k++) {
            float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float g = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float h = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
            this.func_180707_a(this.rand.nextLong(), i, j, chunkPrimerIn, d, e, d2, h, f, g, 0, 0, 3.0);
         }
      }
   }
}
