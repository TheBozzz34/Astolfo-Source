package net.minecraft.client.renderer.block.statemap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class DefaultStateMapper extends StateMapperBase {
   @Override
   public ModelResourceLocation getModelResourceLocation(IBlockState var1) {
      return new ModelResourceLocation(Block.blockRegistry.getNameForObject(var1.getBlock()), this.getPropertyString(var1.getProperties()));
   }
}
