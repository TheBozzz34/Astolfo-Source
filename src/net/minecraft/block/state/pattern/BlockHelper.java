package net.minecraft.block.state.pattern;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockHelper implements Predicate<IBlockState> {
   private final Block block;

   private BlockHelper(Block blockType) {
      this.block = blockType;
   }

   public static BlockHelper forBlock(Block blockType) {
      return new BlockHelper(blockType);
   }

   public boolean apply(IBlockState iBlockState) {
      return iBlockState != null && iBlockState.getBlock() == this.block;
   }
}
