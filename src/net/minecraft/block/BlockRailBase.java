package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {
   public final boolean isPowered;

   public static boolean isRailBlock(World worldIn, BlockPos pos) {
      return isRailBlock(worldIn.getBlockState(pos));
   }

   public static boolean isRailBlock(IBlockState state) {
      Block block = state.getBlock();
      return block == Blocks.rail || block == Blocks.golden_rail || block == Blocks.detector_rail || block == Blocks.activator_rail;
   }

   public BlockRailBase(boolean isPowered) {
      super(Material.circuits);
      this.isPowered = isPowered;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      this.setCreativeTab(CreativeTabs.tabTransport);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 startIn, Vec3 end) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.collisionRayTrace(worldIn, pos, startIn, end);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      IBlockState iBlockState = worldIn.getBlockState(pos);
      BlockRailBase.EnumRailDirection enumRailDirection = iBlockState.getBlock() == this ? iBlockState.getValue(this.getShapeProperty()) : null;
      if (enumRailDirection != null && enumRailDirection.isAscending()) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      }
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         state = this.func_176564_a(worldIn, pos, state, true);
         if (this.isPowered) {
            this.onNeighborBlockChange(worldIn, pos, state, this);
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         BlockRailBase.EnumRailDirection enumRailDirection = state.getValue(this.getShapeProperty());
         boolean bl = false;
         if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
            bl = true;
         }

         if (enumRailDirection == BlockRailBase.EnumRailDirection.ASCENDING_EAST && !World.doesBlockHaveSolidTopSurface(worldIn, pos.east())) {
            bl = true;
         } else if (enumRailDirection == BlockRailBase.EnumRailDirection.ASCENDING_WEST && !World.doesBlockHaveSolidTopSurface(worldIn, pos.west())) {
            bl = true;
         } else if (enumRailDirection == BlockRailBase.EnumRailDirection.ASCENDING_NORTH && !World.doesBlockHaveSolidTopSurface(worldIn, pos.north())) {
            bl = true;
         } else if (enumRailDirection == BlockRailBase.EnumRailDirection.ASCENDING_SOUTH && !World.doesBlockHaveSolidTopSurface(worldIn, pos.south())) {
            bl = true;
         }

         if (bl) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         } else {
            this.onNeighborChangedInternal(worldIn, pos, state, neighborBlock);
         }
      }
   }

   public void onNeighborChangedInternal(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
   }

   public IBlockState func_176564_a(World worldIn, BlockPos blockPos, IBlockState iBlockState, boolean bl) {
      return worldIn.isRemote
         ? iBlockState
         : new BlockRailBase.Rail(worldIn, blockPos, iBlockState).func_180364_a(worldIn.isBlockPowered(blockPos), bl).getBlockState();
   }

   @Override
   public int getMobilityFlag() {
      return 0;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      if (state.getValue(this.getShapeProperty()).isAscending()) {
         worldIn.notifyNeighborsOfStateChange(pos.up(), this);
      }

      if (this.isPowered) {
         worldIn.notifyNeighborsOfStateChange(pos, this);
         worldIn.notifyNeighborsOfStateChange(pos.down(), this);
      }
   }

   public abstract IProperty<BlockRailBase.EnumRailDirection> getShapeProperty();

   public enum EnumRailDirection implements IStringSerializable {
      NORTH_SOUTH(0, "north_south"),
      EAST_WEST(1, "east_west"),
      ASCENDING_EAST(2, "ascending_east"),
      ASCENDING_WEST(3, "ascending_west"),
      ASCENDING_NORTH(4, "ascending_north"),
      ASCENDING_SOUTH(5, "ascending_south"),
      SOUTH_EAST(6, "south_east"),
      SOUTH_WEST(7, "south_west"),
      NORTH_WEST(8, "north_west"),
      NORTH_EAST(9, "north_east");

      private static final BlockRailBase.EnumRailDirection[] META_LOOKUP = new BlockRailBase.EnumRailDirection[values().length];
      private final int meta;
      private final String name;

      EnumRailDirection(int meta, String name) {
         this.meta = meta;
         this.name = name;
      }

      public int getMetadata() {
         return this.meta;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public boolean isAscending() {
         return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
      }

      public static BlockRailBase.EnumRailDirection byMetadata(int meta) {
         if (meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
         }

         return META_LOOKUP[meta];
      }

      @Override
      public String getName() {
         return this.name;
      }

      static {
         for (BlockRailBase.EnumRailDirection enumRailDirection : values()) {
            META_LOOKUP[enumRailDirection.getMetadata()] = enumRailDirection;
         }
      }
   }

   public class Rail {
      private final World world;
      private final BlockPos pos;
      private final BlockRailBase block;
      private IBlockState state;
      private final boolean isPowered;
      private final List<BlockPos> field_150657_g = Lists.newArrayList();

      public Rail(World worldIn, BlockPos pos, IBlockState state) {
         this.world = worldIn;
         this.pos = pos;
         this.state = state;
         this.block = (BlockRailBase)state.getBlock();
         BlockRailBase.EnumRailDirection enumRailDirection = state.getValue(BlockRailBase.this.getShapeProperty());
         this.isPowered = this.block.isPowered;
         this.func_180360_a(enumRailDirection);
      }

      private void func_180360_a(BlockRailBase.EnumRailDirection enumRailDirection) {
         this.field_150657_g.clear();
         switch (enumRailDirection) {
            case NORTH_SOUTH:
               this.field_150657_g.add(this.pos.north());
               this.field_150657_g.add(this.pos.south());
               break;
            case EAST_WEST:
               this.field_150657_g.add(this.pos.west());
               this.field_150657_g.add(this.pos.east());
               break;
            case ASCENDING_EAST:
               this.field_150657_g.add(this.pos.west());
               this.field_150657_g.add(this.pos.east().up());
               break;
            case ASCENDING_WEST:
               this.field_150657_g.add(this.pos.west().up());
               this.field_150657_g.add(this.pos.east());
               break;
            case ASCENDING_NORTH:
               this.field_150657_g.add(this.pos.north().up());
               this.field_150657_g.add(this.pos.south());
               break;
            case ASCENDING_SOUTH:
               this.field_150657_g.add(this.pos.north());
               this.field_150657_g.add(this.pos.south().up());
               break;
            case SOUTH_EAST:
               this.field_150657_g.add(this.pos.east());
               this.field_150657_g.add(this.pos.south());
               break;
            case SOUTH_WEST:
               this.field_150657_g.add(this.pos.west());
               this.field_150657_g.add(this.pos.south());
               break;
            case NORTH_WEST:
               this.field_150657_g.add(this.pos.west());
               this.field_150657_g.add(this.pos.north());
               break;
            case NORTH_EAST:
               this.field_150657_g.add(this.pos.east());
               this.field_150657_g.add(this.pos.north());
         }
      }

      private void func_150651_b() {
         for (int i = 0; i < this.field_150657_g.size(); i++) {
            BlockRailBase.Rail rail = this.findRailAt(this.field_150657_g.get(i));
            if (rail != null && rail.func_150653_a(this)) {
               this.field_150657_g.set(i, rail.pos);
            } else {
               this.field_150657_g.remove(i--);
            }
         }
      }

      private boolean hasRailAt(BlockPos pos) {
         return BlockRailBase.isRailBlock(this.world, pos)
            || BlockRailBase.isRailBlock(this.world, pos.up())
            || BlockRailBase.isRailBlock(this.world, pos.down());
      }

      private BlockRailBase.Rail findRailAt(BlockPos pos) {
         IBlockState iBlockState = this.world.getBlockState(pos);
         if (BlockRailBase.isRailBlock(iBlockState)) {
            return BlockRailBase.this.new Rail(this.world, pos, iBlockState);
         }

         BlockPos blockPos = pos.up();
         iBlockState = this.world.getBlockState(blockPos);
         if (BlockRailBase.isRailBlock(iBlockState)) {
            return BlockRailBase.this.new Rail(this.world, blockPos, iBlockState);
         }

         blockPos = pos.down();
         iBlockState = this.world.getBlockState(blockPos);
         return BlockRailBase.isRailBlock(iBlockState) ? BlockRailBase.this.new Rail(this.world, blockPos, iBlockState) : null;
      }

      private boolean func_150653_a(BlockRailBase.Rail rail) {
         return this.func_180363_c(rail.pos);
      }

      private boolean func_180363_c(BlockPos blockPos) {
         for (int i = 0; i < this.field_150657_g.size(); i++) {
            BlockPos blockPos2 = this.field_150657_g.get(i);
            if (blockPos2.getX() == blockPos.getX() && blockPos2.getZ() == blockPos.getZ()) {
               return true;
            }
         }

         return false;
      }

      public int countAdjacentRails() {
         int i = 0;

         for (Object object : EnumFacing.Plane.HORIZONTAL) {
            if (this.hasRailAt(this.pos.offset((EnumFacing)object))) {
               i++;
            }
         }

         return i;
      }

      private boolean func_150649_b(BlockRailBase.Rail rail) {
         return this.func_150653_a(rail) || this.field_150657_g.size() != 2;
      }

      private void func_150645_c(BlockRailBase.Rail rail) {
         this.field_150657_g.add(rail.pos);
         BlockPos blockPos = this.pos.north();
         BlockPos blockPos2 = this.pos.south();
         BlockPos blockPos3 = this.pos.west();
         BlockPos blockPos4 = this.pos.east();
         boolean bl = this.func_180363_c(blockPos);
         boolean bl2 = this.func_180363_c(blockPos2);
         boolean bl3 = this.func_180363_c(blockPos3);
         boolean bl4 = this.func_180363_c(blockPos4);
         BlockRailBase.EnumRailDirection enumRailDirection = null;
         if (bl || bl2) {
            enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         if (bl3 || bl4) {
            enumRailDirection = BlockRailBase.EnumRailDirection.EAST_WEST;
         }

         if (!this.isPowered) {
            if (bl2 && bl4 && !bl && !bl3) {
               enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
            }

            if (bl2 && bl3 && !bl && !bl4) {
               enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
            }

            if (bl && bl3 && !bl2 && !bl4) {
               enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
            }

            if (bl && bl4 && !bl2 && !bl3) {
               enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
            }
         }

         if (enumRailDirection == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (BlockRailBase.isRailBlock(this.world, blockPos.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
            }

            if (BlockRailBase.isRailBlock(this.world, blockPos2.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
            }
         }

         if (enumRailDirection == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (BlockRailBase.isRailBlock(this.world, blockPos4.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
            }

            if (BlockRailBase.isRailBlock(this.world, blockPos3.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
            }
         }

         if (enumRailDirection == null) {
            enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         this.state = this.state.withProperty(this.block.getShapeProperty(), enumRailDirection);
         this.world.setBlockState(this.pos, this.state, 3);
      }

      private boolean func_180361_d(BlockPos blockPos) {
         BlockRailBase.Rail rail = this.findRailAt(blockPos);
         if (rail == null) {
            return false;
         }

         rail.func_150651_b();
         return rail.func_150649_b(this);
      }

      public BlockRailBase.Rail func_180364_a(boolean bl, boolean bl2) {
         BlockPos blockPos = this.pos.north();
         BlockPos blockPos2 = this.pos.south();
         BlockPos blockPos3 = this.pos.west();
         BlockPos blockPos4 = this.pos.east();
         boolean bl3 = this.func_180361_d(blockPos);
         boolean bl4 = this.func_180361_d(blockPos2);
         boolean bl5 = this.func_180361_d(blockPos3);
         boolean bl6 = this.func_180361_d(blockPos4);
         BlockRailBase.EnumRailDirection enumRailDirection = null;
         if ((bl3 || bl4) && !bl5 && !bl6) {
            enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         if ((bl5 || bl6) && !bl3 && !bl4) {
            enumRailDirection = BlockRailBase.EnumRailDirection.EAST_WEST;
         }

         if (!this.isPowered) {
            if (bl4 && bl6 && !bl3 && !bl5) {
               enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
            }

            if (bl4 && bl5 && !bl3 && !bl6) {
               enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
            }

            if (bl3 && bl5 && !bl4 && !bl6) {
               enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
            }

            if (bl3 && bl6 && !bl4 && !bl5) {
               enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
            }
         }

         if (enumRailDirection == null) {
            if (bl3 || bl4) {
               enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            if (bl5 || bl6) {
               enumRailDirection = BlockRailBase.EnumRailDirection.EAST_WEST;
            }

            if (!this.isPowered) {
               if (bl) {
                  if (bl4 && bl6) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                  }

                  if (bl5 && bl4) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                  }

                  if (bl6 && bl3) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
                  }

                  if (bl3 && bl5) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
                  }
               } else {
                  if (bl3 && bl5) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
                  }

                  if (bl6 && bl3) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
                  }

                  if (bl5 && bl4) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                  }

                  if (bl4 && bl6) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                  }
               }
            }
         }

         if (enumRailDirection == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (BlockRailBase.isRailBlock(this.world, blockPos.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
            }

            if (BlockRailBase.isRailBlock(this.world, blockPos2.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
            }
         }

         if (enumRailDirection == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (BlockRailBase.isRailBlock(this.world, blockPos4.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
            }

            if (BlockRailBase.isRailBlock(this.world, blockPos3.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
            }
         }

         if (enumRailDirection == null) {
            enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         this.func_180360_a(enumRailDirection);
         this.state = this.state.withProperty(this.block.getShapeProperty(), enumRailDirection);
         if (bl2 || this.world.getBlockState(this.pos) != this.state) {
            this.world.setBlockState(this.pos, this.state, 3);

            for (int i = 0; i < this.field_150657_g.size(); i++) {
               BlockRailBase.Rail rail = this.findRailAt(this.field_150657_g.get(i));
               if (rail != null) {
                  rail.func_150651_b();
                  if (rail.func_150649_b(this)) {
                     rail.func_150645_c(this);
                  }
               }
            }
         }

         return this;
      }

      public IBlockState getBlockState() {
         return this.state;
      }
   }
}
