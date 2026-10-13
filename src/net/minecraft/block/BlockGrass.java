package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public class BlockGrass extends Block implements IGrowable {
   public static final PropertyBool SNOWY = PropertyBool.create("snowy");
   private static final long e = 120135262989908L;

   public BlockGrass() {
      super(Material.grass);
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
   public int getBlockColor() {
      return ColorizerGrass.getGrassColor(0.5, 1.0);
   }

   @Override
   public int getRenderColor(IBlockState var1) {
      return this.getBlockColor();
   }

   @Override
   public int colorMultiplier(IBlockAccess var1, BlockPos var2, int var3) {
      return BiomeColorHelper.getGrassColorAtPos(var1, var2);
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.isRemote) {
         if (var1.getLightFromNeighbors(var2.up()) < 4 && var1.getBlockState(var2.up()).getBlock().getLightOpacity() > 2) {
            var1.setBlockState(var2, Blocks.dirt.getDefaultState());
         } else if (var1.getLightFromNeighbors(var2.up()) >= 9) {
            for (int var8 = 0; var8 < 4; var8++) {
               BlockPos var9 = var2.add(var4.nextInt(3) - 1, var4.nextInt(5) - 3, var4.nextInt(3) - 1);
               Block var10 = var1.getBlockState(var9.up()).getBlock();
               IBlockState var11 = var1.getBlockState(var9);
               if (var11.getBlock() == Blocks.dirt
                  && var11.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT
                  && var1.getLightFromNeighbors(var9.up()) >= 4
                  && var10.getLightOpacity() <= 2) {
                  var1.setBlockState(var9, Blocks.grass.getDefaultState());
               }
            }
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Blocks.dirt.getItemDropped(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), var2, var3);
   }

   @Override
   public boolean canGrow(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return true;
   }

   @Override
   public boolean canUseBonemeal(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   @Override
   public void grow(World var1, Random var2, BlockPos var3, IBlockState var4) {
      BlockPos var8 = var3.up();

      label38:
      for (int var9 = 0; var9 < 128; var9++) {
         BlockPos var10 = var8;

         for (int var11 = 0; var11 < var9 / 16; var11++) {
            var10 = var10.add(var2.nextInt(3) - 1, (var2.nextInt(3) - 1) * var2.nextInt(3) / 2, var2.nextInt(3) - 1);
            if (var1.getBlockState(var10.down()).getBlock() != Blocks.grass || var1.getBlockState(var10).getBlock().isNormalCube()) {
               continue label38;
            }
         }

         if (var1.getBlockState(var10).getBlock().blockMaterial == Material.air) {
            if (var2.nextInt(8) == 0) {
               BlockFlower.EnumFlowerType var12 = var1.getBiomeGenForCoords(var10).pickRandomFlower(var2, var10);
               BlockFlower var13 = var12.getBlockType().getBlock();
               IBlockState var14 = var13.getDefaultState().withProperty(var13.getTypeProperty(), var12);
               if (var13.canBlockStay(var1, var10, var14)) {
                  var1.setBlockState(var10, var14, 3);
               }
            } else {
               IBlockState var15 = Blocks.tallgrass.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);
               if (Blocks.tallgrass.canBlockStay(var1, var10, var15)) {
                  var1.setBlockState(var10, var15, 3);
               }
            }
         }
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT_MIPPED;
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
