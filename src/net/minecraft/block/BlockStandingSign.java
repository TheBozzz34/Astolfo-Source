package net.minecraft.block;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockStandingSign extends BlockSign {
   public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 15);
   private static final long i = 49027669299152L;

   public BlockStandingSign() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(ROTATION, 0));
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.getBlockState(var2.down()).getBlock().getMaterial().isSolid()) {
         this.dropBlockAsItem(var1, var2, var3, 0);
         var1.setBlockToAir(var2);
      }

      super.onNeighborBlockChange(var1, var2, var3, var4);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(ROTATION, var1);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(ROTATION);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, ROTATION);
   }
}
