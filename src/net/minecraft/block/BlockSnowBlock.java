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
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.snowball;
   }

   @Override
   public int quantityDropped(Random var1) {
      return 4;
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var1.getLightFor(EnumSkyBlock.BLOCK, var2) > (int)i) {
         this.dropBlockAsItem(var1, var2, var1.getBlockState(var2), 0);
         var1.setBlockToAir(var2);
      }
   }
}
