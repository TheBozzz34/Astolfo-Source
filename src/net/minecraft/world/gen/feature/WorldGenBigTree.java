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

   public WorldGenBigTree(boolean bl) {
      super(bl);
   }

   public void generateLeafNodeList() {
      this.height = (int)(this.heightLimit * this.heightAttenuation);
      if (this.height >= this.heightLimit) {
         this.height = this.heightLimit - 1;
      }

      int i = (int)(1.382 + Math.pow(this.leafDensity * this.heightLimit / 13.0, 2.0));
      if (i < 1) {
         i = 1;
      }

      int j = this.basePos.getY() + this.height;
      int k = this.heightLimit - this.leafDistanceLimit;
      this.field_175948_j = Lists.newArrayList();
      this.field_175948_j.add(new WorldGenBigTree.FoliageCoordinates(this.basePos.up(k), j));

      for (; k >= 0; k--) {
         float f = this.layerSize(k);
         if (f >= 0.0F) {
            for (int l = 0; l < i; l++) {
               double d = this.scaleWidth * f * (this.rand.nextFloat() + 0.328);
               double e = this.rand.nextFloat() * 2.0F * Math.PI;
               double d2 = d * Math.sin(e) + 0.5;
               double d3 = d * Math.cos(e) + 0.5;
               BlockPos blockPos = this.basePos.add(d2, k - 1, d3);
               BlockPos blockPos2 = blockPos.up(this.leafDistanceLimit);
               if (this.checkBlockLine(blockPos, blockPos2) == -1) {
                  int m = this.basePos.getX() - blockPos.getX();
                  int n = this.basePos.getZ() - blockPos.getZ();
                  double d4 = blockPos.getY() - Math.sqrt(m * m + n * n) * this.branchSlope;
                  int i2 = d4 > j ? j : (int)d4;
                  BlockPos blockPos3 = new BlockPos(this.basePos.getX(), i2, this.basePos.getZ());
                  if (this.checkBlockLine(blockPos3, blockPos) == -1) {
                     this.field_175948_j.add(new WorldGenBigTree.FoliageCoordinates(blockPos, blockPos3.getY()));
                  }
               }
            }
         }
      }
   }

   public void func_181631_a(BlockPos blockPos, float f, IBlockState iBlockState) {
      int i = (int)(f + 0.618);

      for (int j = -i; j <= i; j++) {
         for (int k = -i; k <= i; k++) {
            if (Math.pow(Math.abs(j) + 0.5, 2.0) + Math.pow(Math.abs(k) + 0.5, 2.0) <= f * f) {
               BlockPos blockPos2 = blockPos.add(j, 0, k);
               Material material = this.world.getBlockState(blockPos2).getBlock().getMaterial();
               if (material == Material.air || material == Material.leaves) {
                  this.setBlockAndNotifyAdequately(this.world, blockPos2, iBlockState);
               }
            }
         }
      }
   }

   public float layerSize(int i) {
      if (i < this.heightLimit * 0.3F) {
         return -1.0F;
      }

      float f = this.heightLimit / 2.0F;
      float g = f - i;
      float h = MathHelper.sqrt_float(f * f - g * g);
      if (g == 0.0F) {
         h = f;
      } else if (Math.abs(g) >= f) {
         return 0.0F;
      }

      return h * 0.5F;
   }

   public float leafSize(int i) {
      return i >= 0 && i < this.leafDistanceLimit ? (i != 0 && i != this.leafDistanceLimit - 1 ? 3.0F : 2.0F) : -1.0F;
   }

   public void generateLeafNode(BlockPos pos) {
      for (int i = 0; i < this.leafDistanceLimit; i++) {
         this.func_181631_a(pos.up(i), this.leafSize(i), Blocks.leaves.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false));
      }
   }

   public void func_175937_a(BlockPos blockPos, BlockPos blockPos2, Block block) {
      BlockPos blockPos3 = blockPos2.add(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
      int i = this.getGreatestDistance(blockPos3);
      float f = (float)blockPos3.getX() / i;
      float g = (float)blockPos3.getY() / i;
      float h = (float)blockPos3.getZ() / i;

      for (int j = 0; j <= i; j++) {
         BlockPos blockPos4 = blockPos.add(0.5F + j * f, 0.5F + j * g, 0.5F + j * h);
         BlockLog.EnumAxis enumAxis = this.func_175938_b(blockPos, blockPos4);
         this.setBlockAndNotifyAdequately(this.world, blockPos4, block.getDefaultState().withProperty(BlockLog.LOG_AXIS, enumAxis));
      }
   }

   private int getGreatestDistance(BlockPos posIn) {
      int i = MathHelper.abs_int(posIn.getX());
      int j = MathHelper.abs_int(posIn.getY());
      int k = MathHelper.abs_int(posIn.getZ());
      return k > i && k > j ? k : (j > i ? j : i);
   }

   private BlockLog.EnumAxis func_175938_b(BlockPos blockPos, BlockPos blockPos2) {
      BlockLog.EnumAxis enumAxis = BlockLog.EnumAxis.Y;
      int i = Math.abs(blockPos2.getX() - blockPos.getX());
      int j = Math.abs(blockPos2.getZ() - blockPos.getZ());
      int k = Math.max(i, j);
      if (k > 0) {
         if (i == k) {
            enumAxis = BlockLog.EnumAxis.X;
         } else if (j == k) {
            enumAxis = BlockLog.EnumAxis.Z;
         }
      }

      return enumAxis;
   }

   public void generateLeaves() {
      for (WorldGenBigTree.FoliageCoordinates foliageCoordinates : this.field_175948_j) {
         this.generateLeafNode(foliageCoordinates);
      }
   }

   public boolean leafNodeNeedsBase(int i) {
      return i >= this.heightLimit * 0.2;
   }

   public void generateTrunk() {
      BlockPos blockPos = this.basePos;
      BlockPos blockPos2 = this.basePos.up(this.height);
      Block block = Blocks.log;
      this.func_175937_a(blockPos, blockPos2, block);
      if (this.trunkSize == 2) {
         this.func_175937_a(blockPos.east(), blockPos2.east(), block);
         this.func_175937_a(blockPos.east().south(), blockPos2.east().south(), block);
         this.func_175937_a(blockPos.south(), blockPos2.south(), block);
      }
   }

   public void generateLeafNodeBases() {
      for (WorldGenBigTree.FoliageCoordinates foliageCoordinates : this.field_175948_j) {
         int i = foliageCoordinates.func_177999_q();
         BlockPos blockPos = new BlockPos(this.basePos.getX(), i, this.basePos.getZ());
         if (!blockPos.equals(foliageCoordinates) && this.leafNodeNeedsBase(i - this.basePos.getY())) {
            this.func_175937_a(blockPos, foliageCoordinates, Blocks.log);
         }
      }
   }

   public int checkBlockLine(BlockPos posOne, BlockPos posTwo) {
      BlockPos blockPos = posTwo.add(-posOne.getX(), -posOne.getY(), -posOne.getZ());
      int i = this.getGreatestDistance(blockPos);
      float f = (float)blockPos.getX() / i;
      float g = (float)blockPos.getY() / i;
      float h = (float)blockPos.getZ() / i;
      if (i == 0) {
         return -1;
      }

      for (int j = 0; j <= i; j++) {
         BlockPos blockPos2 = posOne.add(0.5F + j * f, 0.5F + j * g, 0.5F + j * h);
         if (!this.func_150523_a(this.world.getBlockState(blockPos2).getBlock())) {
            return j;
         }
      }

      return -1;
   }

   @Override
   public void func_175904_e() {
      this.leafDistanceLimit = 5;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      this.world = worldIn;
      this.basePos = position;
      this.rand = new Random(rand.nextLong());
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
      Block block = this.world.getBlockState(this.basePos.down()).getBlock();
      if (block != Blocks.dirt && block != Blocks.grass && block != Blocks.farmland) {
         return false;
      }

      int i = this.checkBlockLine(this.basePos, this.basePos.up(this.heightLimit - 1));
      if (i == -1) {
         return true;
      }

      if (i < 6) {
         return false;
      }

      this.heightLimit = i;
      return true;
   }

   public static class FoliageCoordinates extends BlockPos {
      private final int field_178000_b;

      public FoliageCoordinates(BlockPos blockPos, int i) {
         super(blockPos.getX(), blockPos.getY(), blockPos.getZ());
         this.field_178000_b = i;
      }

      public int func_177999_q() {
         return this.field_178000_b;
      }
   }
}
