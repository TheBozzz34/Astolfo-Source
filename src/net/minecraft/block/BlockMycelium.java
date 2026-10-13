package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMycelium extends Block {
   public static final PropertyBool SNOWY = PropertyBool.create("snowy");
   private static final long e = 56082587996184L;

   public BlockMycelium() {
      super(Material.grass, MapColor.purpleColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SNOWY, false));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      Block var7 = var2.getBlockState(var3.up()).getBlock();
      return var1.withProperty(SNOWY, var7 == Blocks.snow || var7 == Blocks.snow_layer);
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.isRemote) {
         if (var1.getLightFromNeighbors(var2.up()) < 4 && var1.getBlockState(var2.up()).getBlock().getLightOpacity() > 2) {
            var1.setBlockState(var2, Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
         } else if (var1.getLightFromNeighbors(var2.up()) >= 9) {
            for (int var8 = 0; var8 < 4; var8++) {
               BlockPos var9 = var2.add(var4.nextInt(3) - 1, var4.nextInt(5) - 3, var4.nextInt(3) - 1);
               IBlockState var10 = var1.getBlockState(var9);
               Block var11 = var1.getBlockState(var9.up()).getBlock();
               if (var10.getBlock() == Blocks.dirt
                  && var10.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT
                  && var1.getLightFromNeighbors(var9.up()) >= 4
                  && var11.getLightOpacity() <= 2) {
                  var1.setBlockState(var9, this.getDefaultState());
               }
            }
         }
      }
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      super.randomDisplayTick(var1, var2, var3, var4);
      if (var4.nextInt(10) == 0) {
         var1.spawnParticle(EnumParticleTypes.TOWN_AURA, var2.getX() + var4.nextFloat(), var2.getY() + 1.1F, var2.getZ() + var4.nextFloat(), 0.0, 0.0, 0.0);
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Blocks.dirt.getItemDropped(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), var2, var3);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return 0;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, SNOWY);
   }
}
