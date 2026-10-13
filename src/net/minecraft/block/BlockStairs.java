package net.minecraft.block;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairs extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
   public static final PropertyEnum<BlockStairs.EnumHalf> HALF = PropertyEnum.create("half", BlockStairs.EnumHalf.class);
   public static final PropertyEnum<BlockStairs.EnumShape> SHAPE = PropertyEnum.create("shape", BlockStairs.EnumShape.class);
   private static final int[][] field_150150_a = new int[][]{{4, 5}, {5, 7}, {6, 7}, {4, 6}, {0, 1}, {1, 3}, {2, 3}, {0, 2}};
   private final Block modelBlock;
   private final IBlockState modelState;
   private boolean hasRaytraced;
   private int rayTracePass;
   private static final long K = 60437777466443L;

   public BlockStairs(IBlockState var1) {
      super(var1.getBlock().blockMaterial);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(HALF, BlockStairs.EnumHalf.BOTTOM)
            .withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT)
      );
      this.modelBlock = var1.getBlock();
      this.modelState = var1;
      this.setHardness(this.modelBlock.blockHardness);
      this.setResistance(this.modelBlock.blockResistance / 3.0F);
      this.setStepSound(this.modelBlock.stepSound);
      this.setLightOpacity(255);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      if (this.hasRaytraced) {
         this.setBlockBounds(
            0.5F * (this.rayTracePass % 2),
            0.5F * (this.rayTracePass / 4 % 2),
            0.5F * (this.rayTracePass / 2 % 2),
            0.5F + 0.5F * (this.rayTracePass % 2),
            0.5F + 0.5F * (this.rayTracePass / 4 % 2),
            0.5F + 0.5F * (this.rayTracePass / 2 % 2)
         );
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   public void setBaseCollisionBounds(IBlockAccess var1, BlockPos var2) {
      if (var1.getBlockState(var2).getValue(HALF) == BlockStairs.EnumHalf.TOP) {
         this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }
   }

   public static boolean isBlockStairs(Block var0) {
      return var0 instanceof BlockStairs;
   }

   public static boolean isSameStair(IBlockAccess var0, BlockPos var1, IBlockState var2) {
      IBlockState var6 = var0.getBlockState(var1);
      Block var7 = var6.getBlock();
      return isBlockStairs(var7) && var6.getValue(HALF) == var2.getValue(HALF) && var6.getValue(FACING) == var2.getValue(FACING);
   }

   public int func_176307_f(IBlockAccess var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      EnumFacing var7 = var6.getValue(FACING);
      BlockStairs.EnumHalf var8 = var6.getValue(HALF);
      boolean var9 = var8 == BlockStairs.EnumHalf.TOP;
      if (var7 == EnumFacing.EAST) {
         IBlockState var10 = var1.getBlockState(var2.east());
         Block var11 = var10.getBlock();
         if (isBlockStairs(var11) && var8 == var10.getValue(HALF)) {
            EnumFacing var12 = var10.getValue(FACING);
            if (var12 == EnumFacing.NORTH && !isSameStair(var1, var2.south(), var6)) {
               return var9 ? 1 : 2;
            }

            if (var12 == EnumFacing.SOUTH && !isSameStair(var1, var2.north(), var6)) {
               return var9 ? 2 : 1;
            }
         }
      } else if (var7 == EnumFacing.WEST) {
         IBlockState var13 = var1.getBlockState(var2.west());
         Block var16 = var13.getBlock();
         if (isBlockStairs(var16) && var8 == var13.getValue(HALF)) {
            EnumFacing var19 = var13.getValue(FACING);
            if (var19 == EnumFacing.NORTH && !isSameStair(var1, var2.south(), var6)) {
               return var9 ? 2 : 1;
            }

            if (var19 == EnumFacing.SOUTH && !isSameStair(var1, var2.north(), var6)) {
               return var9 ? 1 : 2;
            }
         }
      } else if (var7 == EnumFacing.SOUTH) {
         IBlockState var14 = var1.getBlockState(var2.south());
         Block var17 = var14.getBlock();
         if (isBlockStairs(var17) && var8 == var14.getValue(HALF)) {
            EnumFacing var20 = var14.getValue(FACING);
            if (var20 == EnumFacing.WEST && !isSameStair(var1, var2.east(), var6)) {
               return var9 ? 2 : 1;
            }

            if (var20 == EnumFacing.EAST && !isSameStair(var1, var2.west(), var6)) {
               return var9 ? 1 : 2;
            }
         }
      } else if (var7 == EnumFacing.NORTH) {
         IBlockState var15 = var1.getBlockState(var2.north());
         Block var18 = var15.getBlock();
         if (isBlockStairs(var18) && var8 == var15.getValue(HALF)) {
            EnumFacing var21 = var15.getValue(FACING);
            if (var21 == EnumFacing.WEST && !isSameStair(var1, var2.east(), var6)) {
               return var9 ? 1 : 2;
            }

            if (var21 == EnumFacing.EAST && !isSameStair(var1, var2.west(), var6)) {
               return var9 ? 2 : 1;
            }
         }
      }

      return 0;
   }

   public int func_176305_g(IBlockAccess var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      EnumFacing var7 = var6.getValue(FACING);
      BlockStairs.EnumHalf var8 = var6.getValue(HALF);
      boolean var9 = var8 == BlockStairs.EnumHalf.TOP;
      if (var7 == EnumFacing.EAST) {
         IBlockState var10 = var1.getBlockState(var2.west());
         Block var11 = var10.getBlock();
         if (isBlockStairs(var11) && var8 == var10.getValue(HALF)) {
            EnumFacing var12 = var10.getValue(FACING);
            if (var12 == EnumFacing.NORTH && !isSameStair(var1, var2.north(), var6)) {
               return var9 ? 1 : 2;
            }

            if (var12 == EnumFacing.SOUTH && !isSameStair(var1, var2.south(), var6)) {
               return var9 ? 2 : 1;
            }
         }
      } else if (var7 == EnumFacing.WEST) {
         IBlockState var13 = var1.getBlockState(var2.east());
         Block var16 = var13.getBlock();
         if (isBlockStairs(var16) && var8 == var13.getValue(HALF)) {
            EnumFacing var19 = var13.getValue(FACING);
            if (var19 == EnumFacing.NORTH && !isSameStair(var1, var2.north(), var6)) {
               return var9 ? 2 : 1;
            }

            if (var19 == EnumFacing.SOUTH && !isSameStair(var1, var2.south(), var6)) {
               return var9 ? 1 : 2;
            }
         }
      } else if (var7 == EnumFacing.SOUTH) {
         IBlockState var14 = var1.getBlockState(var2.north());
         Block var17 = var14.getBlock();
         if (isBlockStairs(var17) && var8 == var14.getValue(HALF)) {
            EnumFacing var20 = var14.getValue(FACING);
            if (var20 == EnumFacing.WEST && !isSameStair(var1, var2.west(), var6)) {
               return var9 ? 2 : 1;
            }

            if (var20 == EnumFacing.EAST && !isSameStair(var1, var2.east(), var6)) {
               return var9 ? 1 : 2;
            }
         }
      } else if (var7 == EnumFacing.NORTH) {
         IBlockState var15 = var1.getBlockState(var2.south());
         Block var18 = var15.getBlock();
         if (isBlockStairs(var18) && var8 == var15.getValue(HALF)) {
            EnumFacing var21 = var15.getValue(FACING);
            if (var21 == EnumFacing.WEST && !isSameStair(var1, var2.west(), var6)) {
               return var9 ? 1 : 2;
            }

            if (var21 == EnumFacing.EAST && !isSameStair(var1, var2.east(), var6)) {
               return var9 ? 2 : 1;
            }
         }
      }

      return 0;
   }

   public boolean func_176306_h(IBlockAccess var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      EnumFacing var7 = var6.getValue(FACING);
      BlockStairs.EnumHalf var8 = var6.getValue(HALF);
      boolean var9 = var8 == BlockStairs.EnumHalf.TOP;
      float var10 = 0.5F;
      float var11 = 1.0F;
      if (var9) {
         var10 = 0.0F;
         var11 = 0.5F;
      }

      float var12 = 0.0F;
      float var13 = 1.0F;
      float var14 = 0.0F;
      float var15 = 0.5F;
      boolean var16 = true;
      if (var7 == EnumFacing.EAST) {
         var12 = 0.5F;
         var15 = 1.0F;
         IBlockState var17 = var1.getBlockState(var2.east());
         Block var18 = var17.getBlock();
         if (isBlockStairs(var18) && var8 == var17.getValue(HALF)) {
            EnumFacing var19 = var17.getValue(FACING);
            if (var19 == EnumFacing.NORTH && !isSameStair(var1, var2.south(), var6)) {
               var15 = 0.5F;
               var16 = false;
            } else if (var19 == EnumFacing.SOUTH && !isSameStair(var1, var2.north(), var6)) {
               var14 = 0.5F;
               var16 = false;
            }
         }
      } else if (var7 == EnumFacing.WEST) {
         var13 = 0.5F;
         var15 = 1.0F;
         IBlockState var20 = var1.getBlockState(var2.west());
         Block var23 = var20.getBlock();
         if (isBlockStairs(var23) && var8 == var20.getValue(HALF)) {
            EnumFacing var26 = var20.getValue(FACING);
            if (var26 == EnumFacing.NORTH && !isSameStair(var1, var2.south(), var6)) {
               var15 = 0.5F;
               var16 = false;
            } else if (var26 == EnumFacing.SOUTH && !isSameStair(var1, var2.north(), var6)) {
               var14 = 0.5F;
               var16 = false;
            }
         }
      } else if (var7 == EnumFacing.SOUTH) {
         var14 = 0.5F;
         var15 = 1.0F;
         IBlockState var21 = var1.getBlockState(var2.south());
         Block var24 = var21.getBlock();
         if (isBlockStairs(var24) && var8 == var21.getValue(HALF)) {
            EnumFacing var27 = var21.getValue(FACING);
            if (var27 == EnumFacing.WEST && !isSameStair(var1, var2.east(), var6)) {
               var13 = 0.5F;
               var16 = false;
            } else if (var27 == EnumFacing.EAST && !isSameStair(var1, var2.west(), var6)) {
               var12 = 0.5F;
               var16 = false;
            }
         }
      } else if (var7 == EnumFacing.NORTH) {
         IBlockState var22 = var1.getBlockState(var2.north());
         Block var25 = var22.getBlock();
         if (isBlockStairs(var25) && var8 == var22.getValue(HALF)) {
            EnumFacing var28 = var22.getValue(FACING);
            if (var28 == EnumFacing.WEST && !isSameStair(var1, var2.east(), var6)) {
               var13 = 0.5F;
               var16 = false;
            } else if (var28 == EnumFacing.EAST && !isSameStair(var1, var2.west(), var6)) {
               var12 = 0.5F;
               var16 = false;
            }
         }
      }

      this.setBlockBounds(var12, var10, var14, var13, var11, var15);
      return var16;
   }

   public boolean func_176304_i(IBlockAccess var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      EnumFacing var7 = var6.getValue(FACING);
      BlockStairs.EnumHalf var8 = var6.getValue(HALF);
      boolean var9 = var8 == BlockStairs.EnumHalf.TOP;
      float var10 = 0.5F;
      float var11 = 1.0F;
      if (var9) {
         var10 = 0.0F;
         var11 = 0.5F;
      }

      float var12 = 0.0F;
      float var13 = 0.5F;
      float var14 = 0.5F;
      float var15 = 1.0F;
      boolean var16 = false;
      if (var7 == EnumFacing.EAST) {
         IBlockState var17 = var1.getBlockState(var2.west());
         Block var18 = var17.getBlock();
         if (isBlockStairs(var18) && var8 == var17.getValue(HALF)) {
            EnumFacing var19 = var17.getValue(FACING);
            if (var19 == EnumFacing.NORTH && !isSameStair(var1, var2.north(), var6)) {
               var14 = 0.0F;
               var15 = 0.5F;
               var16 = true;
            } else if (var19 == EnumFacing.SOUTH && !isSameStair(var1, var2.south(), var6)) {
               var14 = 0.5F;
               var15 = 1.0F;
               var16 = true;
            }
         }
      } else if (var7 == EnumFacing.WEST) {
         IBlockState var20 = var1.getBlockState(var2.east());
         Block var23 = var20.getBlock();
         if (isBlockStairs(var23) && var8 == var20.getValue(HALF)) {
            var12 = 0.5F;
            var13 = 1.0F;
            EnumFacing var26 = var20.getValue(FACING);
            if (var26 == EnumFacing.NORTH && !isSameStair(var1, var2.north(), var6)) {
               var14 = 0.0F;
               var15 = 0.5F;
               var16 = true;
            } else if (var26 == EnumFacing.SOUTH && !isSameStair(var1, var2.south(), var6)) {
               var14 = 0.5F;
               var15 = 1.0F;
               var16 = true;
            }
         }
      } else if (var7 == EnumFacing.SOUTH) {
         IBlockState var21 = var1.getBlockState(var2.north());
         Block var24 = var21.getBlock();
         if (isBlockStairs(var24) && var8 == var21.getValue(HALF)) {
            var14 = 0.0F;
            var15 = 0.5F;
            EnumFacing var27 = var21.getValue(FACING);
            if (var27 == EnumFacing.WEST && !isSameStair(var1, var2.west(), var6)) {
               var16 = true;
            } else if (var27 == EnumFacing.EAST && !isSameStair(var1, var2.east(), var6)) {
               var12 = 0.5F;
               var13 = 1.0F;
               var16 = true;
            }
         }
      } else if (var7 == EnumFacing.NORTH) {
         IBlockState var22 = var1.getBlockState(var2.south());
         Block var25 = var22.getBlock();
         if (isBlockStairs(var25) && var8 == var22.getValue(HALF)) {
            EnumFacing var28 = var22.getValue(FACING);
            if (var28 == EnumFacing.WEST && !isSameStair(var1, var2.west(), var6)) {
               var16 = true;
            } else if (var28 == EnumFacing.EAST && !isSameStair(var1, var2.east(), var6)) {
               var12 = 0.5F;
               var13 = 1.0F;
               var16 = true;
            }
         }
      }

      if (var16) {
         this.setBlockBounds(var12, var10, var14, var13, var11, var15);
      }

      return var16;
   }

   @Override
   public void addCollisionBoxesToList(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.setBaseCollisionBounds(var1, var2);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      boolean var10 = this.func_176306_h(var1, var2);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      if (var10 && this.func_176304_i(var1, var2)) {
         super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      }

      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      this.modelBlock.randomDisplayTick(var1, var2, var3, var4);
   }

   @Override
   public void onBlockClicked(World var1, BlockPos var2, EntityPlayer var3) {
      this.modelBlock.onBlockClicked(var1, var2, var3);
   }

   @Override
   public void onBlockDestroyedByPlayer(World var1, BlockPos var2, IBlockState var3) {
      this.modelBlock.onBlockDestroyedByPlayer(var1, var2, var3);
   }

   @Override
   public int getMixedBrightnessForBlock(IBlockAccess var1, BlockPos var2) {
      return this.modelBlock.getMixedBrightnessForBlock(var1, var2);
   }

   @Override
   public float getExplosionResistance(Entity var1) {
      return this.modelBlock.getExplosionResistance(var1);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return this.modelBlock.getBlockLayer();
   }

   @Override
   public int tickRate(World var1) {
      return this.modelBlock.tickRate(var1);
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World var1, BlockPos var2) {
      return this.modelBlock.getSelectedBoundingBox(var1, var2);
   }

   @Override
   public Vec3 modifyAcceleration(World var1, BlockPos var2, Entity var3, Vec3 var4) {
      return this.modelBlock.modifyAcceleration(var1, var2, var3, var4);
   }

   @Override
   public boolean isCollidable() {
      return this.modelBlock.isCollidable();
   }

   @Override
   public boolean canCollideCheck(IBlockState var1, boolean var2) {
      return this.modelBlock.canCollideCheck(var1, var2);
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return this.modelBlock.canPlaceBlockAt(var1, var2);
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      this.onNeighborBlockChange(var1, var2, this.modelState, Blocks.air);
      this.modelBlock.onBlockAdded(var1, var2, this.modelState);
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      this.modelBlock.breakBlock(var1, var2, this.modelState);
   }

   @Override
   public void onEntityCollidedWithBlock(World var1, BlockPos var2, Entity var3) {
      this.modelBlock.onEntityCollidedWithBlock(var1, var2, var3);
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      this.modelBlock.updateTick(var1, var2, var3, var4);
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      return this.modelBlock.onBlockActivated(var1, var2, this.modelState, var4, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
   }

   @Override
   public void onBlockDestroyedByExplosion(World var1, BlockPos var2, Explosion var3) {
      this.modelBlock.onBlockDestroyedByExplosion(var1, var2, var3);
   }

   @Override
   public MapColor getMapColor(IBlockState var1) {
      return this.modelBlock.getMapColor(this.modelState);
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      IBlockState var12 = super.onBlockPlaced(var1, var2, var3, var4, var5, var6, var7, var8);
      var12 = var12.withProperty(FACING, var8.getHorizontalFacing()).withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
      return var3 == EnumFacing.DOWN || var3 != EnumFacing.UP && !(var5 <= 0.5)
         ? var12.withProperty(HALF, BlockStairs.EnumHalf.TOP)
         : var12.withProperty(HALF, BlockStairs.EnumHalf.BOTTOM);
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      MovingObjectPosition[] var8 = new MovingObjectPosition[8];
      IBlockState var9 = var1.getBlockState(var2);
      int var10 = var9.getValue(FACING).getHorizontalIndex();
      boolean var11 = var9.getValue(HALF) == BlockStairs.EnumHalf.TOP;
      int[] var12 = field_150150_a[var10 + (var11 ? 4 : 0)];
      this.hasRaytraced = true;

      for (int var13 = 0; var13 < 8; var13++) {
         this.rayTracePass = var13;
         if (Arrays.binarySearch(var12, var13) < 0) {
            var8[var13] = super.collisionRayTrace(var1, var2, var3, var4);
         }
      }

      for (int var16 : var12) {
         var8[var16] = null;
      }

      MovingObjectPosition var23 = null;
      double var24 = 0.0;

      for (MovingObjectPosition var19 : var8) {
         if (var19 != null) {
            double var20 = var19.hitVec.squareDistanceTo(var4);
            if (var20 > var24) {
               var23 = var19;
               var24 = var20;
            }
         }
      }

      return var23;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      IBlockState var4 = this.getDefaultState().withProperty(HALF, (var1 & 4) > 0 ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM);
      return var4.withProperty(FACING, EnumFacing.getFront(5 - (var1 & 3)));
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      byte var5 = 0;
      if (var1.getValue(HALF) == BlockStairs.EnumHalf.TOP) {
         var5 |= 4;
      }

      return var5 | 5 - var1.getValue(FACING).getIndex();
   }

   @Override
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      if (this.func_176306_h(var2, var3)) {
         switch (this.func_176305_g(var2, var3)) {
            case 0:
               var1 = var1.withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
               break;
            case 1:
               var1 = var1.withProperty(SHAPE, BlockStairs.EnumShape.INNER_RIGHT);
               break;
            case 2:
               var1 = var1.withProperty(SHAPE, BlockStairs.EnumShape.INNER_LEFT);
         }
      } else {
         switch (this.func_176307_f(var2, var3)) {
            case 0:
               var1 = var1.withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
               break;
            case 1:
               var1 = var1.withProperty(SHAPE, BlockStairs.EnumShape.OUTER_RIGHT);
               break;
            case 2:
               var1 = var1.withProperty(SHAPE, BlockStairs.EnumShape.OUTER_LEFT);
         }
      }

      return var1;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, HALF, SHAPE);
   }

   static {
      long var20 = K ^ 110353765561930L;
      long var10000 = 5591898254320251588L ^ var20;
   }

   public enum EnumHalf implements IStringSerializable {
      TOP("top"),
      BOTTOM("bottom");

      private final String name;

      EnumHalf(String var3) {
         this.name = var3;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @Override
      public String getName() {
         return this.name;
      }
   }

   public enum EnumShape implements IStringSerializable {
      STRAIGHT("straight"),
      INNER_LEFT("inner_left"),
      INNER_RIGHT("inner_right"),
      OUTER_LEFT("outer_left"),
      OUTER_RIGHT("outer_right");

      private final String name;

      EnumShape(String var3) {
         this.name = var3;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
