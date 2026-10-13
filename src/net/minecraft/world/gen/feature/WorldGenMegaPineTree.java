package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaPineTree extends WorldGenHugeTrees {
   private static final IBlockState field_181633_e = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
   private static final IBlockState field_181634_f = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE)
      .withProperty(BlockLeaves.CHECK_DECAY, false);
   private static final IBlockState field_181635_g = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL);
   private boolean useBaseHeight;
   private static final long c = 25446705572005L;

   public WorldGenMegaPineTree(boolean var1, boolean var2) {
      super(var1, 13, 15, field_181633_e, field_181634_f);
      this.useBaseHeight = var2;
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      int var7 = this.func_150533_a(var2);
      if (!this.func_175929_a(var1, var2, var3, var7)) {
         return false;
      }

      this.func_150541_c(var1, var3.getX(), var3.getZ(), var3.getY() + var7, 0, var2);

      for (int var8 = 0; var8 < var7; var8++) {
         Block var9 = var1.getBlockState(var3.up(var8)).getBlock();
         if (var9.getMaterial() == Material.air || var9.getMaterial() == Material.leaves) {
            this.setBlockAndNotifyAdequately(var1, var3.up(var8), this.woodMetadata);
         }

         if (var8 < var7 - 1) {
            var9 = var1.getBlockState(var3.add(1, var8, 0)).getBlock();
            if (var9.getMaterial() == Material.air || var9.getMaterial() == Material.leaves) {
               this.setBlockAndNotifyAdequately(var1, var3.add(1, var8, 0), this.woodMetadata);
            }

            var9 = var1.getBlockState(var3.add(1, var8, 1)).getBlock();
            if (var9.getMaterial() == Material.air || var9.getMaterial() == Material.leaves) {
               this.setBlockAndNotifyAdequately(var1, var3.add(1, var8, 1), this.woodMetadata);
            }

            var9 = var1.getBlockState(var3.add(0, var8, 1)).getBlock();
            if (var9.getMaterial() == Material.air || var9.getMaterial() == Material.leaves) {
               this.setBlockAndNotifyAdequately(var1, var3.add(0, var8, 1), this.woodMetadata);
            }
         }
      }

      return true;
   }

   private void func_150541_c(World var1, int var2, int var3, int var4, int var5, Random var6) {
      int var10 = var6.nextInt(5) + (this.useBaseHeight ? this.baseHeight : 3);
      int var11 = 0;

      for (int var12 = var4 - var10; var12 <= var4; var12++) {
         int var13 = var4 - var12;
         int var14 = var5 + MathHelper.floor_float((float)var13 / var10 * 3.5F);
         this.j(var1, new BlockPos(var2, var12, var3), var14 + (var13 > 0 && var14 == var11 && (var12 & 1) == 0 ? 1 : 0));
         var11 = var14;
      }
   }

   @Override
   public void func_180711_a(World var1, Random var2, BlockPos var3) {
      this.func_175933_b(var1, var3.west().north());
      this.func_175933_b(var1, var3.east(2).north());
      this.func_175933_b(var1, var3.west().south(2));
      this.func_175933_b(var1, var3.east(2).south(2));

      for (int var7 = 0; var7 < 5; var7++) {
         int var8 = var2.nextInt(64);
         int var9 = var8 % 8;
         int var10 = var8 / 8;
         if (var9 == 0 || var9 == 7 || var10 == 0 || var10 == 7) {
            this.func_175933_b(var1, var3.add(-3 + var9, 0, -3 + var10));
         }
      }
   }

   private void func_175933_b(World var1, BlockPos var2) {
      for (int var6 = -2; var6 <= 2; var6++) {
         for (int var7 = -2; var7 <= 2; var7++) {
            if (Math.abs(var6) != 2 || Math.abs(var7) != 2) {
               this.func_175934_c(var1, var2.add(var6, 0, var7));
            }
         }
      }
   }

   private void func_175934_c(World var1, BlockPos var2) {
      for (int var6 = 2; var6 >= -3; var6--) {
         BlockPos var7 = var2.up(var6);
         Block var8 = var1.getBlockState(var7).getBlock();
         if (var8 == Blocks.grass || var8 == Blocks.dirt) {
            this.setBlockAndNotifyAdequately(var1, var7, field_181635_g);
            break;
         }

         if (var8.getMaterial() != Material.air && var6 < 0) {
            break;
         }
      }
   }
}
