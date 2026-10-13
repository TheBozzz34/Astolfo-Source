package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaJungle extends WorldGenHugeTrees {
   private static final long a = 51016080922317L;

   public WorldGenMegaJungle(boolean var1, int var2, int var3, IBlockState var4, IBlockState var5) {
      super(var1, var2, var3, var4, var5);
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      int var7 = this.func_150533_a(var2);
      if (!this.func_175929_a(var1, var2, var3, var7)) {
         return false;
      }

      this.func_175930_c(var1, var3.up(var7), 2);

      for (int var8 = var3.getY() + var7 - 2 - var2.nextInt(4); var8 > var3.getY() + var7 / 2; var8 -= 2 + var2.nextInt(4)) {
         float var9 = var2.nextFloat() * (float) Math.PI * 2.0F;
         int var10 = var3.getX() + (int)(0.5F + MathHelper.cos(var9) * 4.0F);
         int var11 = var3.getZ() + (int)(0.5F + MathHelper.sin(var9) * 4.0F);

         for (int var12 = 0; var12 < 5; var12++) {
            var10 = var3.getX() + (int)(1.5F + MathHelper.cos(var9) * var12);
            var11 = var3.getZ() + (int)(1.5F + MathHelper.sin(var9) * var12);
            this.setBlockAndNotifyAdequately(var1, new BlockPos(var10, var8 - 3 + var12 / 2, var11), this.woodMetadata);
         }

         int var20 = 1 + var2.nextInt(2);
         int var13 = var8;

         for (int var14 = var8 - var20; var14 <= var13; var14++) {
            int var15 = var14 - var13;
            this.e(var1, new BlockPos(var10, var14, var11), 1 - var15);
         }
      }

      for (int var16 = 0; var16 < var7; var16++) {
         BlockPos var17 = var3.up(var16);
         if (this.func_150523_a(var1.getBlockState(var17).getBlock())) {
            this.setBlockAndNotifyAdequately(var1, var17, this.woodMetadata);
            if (var16 > 0) {
               this.func_181632_a(var1, var2, var17.west(), BlockVine.EAST);
               this.func_181632_a(var1, var2, var17.north(), BlockVine.SOUTH);
            }
         }

         if (var16 < var7 - 1) {
            BlockPos var18 = var17.east();
            if (this.func_150523_a(var1.getBlockState(var18).getBlock())) {
               this.setBlockAndNotifyAdequately(var1, var18, this.woodMetadata);
               if (var16 > 0) {
                  this.func_181632_a(var1, var2, var18.east(), BlockVine.WEST);
                  this.func_181632_a(var1, var2, var18.north(), BlockVine.SOUTH);
               }
            }

            BlockPos var19 = var17.south().east();
            if (this.func_150523_a(var1.getBlockState(var19).getBlock())) {
               this.setBlockAndNotifyAdequately(var1, var19, this.woodMetadata);
               if (var16 > 0) {
                  this.func_181632_a(var1, var2, var19.east(), BlockVine.WEST);
                  this.func_181632_a(var1, var2, var19.south(), BlockVine.NORTH);
               }
            }

            BlockPos var21 = var17.south();
            if (this.func_150523_a(var1.getBlockState(var21).getBlock())) {
               this.setBlockAndNotifyAdequately(var1, var21, this.woodMetadata);
               if (var16 > 0) {
                  this.func_181632_a(var1, var2, var21.west(), BlockVine.EAST);
                  this.func_181632_a(var1, var2, var21.south(), BlockVine.NORTH);
               }
            }
         }
      }

      return true;
   }

   private void func_181632_a(World var1, Random var2, BlockPos var3, PropertyBool var4) {
      if (var2.nextInt(3) > 0 && var1.isAirBlock(var3)) {
         this.setBlockAndNotifyAdequately(var1, var3, Blocks.vine.getDefaultState().withProperty(var4, true));
      }
   }

   private void func_175930_c(World var1, BlockPos var2, int var3) {
      byte var7 = 2;

      for (int var8 = -var7; var8 <= 0; var8++) {
         this.j(var1, var2.up(var8), var3 + 1 - var8);
      }
   }
}
