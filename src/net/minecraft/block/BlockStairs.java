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

   public BlockStairs(IBlockState modelState) {
      super(modelState.getBlock().blockMaterial);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(HALF, BlockStairs.EnumHalf.BOTTOM)
            .withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT)
      );
      this.modelBlock = modelState.getBlock();
      this.modelState = modelState;
      this.setHardness(this.modelBlock.blockHardness);
      this.setResistance(this.modelBlock.blockResistance / 3.0F);
      this.setStepSound(this.modelBlock.stepSound);
      this.setLightOpacity(255);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
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

   public void setBaseCollisionBounds(IBlockAccess worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getValue(HALF) == BlockStairs.EnumHalf.TOP) {
         this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }
   }

   public static boolean isBlockStairs(Block blockIn) {
      return blockIn instanceof BlockStairs;
   }

   public static boolean isSameStair(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      IBlockState iBlockState = worldIn.getBlockState(pos);
      Block block = iBlockState.getBlock();
      return isBlockStairs(block) && iBlockState.getValue(HALF) == state.getValue(HALF) && iBlockState.getValue(FACING) == state.getValue(FACING);
   }

   public int func_176307_f(IBlockAccess blockAccess, BlockPos pos) {
      IBlockState iBlockState = blockAccess.getBlockState(pos);
      EnumFacing enumFacing = iBlockState.getValue(FACING);
      BlockStairs.EnumHalf enumHalf = iBlockState.getValue(HALF);
      boolean bl = enumHalf == BlockStairs.EnumHalf.TOP;
      if (enumFacing == EnumFacing.EAST) {
         IBlockState iBlockState2 = blockAccess.getBlockState(pos.east());
         Block block = iBlockState2.getBlock();
         if (isBlockStairs(block) && enumHalf == iBlockState2.getValue(HALF)) {
            EnumFacing enumFacing2 = iBlockState2.getValue(FACING);
            if (enumFacing2 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iBlockState)) {
               return bl ? 1 : 2;
            }

            if (enumFacing2 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iBlockState)) {
               return bl ? 2 : 1;
            }
         }
      } else if (enumFacing == EnumFacing.WEST) {
         IBlockState iBlockState3 = blockAccess.getBlockState(pos.west());
         Block block2 = iBlockState3.getBlock();
         if (isBlockStairs(block2) && enumHalf == iBlockState3.getValue(HALF)) {
            EnumFacing enumFacing3 = iBlockState3.getValue(FACING);
            if (enumFacing3 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iBlockState)) {
               return bl ? 2 : 1;
            }

            if (enumFacing3 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iBlockState)) {
               return bl ? 1 : 2;
            }
         }
      } else if (enumFacing == EnumFacing.SOUTH) {
         IBlockState iBlockState4 = blockAccess.getBlockState(pos.south());
         Block block3 = iBlockState4.getBlock();
         if (isBlockStairs(block3) && enumHalf == iBlockState4.getValue(HALF)) {
            EnumFacing enumFacing4 = iBlockState4.getValue(FACING);
            if (enumFacing4 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iBlockState)) {
               return bl ? 2 : 1;
            }

            if (enumFacing4 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iBlockState)) {
               return bl ? 1 : 2;
            }
         }
      } else if (enumFacing == EnumFacing.NORTH) {
         IBlockState iBlockState5 = blockAccess.getBlockState(pos.north());
         Block block4 = iBlockState5.getBlock();
         if (isBlockStairs(block4) && enumHalf == iBlockState5.getValue(HALF)) {
            EnumFacing enumFacing5 = iBlockState5.getValue(FACING);
            if (enumFacing5 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iBlockState)) {
               return bl ? 1 : 2;
            }

            if (enumFacing5 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iBlockState)) {
               return bl ? 2 : 1;
            }
         }
      }

      return 0;
   }

   public int func_176305_g(IBlockAccess blockAccess, BlockPos pos) {
      IBlockState iBlockState = blockAccess.getBlockState(pos);
      EnumFacing enumFacing = iBlockState.getValue(FACING);
      BlockStairs.EnumHalf enumHalf = iBlockState.getValue(HALF);
      boolean bl = enumHalf == BlockStairs.EnumHalf.TOP;
      if (enumFacing == EnumFacing.EAST) {
         IBlockState iBlockState2 = blockAccess.getBlockState(pos.west());
         Block block = iBlockState2.getBlock();
         if (isBlockStairs(block) && enumHalf == iBlockState2.getValue(HALF)) {
            EnumFacing enumFacing2 = iBlockState2.getValue(FACING);
            if (enumFacing2 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), iBlockState)) {
               return bl ? 1 : 2;
            }

            if (enumFacing2 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), iBlockState)) {
               return bl ? 2 : 1;
            }
         }
      } else if (enumFacing == EnumFacing.WEST) {
         IBlockState iBlockState3 = blockAccess.getBlockState(pos.east());
         Block block2 = iBlockState3.getBlock();
         if (isBlockStairs(block2) && enumHalf == iBlockState3.getValue(HALF)) {
            EnumFacing enumFacing3 = iBlockState3.getValue(FACING);
            if (enumFacing3 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), iBlockState)) {
               return bl ? 2 : 1;
            }

            if (enumFacing3 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), iBlockState)) {
               return bl ? 1 : 2;
            }
         }
      } else if (enumFacing == EnumFacing.SOUTH) {
         IBlockState iBlockState4 = blockAccess.getBlockState(pos.north());
         Block block3 = iBlockState4.getBlock();
         if (isBlockStairs(block3) && enumHalf == iBlockState4.getValue(HALF)) {
            EnumFacing enumFacing4 = iBlockState4.getValue(FACING);
            if (enumFacing4 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), iBlockState)) {
               return bl ? 2 : 1;
            }

            if (enumFacing4 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), iBlockState)) {
               return bl ? 1 : 2;
            }
         }
      } else if (enumFacing == EnumFacing.NORTH) {
         IBlockState iBlockState5 = blockAccess.getBlockState(pos.south());
         Block block4 = iBlockState5.getBlock();
         if (isBlockStairs(block4) && enumHalf == iBlockState5.getValue(HALF)) {
            EnumFacing enumFacing5 = iBlockState5.getValue(FACING);
            if (enumFacing5 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), iBlockState)) {
               return bl ? 1 : 2;
            }

            if (enumFacing5 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), iBlockState)) {
               return bl ? 2 : 1;
            }
         }
      }

      return 0;
   }

   public boolean func_176306_h(IBlockAccess blockAccess, BlockPos pos) {
      IBlockState iBlockState = blockAccess.getBlockState(pos);
      EnumFacing enumFacing = iBlockState.getValue(FACING);
      BlockStairs.EnumHalf enumHalf = iBlockState.getValue(HALF);
      boolean bl = enumHalf == BlockStairs.EnumHalf.TOP;
      float f = 0.5F;
      float g = 1.0F;
      if (bl) {
         f = 0.0F;
         g = 0.5F;
      }

      float h = 0.0F;
      float f2 = 1.0F;
      float f3 = 0.0F;
      float f4 = 0.5F;
      boolean bl2 = true;
      if (enumFacing == EnumFacing.EAST) {
         h = 0.5F;
         f4 = 1.0F;
         IBlockState iBlockState2 = blockAccess.getBlockState(pos.east());
         Block block = iBlockState2.getBlock();
         if (isBlockStairs(block) && enumHalf == iBlockState2.getValue(HALF)) {
            EnumFacing enumFacing2 = iBlockState2.getValue(FACING);
            if (enumFacing2 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iBlockState)) {
               f4 = 0.5F;
               bl2 = false;
            } else if (enumFacing2 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iBlockState)) {
               f3 = 0.5F;
               bl2 = false;
            }
         }
      } else if (enumFacing == EnumFacing.WEST) {
         f2 = 0.5F;
         f4 = 1.0F;
         IBlockState iBlockState3 = blockAccess.getBlockState(pos.west());
         Block block2 = iBlockState3.getBlock();
         if (isBlockStairs(block2) && enumHalf == iBlockState3.getValue(HALF)) {
            EnumFacing enumFacing3 = iBlockState3.getValue(FACING);
            if (enumFacing3 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iBlockState)) {
               f4 = 0.5F;
               bl2 = false;
            } else if (enumFacing3 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iBlockState)) {
               f3 = 0.5F;
               bl2 = false;
            }
         }
      } else if (enumFacing == EnumFacing.SOUTH) {
         f3 = 0.5F;
         f4 = 1.0F;
         IBlockState iBlockState4 = blockAccess.getBlockState(pos.south());
         Block block3 = iBlockState4.getBlock();
         if (isBlockStairs(block3) && enumHalf == iBlockState4.getValue(HALF)) {
            EnumFacing enumFacing4 = iBlockState4.getValue(FACING);
            if (enumFacing4 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iBlockState)) {
               f2 = 0.5F;
               bl2 = false;
            } else if (enumFacing4 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iBlockState)) {
               h = 0.5F;
               bl2 = false;
            }
         }
      } else if (enumFacing == EnumFacing.NORTH) {
         IBlockState iBlockState5 = blockAccess.getBlockState(pos.north());
         Block block4 = iBlockState5.getBlock();
         if (isBlockStairs(block4) && enumHalf == iBlockState5.getValue(HALF)) {
            EnumFacing enumFacing5 = iBlockState5.getValue(FACING);
            if (enumFacing5 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iBlockState)) {
               f2 = 0.5F;
               bl2 = false;
            } else if (enumFacing5 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iBlockState)) {
               h = 0.5F;
               bl2 = false;
            }
         }
      }

      this.setBlockBounds(h, f, f3, f2, g, f4);
      return bl2;
   }

   public boolean func_176304_i(IBlockAccess blockAccess, BlockPos pos) {
      IBlockState iBlockState = blockAccess.getBlockState(pos);
      EnumFacing enumFacing = iBlockState.getValue(FACING);
      BlockStairs.EnumHalf enumHalf = iBlockState.getValue(HALF);
      boolean bl = enumHalf == BlockStairs.EnumHalf.TOP;
      float f = 0.5F;
      float g = 1.0F;
      if (bl) {
         f = 0.0F;
         g = 0.5F;
      }

      float h = 0.0F;
      float f2 = 0.5F;
      float f3 = 0.5F;
      float f4 = 1.0F;
      boolean bl2 = false;
      if (enumFacing == EnumFacing.EAST) {
         IBlockState iBlockState2 = blockAccess.getBlockState(pos.west());
         Block block = iBlockState2.getBlock();
         if (isBlockStairs(block) && enumHalf == iBlockState2.getValue(HALF)) {
            EnumFacing enumFacing2 = iBlockState2.getValue(FACING);
            if (enumFacing2 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), iBlockState)) {
               f3 = 0.0F;
               f4 = 0.5F;
               bl2 = true;
            } else if (enumFacing2 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), iBlockState)) {
               f3 = 0.5F;
               f4 = 1.0F;
               bl2 = true;
            }
         }
      } else if (enumFacing == EnumFacing.WEST) {
         IBlockState iBlockState3 = blockAccess.getBlockState(pos.east());
         Block block2 = iBlockState3.getBlock();
         if (isBlockStairs(block2) && enumHalf == iBlockState3.getValue(HALF)) {
            h = 0.5F;
            f2 = 1.0F;
            EnumFacing enumFacing3 = iBlockState3.getValue(FACING);
            if (enumFacing3 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), iBlockState)) {
               f3 = 0.0F;
               f4 = 0.5F;
               bl2 = true;
            } else if (enumFacing3 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), iBlockState)) {
               f3 = 0.5F;
               f4 = 1.0F;
               bl2 = true;
            }
         }
      } else if (enumFacing == EnumFacing.SOUTH) {
         IBlockState iBlockState4 = blockAccess.getBlockState(pos.north());
         Block block3 = iBlockState4.getBlock();
         if (isBlockStairs(block3) && enumHalf == iBlockState4.getValue(HALF)) {
            f3 = 0.0F;
            f4 = 0.5F;
            EnumFacing enumFacing4 = iBlockState4.getValue(FACING);
            if (enumFacing4 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), iBlockState)) {
               bl2 = true;
            } else if (enumFacing4 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), iBlockState)) {
               h = 0.5F;
               f2 = 1.0F;
               bl2 = true;
            }
         }
      } else if (enumFacing == EnumFacing.NORTH) {
         IBlockState iBlockState5 = blockAccess.getBlockState(pos.south());
         Block block4 = iBlockState5.getBlock();
         if (isBlockStairs(block4) && enumHalf == iBlockState5.getValue(HALF)) {
            EnumFacing enumFacing5 = iBlockState5.getValue(FACING);
            if (enumFacing5 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), iBlockState)) {
               bl2 = true;
            } else if (enumFacing5 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), iBlockState)) {
               h = 0.5F;
               f2 = 1.0F;
               bl2 = true;
            }
         }
      }

      if (bl2) {
         this.setBlockBounds(h, f, f3, f2, g, f4);
      }

      return bl2;
   }

   @Override
   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      this.setBaseCollisionBounds(worldIn, pos);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      boolean bl = this.func_176306_h(worldIn, pos);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      if (bl && this.func_176304_i(worldIn, pos)) {
         super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      }

      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      this.modelBlock.randomDisplayTick(worldIn, pos, state, rand);
   }

   @Override
   public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
      this.modelBlock.onBlockClicked(worldIn, pos, playerIn);
   }

   @Override
   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      this.modelBlock.onBlockDestroyedByPlayer(worldIn, pos, state);
   }

   @Override
   public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos) {
      return this.modelBlock.getMixedBrightnessForBlock(worldIn, pos);
   }

   @Override
   public float getExplosionResistance(Entity exploder) {
      return this.modelBlock.getExplosionResistance(exploder);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return this.modelBlock.getBlockLayer();
   }

   @Override
   public int tickRate(World worldIn) {
      return this.modelBlock.tickRate(worldIn);
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
      return this.modelBlock.getSelectedBoundingBox(worldIn, pos);
   }

   @Override
   public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion) {
      return this.modelBlock.modifyAcceleration(worldIn, pos, entityIn, motion);
   }

   @Override
   public boolean isCollidable() {
      return this.modelBlock.isCollidable();
   }

   @Override
   public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
      return this.modelBlock.canCollideCheck(state, hitIfLiquid);
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return this.modelBlock.canPlaceBlockAt(worldIn, pos);
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.onNeighborBlockChange(worldIn, pos, this.modelState, Blocks.air);
      this.modelBlock.onBlockAdded(worldIn, pos, this.modelState);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      this.modelBlock.breakBlock(worldIn, pos, this.modelState);
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn) {
      this.modelBlock.onEntityCollidedWithBlock(worldIn, pos, entityIn);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      this.modelBlock.updateTick(worldIn, pos, state, rand);
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      return this.modelBlock.onBlockActivated(worldIn, pos, this.modelState, playerIn, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
   }

   @Override
   public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
      this.modelBlock.onBlockDestroyedByExplosion(worldIn, pos, explosionIn);
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return this.modelBlock.getMapColor(this.modelState);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState iBlockState = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
      iBlockState = iBlockState.withProperty(FACING, placer.getHorizontalFacing()).withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
      return facing == EnumFacing.DOWN || facing != EnumFacing.UP && !(hitY <= 0.5)
         ? iBlockState.withProperty(HALF, BlockStairs.EnumHalf.TOP)
         : iBlockState.withProperty(HALF, BlockStairs.EnumHalf.BOTTOM);
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 startIn, Vec3 end) {
      MovingObjectPosition[] movingObjectPositions = new MovingObjectPosition[8];
      IBlockState iBlockState = worldIn.getBlockState(pos);
      int i = iBlockState.getValue(FACING).getHorizontalIndex();
      boolean bl = iBlockState.getValue(HALF) == BlockStairs.EnumHalf.TOP;
      int[] ints = field_150150_a[i + (bl ? 4 : 0)];
      this.hasRaytraced = true;

      for (int j = 0; j < 8; j++) {
         this.rayTracePass = j;
         if (Arrays.binarySearch(ints, j) < 0) {
            movingObjectPositions[j] = super.collisionRayTrace(worldIn, pos, startIn, end);
         }
      }

      for (int k : ints) {
         movingObjectPositions[k] = null;
      }

      MovingObjectPosition movingObjectPosition = null;
      double d = 0.0;

      for (MovingObjectPosition movingObjectPosition2 : movingObjectPositions) {
         if (movingObjectPosition2 != null) {
            double e = movingObjectPosition2.hitVec.squareDistanceTo(end);
            if (e > d) {
               movingObjectPosition = movingObjectPosition2;
               d = e;
            }
         }
      }

      return movingObjectPosition;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      IBlockState iBlockState = this.getDefaultState().withProperty(HALF, (meta & 4) > 0 ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM);
      return iBlockState.withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      byte b = 0;
      if (state.getValue(HALF) == BlockStairs.EnumHalf.TOP) {
         b |= 4;
      }

      return b | 5 - state.getValue(FACING).getIndex();
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      if (this.func_176306_h(worldIn, pos)) {
         switch (this.func_176305_g(worldIn, pos)) {
            case 0:
               state = state.withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
               break;
            case 1:
               state = state.withProperty(SHAPE, BlockStairs.EnumShape.INNER_RIGHT);
               break;
            case 2:
               state = state.withProperty(SHAPE, BlockStairs.EnumShape.INNER_LEFT);
         }
      } else {
         switch (this.func_176307_f(worldIn, pos)) {
            case 0:
               state = state.withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
               break;
            case 1:
               state = state.withProperty(SHAPE, BlockStairs.EnumShape.OUTER_RIGHT);
               break;
            case 2:
               state = state.withProperty(SHAPE, BlockStairs.EnumShape.OUTER_LEFT);
         }
      }

      return state;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, HALF, SHAPE);
   }

   public enum EnumHalf implements IStringSerializable {
      TOP("top"),
      BOTTOM("bottom");

      private final String name;

      EnumHalf(String name) {
         this.name = name;
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

      EnumShape(String name) {
         this.name = name;
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
