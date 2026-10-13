package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenHugeTrees extends WorldGenAbstractTree {
   public final int baseHeight;
   public final IBlockState woodMetadata;
   public final IBlockState leavesMetadata;
   public int extraRandomHeight;
   private static final long b = 117536705502551L;

   public WorldGenHugeTrees(boolean var1, int var2, int var3, IBlockState var4, IBlockState var5) {
      super(var1);
      this.baseHeight = var2;
      this.extraRandomHeight = var3;
      this.woodMetadata = var4;
      this.leavesMetadata = var5;
   }

   public int func_150533_a(Random var1) {
      int var5 = var1.nextInt(3) + this.baseHeight;
      if (this.extraRandomHeight > 1) {
         var5 += var1.nextInt(this.extraRandomHeight);
      }

      return var5;
   }

   private boolean func_175926_c(World var1, BlockPos var2, int var3) {
      boolean var7 = true;
      if (var2.getY() >= 1 && var2.getY() + var3 + 1 <= 256) {
         for (int var8 = 0; var8 <= 1 + var3; var8++) {
            byte var9 = 2;
            if (var8 == 0) {
               var9 = 1;
            } else if (var8 >= 1 + var3 - 2) {
               var9 = 2;
            }

            for (int var10 = -var9; var10 <= var9 && var7; var10++) {
               for (int var11 = -var9; var11 <= var9 && var7; var11++) {
                  if (var2.getY() + var8 < 0 || var2.getY() + var8 >= 256 || !this.func_150523_a(var1.getBlockState(var2.add(var10, var8, var11)).getBlock())) {
                     var7 = false;
                  }
               }
            }
         }

         return var7;
      } else {
         return false;
      }
   }

   private boolean func_175927_a(BlockPos var1, World var2) {
      BlockPos var6 = var1.down();
      Block var7 = var2.getBlockState(var6).getBlock();
      if ((var7 == Blocks.grass || var7 == Blocks.dirt) && var1.getY() >= 2) {
         this.func_175921_a(var2, var6);
         this.func_175921_a(var2, var6.east());
         this.func_175921_a(var2, var6.south());
         this.func_175921_a(var2, var6.south().east());
         return true;
      } else {
         return false;
      }
   }

   public boolean func_175929_a(World var1, Random var2, BlockPos var3, int var4) {
      return this.func_175926_c(var1, var3, var4) && this.func_175927_a(var3, var1);
   }

   public void j(World var1, BlockPos var2, int var3) {
      int var7 = var3 * var3;

      for (int var8 = -var3; var8 <= var3 + 1; var8++) {
         for (int var9 = -var3; var9 <= var3 + 1; var9++) {
            int var10 = var8 - 1;
            int var11 = var9 - 1;
            if (var8 * var8 + var9 * var9 <= var7
               || var10 * var10 + var11 * var11 <= var7
               || var8 * var8 + var11 * var11 <= var7
               || var10 * var10 + var9 * var9 <= var7) {
               BlockPos var12 = var2.add(var8, 0, var9);
               Material var13 = var1.getBlockState(var12).getBlock().getMaterial();
               if (var13 == Material.air || var13 == Material.leaves) {
                  this.setBlockAndNotifyAdequately(var1, var12, this.leavesMetadata);
               }
            }
         }
      }
   }

   public void e(World var1, BlockPos var2, int var3) {
      int var7 = var3 * var3;

      for (int var8 = -var3; var8 <= var3; var8++) {
         for (int var9 = -var3; var9 <= var3; var9++) {
            if (var8 * var8 + var9 * var9 <= var7) {
               BlockPos var10 = var2.add(var8, 0, var9);
               Material var11 = var1.getBlockState(var10).getBlock().getMaterial();
               if (var11 == Material.air || var11 == Material.leaves) {
                  this.setBlockAndNotifyAdequately(var1, var10, this.leavesMetadata);
               }
            }
         }
      }
   }
}
