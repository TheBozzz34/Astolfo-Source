package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCrops extends BlockBush implements IGrowable {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
   private static final long e = 125088117987560L;

   public BlockCrops() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
      this.setTickRandomly(true);
      float var1 = 0.5F;
      this.setBlockBounds(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.25F, 0.5F + var1);
      this.setCreativeTab((CreativeTabs)null);
      this.setHardness(0.0F);
      this.setStepSound(soundTypeGrass);
      this.disableStats();
   }

   @Override
   public boolean canPlaceBlockOn(Block var1) {
      return var1 == Blocks.farmland;
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      super.updateTick(var1, var2, var3, var4);
      if (var1.getLightFromNeighbors(var2.up()) >= 9) {
         int var8 = var3.getValue(AGE);
         if (var8 < 7) {
            float var9 = getGrowthChance(this, var1, var2);
            if (var4.nextInt((int)(25.0F / var9) + 1) == 0) {
               var1.setBlockState(var2, var3.withProperty(AGE, var8 + 1), 2);
            }
         }
      }
   }

   public void grow(World var1, BlockPos var2, IBlockState var3) {
      int var7 = var3.getValue(AGE) + MathHelper.getRandomIntegerInRange(var1.rand, 2, 5);
      if (var7 > 7) {
         var7 = 7;
      }

      var1.setBlockState(var2, var3.withProperty(AGE, var7), 2);
   }

   public static float getGrowthChance(Block var0, World var1, BlockPos var2) {
      float var6 = 1.0F;
      BlockPos var7 = var2.down();

      for (int var8 = -1; var8 <= 1; var8++) {
         for (int var9 = -1; var9 <= 1; var9++) {
            float var10 = 0.0F;
            IBlockState var11 = var1.getBlockState(var7.add(var8, 0, var9));
            if (var11.getBlock() == Blocks.farmland) {
               var10 = 1.0F;
               if (var11.getValue(BlockFarmland.MOISTURE) > 0) {
                  var10 = 3.0F;
               }
            }

            if (var8 != 0 || var9 != 0) {
               var10 /= 4.0F;
            }

            var6 += var10;
         }
      }

      BlockPos var15 = var2.north();
      BlockPos var16 = var2.south();
      BlockPos var17 = var2.west();
      BlockPos var18 = var2.east();
      boolean var12 = var0 == var1.getBlockState(var17).getBlock() || var0 == var1.getBlockState(var18).getBlock();
      boolean var13 = var0 == var1.getBlockState(var15).getBlock() || var0 == var1.getBlockState(var16).getBlock();
      if (var12 && var13) {
         var6 /= 2.0F;
      } else {
         boolean var14 = var0 == var1.getBlockState(var17.north()).getBlock()
            || var0 == var1.getBlockState(var18.north()).getBlock()
            || var0 == var1.getBlockState(var18.south()).getBlock()
            || var0 == var1.getBlockState(var17.south()).getBlock();
         if (var14) {
            var6 /= 2.0F;
         }
      }

      return var6;
   }

   @Override
   public boolean canBlockStay(World var1, BlockPos var2, IBlockState var3) {
      return (var1.getLight(var2) >= 8 || var1.canSeeSky(var2)) && this.canPlaceBlockOn(var1.getBlockState(var2.down()).getBlock());
   }

   public Item getSeed() {
      return Items.wheat_seeds;
   }

   public Item getCrop() {
      return Items.wheat;
   }

   @Override
   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      super.dropBlockAsItemWithChance(var1, var2, var3, var4, 0);
      if (!var1.isRemote) {
         int var9 = var3.getValue(AGE);
         if (var9 >= 7) {
            int var10 = 3 + var5;

            for (int var11 = 0; var11 < var10; var11++) {
               if (var1.rand.nextInt(15) <= var9) {
                  spawnAsEntity(var1, var2, new ItemStack(this.getSeed(), 1, 0));
               }
            }
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return var1.getValue(AGE) == 7 ? this.getCrop() : this.getSeed();
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return this.getSeed();
   }

   @Override
   public boolean canGrow(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return var3.getValue(AGE) < 7;
   }

   @Override
   public boolean canUseBonemeal(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   @Override
   public void grow(World var1, Random var2, BlockPos var3, IBlockState var4) {
      this.grow(var1, var3, var4);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(AGE, var1);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(AGE);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AGE);
   }
}
