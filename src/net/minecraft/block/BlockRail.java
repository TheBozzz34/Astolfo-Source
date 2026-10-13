package net.minecraft.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRail extends BlockRailBase {
   public static final PropertyEnum<BlockRailBase.EnumRailDirection> SHAPE = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class);
   private static final long i = 44233709347906L;

   public BlockRail() {
      super(false);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
   }

   @Override
   public void onNeighborChangedInternal(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (var4.canProvidePower() && new BlockRailBase.Rail(var1, var2, var3).countAdjacentRails() == 3) {
         this.func_176564_a(var1, var2, var3, false);
      }
   }

   @Override
   public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty() {
      return SHAPE;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(var1));
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(SHAPE).getMetadata();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, SHAPE);
   }
}
