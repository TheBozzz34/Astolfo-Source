package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenBigTree extends WorldGenAbstractTree {
   private Random rand;
   private World world;
   private BlockPos basePos = BlockPos.ORIGIN;
   public int heightLimit;
   public int height;
   public double heightAttenuation = 0.618;
   public double branchSlope = 0.381;
   public double scaleWidth = 1.0;
   public double leafDensity = 1.0;
   public int trunkSize = 1;
   public int heightLimitLimit = 12;
   public int leafDistanceLimit = 4;
   public List<WorldGenBigTree.FoliageCoordinates> field_175948_j;
   private static final long a = 21528441286757L;

   public WorldGenBigTree(boolean var1) {
      super(var1);
   }

   public void generateLeafNodeList() {
      this.height = (int)(this.heightLimit * this.heightAttenuation);
      if (this.height >= this.heightLimit) {
         this.height = this.heightLimit - 1;
      }

      int var4 = (int)(1.382 + Math.pow(this.leafDensity * this.heightLimit / 13.0, 2.0));
      if (var4 < 1) {
         var4 = 1;
      }

      int var5 = this.basePos.getY() + this.height;
      int var6 = this.heightLimit - this.leafDistanceLimit;
      this.field_175948_j = Lists.newArrayList();
      this.field_175948_j.add(new WorldGenBigTree.FoliageCoordinates(this.basePos.up(var6), var5));

      for (; var6 >= 0; var6--) {
         float var7 = this.layerSize(var6);
         if (var7 >= 0.0F) {
            for (int var8 = 0; var8 < var4; var8++) {
               double var9 = this.scaleWidth * var7 * (this.rand.nextFloat() + 0.328);
               double var11 = this.rand.nextFloat() * 2.0F * Math.PI;
               double var13 = var9 * Math.sin(var11) + 0.5;
               double var15 = var9 * Math.cos(var11) + 0.5;
               BlockPos var17 = this.basePos.add(var13, var6 - 1, var15);
               BlockPos var18 = var17.up(this.leafDistanceLimit);
               if (this.checkBlockLine(var17, var18) == -1) {
                  int var19 = this.basePos.getX() - var17.getX();
                  int var20 = this.basePos.getZ() - var17.getZ();
                  double var21 = var17.getY() - Math.sqrt(var19 * var19 + var20 * var20) * this.branchSlope;
                  int var23 = var21 > var5 ? var5 : (int)var21;
                  BlockPos var24 = new BlockPos(this.basePos.getX(), var23, this.basePos.getZ());
                  if (this.checkBlockLine(var24, var17) == -1) {
                     this.field_175948_j.add(new WorldGenBigTree.FoliageCoordinates(var17, var24.getY()));
                  }
               }
            }
         }
      }
   }

   public void func_181631_a(BlockPos var1, float var2, IBlockState var3) {
      int var7 = (int)(var2 + 0.618);

      for (int var8 = -var7; var8 <= var7; var8++) {
         for (int var9 = -var7; var9 <= var7; var9++) {
            if (Math.pow(Math.abs(var8) + 0.5, 2.0) + Math.pow(Math.abs(var9) + 0.5, 2.0) <= var2 * var2) {
               BlockPos var10 = var1.add(var8, 0, var9);
               Material var11 = this.world.getBlockState(var10).getBlock().getMaterial();
               if (var11 == Material.air || var11 == Material.leaves) {
                  this.setBlockAndNotifyAdequately(this.world, var10, var3);
               }
            }
         }
      }
   }

   public float layerSize(int var1) {
      if (var1 < this.heightLimit * 0.3F) {
         return -1.0F;
      }

      float var5 = this.heightLimit / 2.0F;
      float var6 = var5 - var1;
      float var7 = MathHelper.sqrt_float(var5 * var5 - var6 * var6);
      if (var6 == 0.0F) {
         var7 = var5;
      } else if (Math.abs(var6) >= var5) {
         return 0.0F;
      }

      return var7 * 0.5F;
   }

   public float leafSize(int var1) {
      return var1 >= 0 && var1 < this.leafDistanceLimit ? (var1 != 0 && var1 != this.leafDistanceLimit - 1 ? 3.0F : 2.0F) : -1.0F;
   }

   public void generateLeafNode(BlockPos var1) {
      for (int var5 = 0; var5 < this.leafDistanceLimit; var5++) {
         this.func_181631_a(var1.up(var5), this.leafSize(var5), Blocks.leaves.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false));
      }
   }

   public void func_175937_a(BlockPos var1, BlockPos var2, Block var3) {
      BlockPos var7 = var2.add(-var1.getX(), -var1.getY(), -var1.getZ());
      int var8 = this.getGreatestDistance(var7);
      float var9 = (float)var7.getX() / var8;
      float var10 = (float)var7.getY() / var8;
      float var11 = (float)var7.getZ() / var8;

      for (int var12 = 0; var12 <= var8; var12++) {
         BlockPos var13 = var1.add(0.5F + var12 * var9, 0.5F + var12 * var10, 0.5F + var12 * var11);
         BlockLog.EnumAxis var14 = this.func_175938_b(var1, var13);
         this.setBlockAndNotifyAdequately(this.world, var13, var3.getDefaultState().withProperty(BlockLog.LOG_AXIS, var14));
      }
   }

   private int getGreatestDistance(BlockPos var1) {
      int var5 = MathHelper.abs_int(var1.getX());
      int var6 = MathHelper.abs_int(var1.getY());
      int var7 = MathHelper.abs_int(var1.getZ());
      return var7 > var5 && var7 > var6 ? var7 : (var6 > var5 ? var6 : var5);
   }

   private BlockLog.EnumAxis func_175938_b(BlockPos var1, BlockPos var2) {
      BlockLog.EnumAxis var6 = BlockLog.EnumAxis.Y;
      int var7 = Math.abs(var2.getX() - var1.getX());
      int var8 = Math.abs(var2.getZ() - var1.getZ());
      int var9 = Math.max(var7, var8);
      if (var9 > 0) {
         if (var7 == var9) {
            var6 = BlockLog.EnumAxis.X;
         } else if (var8 == var9) {
            var6 = BlockLog.EnumAxis.Z;
         }
      }

      return var6;
   }

   public void generateLeaves() {
      for (WorldGenBigTree.FoliageCoordinates var5 : this.field_175948_j) {
         this.generateLeafNode(var5);
      }
   }

   public boolean leafNodeNeedsBase(int var1) {
      return var1 >= this.heightLimit * 0.2;
   }

   public void generateTrunk() {
      BlockPos var4 = this.basePos;
      BlockPos var5 = this.basePos.up(this.height);
      Block var6 = Blocks.log;
      this.func_175937_a(var4, var5, var6);
      if (this.trunkSize == 2) {
         this.func_175937_a(var4.east(), var5.east(), var6);
         this.func_175937_a(var4.east().south(), var5.east().south(), var6);
         this.func_175937_a(var4.south(), var5.south(), var6);
      }
   }

   public void generateLeafNodeBases() {
      for (WorldGenBigTree.FoliageCoordinates var5 : this.field_175948_j) {
         int var6 = var5.func_177999_q();
         BlockPos var7 = new BlockPos(this.basePos.getX(), var6, this.basePos.getZ());
         if (!var7.equals(var5) && this.leafNodeNeedsBase(var6 - this.basePos.getY())) {
            this.func_175937_a(var7, var5, Blocks.log);
         }
      }
   }

   public int checkBlockLine(BlockPos var1, BlockPos var2) {
      BlockPos var6 = var2.add(-var1.getX(), -var1.getY(), -var1.getZ());
      int var7 = this.getGreatestDistance(var6);
      float var8 = (float)var6.getX() / var7;
      float var9 = (float)var6.getY() / var7;
      float var10 = (float)var6.getZ() / var7;
      if (var7 == 0) {
         return -1;
      }

      for (int var11 = 0; var11 <= var7; var11++) {
         BlockPos var12 = var1.add(0.5F + var11 * var8, 0.5F + var11 * var9, 0.5F + var11 * var10);
         if (!this.func_150523_a(this.world.getBlockState(var12).getBlock())) {
            return var11;
         }
      }

      return -1;
   }

   @Override
   public void func_175904_e() {
      this.leafDistanceLimit = 5;
   }

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      this.world = var1;
      this.basePos = var3;
      this.rand = new Random(var2.nextLong());
      if (this.heightLimit == 0) {
         this.heightLimit = 5 + this.rand.nextInt(this.heightLimitLimit);
      }

      if (!this.validTreeLocation()) {
         return false;
      }

      this.generateLeafNodeList();
      this.generateLeaves();
      this.generateTrunk();
      this.generateLeafNodeBases();
      return true;
   }

   private boolean validTreeLocation() {
      Block var4 = this.world.getBlockState(this.basePos.down()).getBlock();
      if (var4 != Blocks.dirt && var4 != Blocks.grass && var4 != Blocks.farmland) {
         return false;
      }

      int var5 = this.checkBlockLine(this.basePos, this.basePos.up(this.heightLimit - 1));
      if (var5 == -1) {
         return true;
      }

      if (var5 < 6) {
         return false;
      }

      this.heightLimit = var5;
      return true;
   }

   public static class FoliageCoordinates extends BlockPos {
      private final int field_178000_b;

      public FoliageCoordinates(BlockPos var1, int var2) {
         super(var1.getX(), var1.getY(), var1.getZ());
         this.field_178000_b = var2;
      }

      public int func_177999_q() {
         return this.field_178000_b;
      }
   }
}
