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
   public void func_180703_a(long l, int i, int j, ChunkPrimer chunkPrimer, double d, double e, double d2) {
      this.func_180702_a(l, i, j, chunkPrimer, d, e, d2, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
   }

   public void func_180702_a(long l, int i, int j, ChunkPrimer chunkPrimer, double d, double e, double d2, float f, float g, float h, int k, int m, double d3) {
      double d4 = i * 16 + 8;
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
         double d7 = d6 * d3;
         float f4 = MathHelper.cos(h);
         float f5 = MathHelper.sin(h);
         d += MathHelper.cos(g) * f4;
         e += f5;
         d2 += MathHelper.sin(g) * f4;
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
         if (!bl && k == i2 && f > 1.0F && m > 0) {
            this.func_180702_a(random.nextLong(), i, j, chunkPrimer, d, e, d2, random.nextFloat() * 0.5F + 0.5F, g - (float) (Math.PI / 2), h / 3.0F, k, m, 1.0);
            this.func_180702_a(random.nextLong(), i, j, chunkPrimer, d, e, d2, random.nextFloat() * 0.5F + 0.5F, g + (float) (Math.PI / 2), h / 3.0F, k, m, 1.0);
            return;
         }

         if (bl || random.nextInt(4) != 0) {
            double d8 = d - d4;
            double d9 = d2 - d5;
            double d10 = m - k;
            double d11 = f + 2.0F + 16.0F;
            if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11) {
               return;
            }

            if (d >= d4 - 16.0 - d6 * 2.0 && d2 >= d5 - 16.0 - d6 * 2.0 && d <= d4 + 16.0 + d6 * 2.0 && d2 <= d5 + 16.0 + d6 * 2.0) {
               int i3 = MathHelper.floor_double(d - d6) - i * 16 - 1;
               int i4 = MathHelper.floor_double(d + d6) - i * 16 + 1;
               int i5 = MathHelper.floor_double(e - d7) - 1;
               int i6 = MathHelper.floor_double(e + d7) + 1;
               int i7 = MathHelper.floor_double(d2 - d6) - j * 16 - 1;
               int i8 = MathHelper.floor_double(d2 + d6) - j * 16 + 1;
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

               boolean bl3 = false;

               for (int i9 = i3; !bl3 && i9 < i4; i9++) {
                  for (int i10 = i7; !bl3 && i10 < i8; i10++) {
                     for (int i11 = i6 + 1; !bl3 && i11 >= i5 - 1; i11--) {
                        if (i11 >= 0 && i11 < 256) {
                           IBlockState iBlockState = chunkPrimer.getBlockState(i9, i11, i10);
                           if (iBlockState.getBlock() == Blocks.flowing_water || iBlockState.getBlock() == Blocks.water) {
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
                  BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

                  for (int i12 = i3; i12 < i4; i12++) {
                     double d12 = (i12 + i * 16 + 0.5 - d) / d6;

                     for (int i13 = i7; i13 < i8; i13++) {
                        double d13 = (i13 + j * 16 + 0.5 - d2) / d6;
                        boolean bl4 = false;
                        if (d12 * d12 + d13 * d13 < 1.0) {
                           for (int i14 = i6; i14 > i5; i14--) {
                              double d14 = (i14 - 1 + 0.5 - e) / d7;
                              if (d14 > -0.7 && d12 * d12 + d14 * d14 + d13 * d13 < 1.0) {
                                 IBlockState iBlockState2 = chunkPrimer.getBlockState(i12, i14, i13);
                                 IBlockState iBlockState3 = Objects.firstNonNull(chunkPrimer.getBlockState(i12, i14 + 1, i13), Blocks.air.getDefaultState());
                                 if (iBlockState2.getBlock() == Blocks.grass || iBlockState2.getBlock() == Blocks.mycelium) {
                                    bl4 = true;
                                 }

                                 if (this.func_175793_a(iBlockState2, iBlockState3)) {
                                    if (i14 - 1 < 10) {
                                       chunkPrimer.setBlockState(i12, i14, i13, Blocks.lava.getDefaultState());
                                    } else {
                                       chunkPrimer.setBlockState(i12, i14, i13, Blocks.air.getDefaultState());
                                       if (iBlockState3.getBlock() == Blocks.sand) {
                                          chunkPrimer.setBlockState(
                                             i12,
                                             i14 + 1,
                                             i13,
                                             iBlockState3.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND
                                                ? Blocks.red_sandstone.getDefaultState()
                                                : Blocks.sandstone.getDefaultState()
                                          );
                                       }

                                       if (bl4 && chunkPrimer.getBlockState(i12, i14 - 1, i13).getBlock() == Blocks.dirt) {
                                          mutableBlockPos.set(i12 + i * 16, 0, i13 + j * 16);
                                          chunkPrimer.setBlockState(
                                             i12, i14 - 1, i13, this.worldObj.getBiomeGenForCoords(mutableBlockPos).topBlock.getBlock().getDefaultState()
                                          );
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

         k++;
      }
   }

   public boolean func_175793_a(IBlockState iBlockState, IBlockState iBlockState2) {
      return iBlockState.getBlock() == Blocks.stone
         ? true
         : (
            iBlockState.getBlock() == Blocks.dirt
               ? true
               : (
                  iBlockState.getBlock() == Blocks.grass
                     ? true
                     : (
                        iBlockState.getBlock() == Blocks.hardened_clay
                           ? true
                           : (
                              iBlockState.getBlock() == Blocks.stained_hardened_clay
                                 ? true
                                 : (
                                    iBlockState.getBlock() == Blocks.sandstone
                                       ? true
                                       : (
                                          iBlockState.getBlock() == Blocks.red_sandstone
                                             ? true
                                             : (
                                                iBlockState.getBlock() == Blocks.mycelium
                                                   ? true
                                                   : (
                                                      iBlockState.getBlock() == Blocks.snow_layer
                                                         ? true
                                                         : (iBlockState.getBlock() == Blocks.sand || iBlockState.getBlock() == Blocks.gravel)
                                                            && iBlockState2.getBlock().getMaterial() != Material.water
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
   public void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int i, int j, ChunkPrimer chunkPrimerIn) {
      int k = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(15) + 1) + 1);
      if (this.rand.nextInt(7) != 0) {
         k = 0;
      }

      for (int l = 0; l < k; l++) {
         double d = chunkX * 16 + this.rand.nextInt(16);
         double e = this.rand.nextInt(this.rand.nextInt(120) + 8);
         double d2 = chunkZ * 16 + this.rand.nextInt(16);
         int m = 1;
         if (this.rand.nextInt(4) == 0) {
            this.func_180703_a(this.rand.nextLong(), i, j, chunkPrimerIn, d, e, d2);
            m += this.rand.nextInt(4);
         }

         for (int n = 0; n < m; n++) {
            float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float g = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float h = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
            if (this.rand.nextInt(10) == 0) {
               h *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
            }

            this.func_180702_a(this.rand.nextLong(), i, j, chunkPrimerIn, d, e, d2, h, f, g, 0, 0, 1.0);
         }
      }
   }
}
