package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BlockSnowBlock extends Block {
   private static final long e = 85695792303681L;
   private static final long i = -3849075727043395573L;

   public BlockSnowBlock() {
      super(Material.craftedSnow);
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.snowball;
   }

   @Override
   public int quantityDropped(Random randomIn) {
      return 4;
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > (int)i) {
         this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0);
         worldIn.setBlockToAir(pos);
      }
   }
}
