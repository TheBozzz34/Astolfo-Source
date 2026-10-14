package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenCavesHell extends MapGenBase {
   public void func_180705_a(long l, int i, int j, ChunkPrimer chunkPrimer, double d, double d2, double d3) {
      this.func_180704_a(l, i, j, chunkPrimer, d, d2, d3, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
   }

   public void func_180704_a(long l, int i, int j, ChunkPrimer chunkPrimer, double d, double d2, double d3, float f, float g, float h, int k, int m, double d4) {
      double e = i * 16 + 8;
      double d5 = j * 16 + 8;
      float f2 = 0.0F;
      float f3 = 0.0F;
      Random random = new Random(l);
      if (m <= 0) {
         int n = this.range * 16 - 16;
         m = n - random.nextInt(n / 4);
      }

      boolean bl = false;
      if (k == -1) {
         k = m / 2;
         bl = true;
      }

      int i2 = random.nextInt(m / 2) + m / 4;
      boolean bl2 = random.nextInt(6) == 0;

      while (k < m) {
         double d6 = 1.5 + MathHelper.sin(k * (float) Math.PI / m) * f * 1.0F;
         double d7 = d6 * d4;
         float f4 = MathHelper.cos(h);
         float f5 = MathHelper.sin(h);
         d += MathHelper.cos(g) * f4;
         d2 += f5;
         d3 += MathHelper.sin(g) * f4;
         if (bl2) {
            h *= 0.92F;
         } else {
            h *= 0.7F;
         }

         h += f3 * 0.1F;
         g += f2 * 0.1F;
         f3 *= 0.9F;
         f2 *= 0.75F;
         f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         f2 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
         if (!bl && k == i2 && f > 1.0F) {
            this.func_180704_a(random.nextLong(), i, j, chunkPrimer, d, d2, d3, random.nextFloat() * 0.5F + 0.5F, g - (float) (Math.PI / 2), h / 3.0F, k, m, 1.0);
            this.func_180704_a(random.nextLong(), i, j, chunkPrimer, d, d2, d3, random.nextFloat() * 0.5F + 0.5F, g + (float) (Math.PI / 2), h / 3.0F, k, m, 1.0);
            return;
         }

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

               if (i6 > 120) {
                  i6 = 120;
               }

               if (i7 < 0) {
                  i7 = 0;
               }

               if (i8 > 16) {
                  i8 = 16;
               }

               boolean bl3 = false;

               for (int i9 = i3; !bl3 && i9 < i4; i9++) {
                  for (int i10 = i7; !bl3 && i10 < i8; i10++) {
                     for (int i11 = i6 + 1; !bl3 && i11 >= i5 - 1; i11--) {
                        if (i11 >= 0 && i11 < 128) {
                           IBlockState iBlockState = chunkPrimer.getBlockState(i9, i11, i10);
                           if (iBlockState.getBlock() == Blocks.flowing_lava || iBlockState.getBlock() == Blocks.lava) {
                              bl3 = true;
                           }

                           if (i11 != i5 - 1 && i9 != i3 && i9 != i4 - 1 && i10 != i7 && i10 != i8 - 1) {
                              i11 = i5;
                           }
                        }
                     }
                  }
               }

               if (!bl3) {
                  for (int i12 = i3; i12 < i4; i12++) {
                     double d12 = (i12 + i * 16 + 0.5 - d) / d6;

                     for (int i13 = i7; i13 < i8; i13++) {
                        double d13 = (i13 + j * 16 + 0.5 - d3) / d6;

                        for (int i14 = i6; i14 > i5; i14--) {
                           double d14 = (i14 - 1 + 0.5 - d2) / d7;
                           if (d14 > -0.7 && d12 * d12 + d14 * d14 + d13 * d13 < 1.0) {
                              IBlockState iBlockState2 = chunkPrimer.getBlockState(i12, i14, i13);
                              if (iBlockState2.getBlock() == Blocks.netherrack || iBlockState2.getBlock() == Blocks.dirt || iBlockState2.getBlock() == Blocks.grass) {
                                 chunkPrimer.setBlockState(i12, i14, i13, Blocks.air.getDefaultState());
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

         k++;
      }
   }

   @Override
   public void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int i, int j, ChunkPrimer chunkPrimerIn) {
      int k = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(10) + 1) + 1);
      if (this.rand.nextInt(5) != 0) {
         k = 0;
      }

      for (int l = 0; l < k; l++) {
         double d = chunkX * 16 + this.rand.nextInt(16);
         double e = this.rand.nextInt(128);
         double d2 = chunkZ * 16 + this.rand.nextInt(16);
         int m = 1;
         if (this.rand.nextInt(4) == 0) {
            this.func_180705_a(this.rand.nextLong(), i, j, chunkPrimerIn, d, e, d2);
            m += this.rand.nextInt(4);
         }

         for (int n = 0; n < m; n++) {
            float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float g = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float h = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
            this.func_180704_a(this.rand.nextLong(), i, j, chunkPrimerIn, d, e, d2, h * 2.0F, f, g, 0, 0, 0.5);
         }
      }
   }
}
