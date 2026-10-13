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
         private static final long a = 53115744462259L;

         public boolean apply(BlockRailBase.EnumRailDirection var1) {
            return var1 != BlockRailBase.EnumRailDirection.NORTH_EAST
               && var1 != BlockRailBase.EnumRailDirection.NORTH_WEST
               && var1 != BlockRailBase.EnumRailDirection.SOUTH_EAST
               && var1 != BlockRailBase.EnumRailDirection.SOUTH_WEST;
         }
      }
   );
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   private static final long i = 64738556740391L;

   public BlockRailPowered() {
      super(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH).withProperty(POWERED, false));
   }

   public boolean func_176566_a(World var1, BlockPos var2, IBlockState var3, boolean var4, int var5) {
      if (var5 >= 8) {
         return false;
      }

      int var9 = var2.getX();
      int var10 = var2.getY();
      int var11 = var2.getZ();
      boolean var12 = true;
      BlockRailBase.EnumRailDirection var13 = var3.getValue(SHAPE);
      switch (var13) {
         case NORTH_SOUTH:
            if (var4) {
               var11++;
            } else {
               var11--;
            }
            break;
         case EAST_WEST:
            if (var4) {
               var9--;
            } else {
               var9++;
            }
            break;
         case ASCENDING_EAST:
            if (var4) {
               var9--;
            } else {
               var9++;
               var10++;
               var12 = false;
            }

            var13 = BlockRailBase.EnumRailDirection.EAST_WEST;
            break;
         case ASCENDING_WEST:
            if (var4) {
               var9--;
               var10++;
               var12 = false;
            } else {
               var9++;
            }

            var13 = BlockRailBase.EnumRailDirection.EAST_WEST;
            break;
         case ASCENDING_NORTH:
            if (var4) {
               var11++;
            } else {
               var11--;
               var10++;
               var12 = false;
            }

            var13 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            break;
         case ASCENDING_SOUTH:
            if (var4) {
               var11++;
               var10++;
               var12 = false;
            } else {
               var11--;
            }

            var13 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
      }

      return this.func_176567_a(var1, new BlockPos(var9, var10, var11), var4, var5, var13)
         ? true
         : var12 && this.func_176567_a(var1, new BlockPos(var9, var10 - 1, var11), var4, var5, var13);
   }

   public boolean func_176567_a(World var1, BlockPos var2, boolean var3, int var4, BlockRailBase.EnumRailDirection var5) {
      IBlockState var9 = var1.getBlockState(var2);
      if (var9.getBlock() != this) {
         return false;
      }

      BlockRailBase.EnumRailDirection var10 = var9.getValue(SHAPE);
      return var5 != BlockRailBase.EnumRailDirection.EAST_WEST
            || var10 != BlockRailBase.EnumRailDirection.NORTH_SOUTH
               && var10 != BlockRailBase.EnumRailDirection.ASCENDING_NORTH
               && var10 != BlockRailBase.EnumRailDirection.ASCENDING_SOUTH
         ? (
            var5 != BlockRailBase.EnumRailDirection.NORTH_SOUTH
                  || var10 != BlockRailBase.EnumRailDirection.EAST_WEST
                     && var10 != BlockRailBase.EnumRailDirection.ASCENDING_EAST
                     && var10 != BlockRailBase.EnumRailDirection.ASCENDING_WEST
               ? (var9.getValue(POWERED) ? (var1.isBlockPowered(var2) ? true : this.func_176566_a(var1, var2, var9, var3, var4 + 1)) : false)
               : false
         )
         : false;
   }

   @Override
   public void onNeighborChangedInternal(World var1, BlockPos var2, IBlockState var3, Block var4) {
      boolean var8 = var3.getValue(POWERED);
      boolean var9 = var1.isBlockPowered(var2) || this.func_176566_a(var1, var2, var3, true, 0) || this.func_176566_a(var1, var2, var3, false, 0);
      if (var9 != var8) {
         var1.setBlockState(var2, var3.withProperty(POWERED, var9), 3);
         var1.notifyNeighborsOfStateChange(var2.down(), this);
         if (var3.getValue(SHAPE).isAscending()) {
            var1.notifyNeighborsOfStateChange(var2.up(), this);
         }
      }
   }

   @Override
   public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty() {
      return SHAPE;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(var1 & 7)).withProperty(POWERED, (var1 & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(SHAPE).getMetadata();
      if (var1.getValue(POWERED)) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, SHAPE, POWERED);
   }
}
