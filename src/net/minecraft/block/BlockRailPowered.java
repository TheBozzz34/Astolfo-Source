package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRailPowered extends BlockRailBase {
   public static final PropertyEnum<BlockRailBase.EnumRailDirection> SHAPE = PropertyEnum.create(
      "shape",
      BlockRailBase.EnumRailDirection.class,
      new Predicate<BlockRailBase.EnumRailDirection>() {
         public boolean apply(BlockRailBase.EnumRailDirection enumRailDirection) {
            return enumRailDirection != BlockRailBase.EnumRailDirection.NORTH_EAST
               && enumRailDirection != BlockRailBase.EnumRailDirection.NORTH_WEST
               && enumRailDirection != BlockRailBase.EnumRailDirection.SOUTH_EAST
               && enumRailDirection != BlockRailBase.EnumRailDirection.SOUTH_WEST;
         }
      }
   );
   public static final PropertyBool POWERED = PropertyBool.create("powered");

   public BlockRailPowered() {
      super(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH).withProperty(POWERED, false));
   }

   public boolean func_176566_a(World worldIn, BlockPos pos, IBlockState state, boolean bl, int i) {
      if (i >= 8) {
         return false;
      }

      int j = pos.getX();
      int k = pos.getY();
      int l = pos.getZ();
      boolean bl2 = true;
      BlockRailBase.EnumRailDirection enumRailDirection = state.getValue(SHAPE);
      switch (enumRailDirection) {
         case NORTH_SOUTH:
            if (bl) {
               l++;
            } else {
               l--;
            }
            break;
         case EAST_WEST:
            if (bl) {
               j--;
            } else {
               j++;
            }
            break;
         case ASCENDING_EAST:
            if (bl) {
               j--;
            } else {
               j++;
               k++;
               bl2 = false;
            }

            enumRailDirection = BlockRailBase.EnumRailDirection.EAST_WEST;
            break;
         case ASCENDING_WEST:
            if (bl) {
               j--;
               k++;
               bl2 = false;
            } else {
               j++;
            }

            enumRailDirection = BlockRailBase.EnumRailDirection.EAST_WEST;
            break;
         case ASCENDING_NORTH:
            if (bl) {
               l++;
            } else {
               l--;
               k++;
               bl2 = false;
            }

            enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            break;
         case ASCENDING_SOUTH:
            if (bl) {
               l++;
               k++;
               bl2 = false;
            } else {
               l--;
            }

            enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
      }

      return this.func_176567_a(worldIn, new BlockPos(j, k, l), bl, i, enumRailDirection)
         ? true
         : bl2 && this.func_176567_a(worldIn, new BlockPos(j, k - 1, l), bl, i, enumRailDirection);
   }

   public boolean func_176567_a(World worldIn, BlockPos blockPos, boolean bl, int distance, BlockRailBase.EnumRailDirection enumRailDirection) {
      IBlockState iBlockState = worldIn.getBlockState(blockPos);
      if (iBlockState.getBlock() != this) {
         return false;
      }

      BlockRailBase.EnumRailDirection enumRailDirection2 = iBlockState.getValue(SHAPE);
      return enumRailDirection != BlockRailBase.EnumRailDirection.EAST_WEST
            || enumRailDirection2 != BlockRailBase.EnumRailDirection.NORTH_SOUTH
               && enumRailDirection2 != BlockRailBase.EnumRailDirection.ASCENDING_NORTH
               && enumRailDirection2 != BlockRailBase.EnumRailDirection.ASCENDING_SOUTH
         ? (
            enumRailDirection != BlockRailBase.EnumRailDirection.NORTH_SOUTH
                  || enumRailDirection2 != BlockRailBase.EnumRailDirection.EAST_WEST
                     && enumRailDirection2 != BlockRailBase.EnumRailDirection.ASCENDING_EAST
                     && enumRailDirection2 != BlockRailBase.EnumRailDirection.ASCENDING_WEST
               ? (iBlockState.getValue(POWERED) ? (worldIn.isBlockPowered(blockPos) ? true : this.func_176566_a(worldIn, blockPos, iBlockState, bl, distance + 1)) : false)
               : false
         )
         : false;
   }

   @Override
   public void onNeighborChangedInternal(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      boolean bl = state.getValue(POWERED);
      boolean bl2 = worldIn.isBlockPowered(pos) || this.func_176566_a(worldIn, pos, state, true, 0) || this.func_176566_a(worldIn, pos, state, false, 0);
      if (bl2 != bl) {
         worldIn.setBlockState(pos, state.withProperty(POWERED, bl2), 3);
         worldIn.notifyNeighborsOfStateChange(pos.down(), this);
         if (state.getValue(SHAPE).isAscending()) {
            worldIn.notifyNeighborsOfStateChange(pos.up(), this);
         }
      }
   }

   @Override
   public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty() {
      return SHAPE;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta & 7)).withProperty(POWERED, (meta & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i |= state.getValue(SHAPE).getMetadata();
      if (state.getValue(POWERED)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, SHAPE, POWERED);
   }
}
