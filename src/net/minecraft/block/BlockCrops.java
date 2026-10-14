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
      float f = 0.5F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
      this.setCreativeTab((CreativeTabs)null);
      this.setHardness(0.0F);
      this.setStepSound(soundTypeGrass);
      this.disableStats();
   }

   @Override
   public boolean canPlaceBlockOn(Block ground) {
      return ground == Blocks.farmland;
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      super.updateTick(worldIn, pos, state, rand);
      if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
         int i = state.getValue(AGE);
         if (i < 7) {
            float f = getGrowthChance(this, worldIn, pos);
            if (rand.nextInt((int)(25.0F / f) + 1) == 0) {
               worldIn.setBlockState(pos, state.withProperty(AGE, i + 1), 2);
            }
         }
      }
   }

   public void grow(World worldIn, BlockPos pos, IBlockState state) {
      int i = state.getValue(AGE) + MathHelper.getRandomIntegerInRange(worldIn.rand, 2, 5);
      if (i > 7) {
         i = 7;
      }

      worldIn.setBlockState(pos, state.withProperty(AGE, i), 2);
   }

   public static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos) {
      float f = 1.0F;
      BlockPos blockPos = pos.down();

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            float g = 0.0F;
            IBlockState iBlockState = worldIn.getBlockState(blockPos.add(i, 0, j));
            if (iBlockState.getBlock() == Blocks.farmland) {
               g = 1.0F;
               if (iBlockState.getValue(BlockFarmland.MOISTURE) > 0) {
                  g = 3.0F;
               }
            }

            if (i != 0 || j != 0) {
               g /= 4.0F;
            }

            f += g;
         }
      }

      BlockPos blockPos2 = pos.north();
      BlockPos blockPos3 = pos.south();
      BlockPos blockPos4 = pos.west();
      BlockPos blockPos5 = pos.east();
      boolean bl = blockIn == worldIn.getBlockState(blockPos4).getBlock() || blockIn == worldIn.getBlockState(blockPos5).getBlock();
      boolean bl2 = blockIn == worldIn.getBlockState(blockPos2).getBlock() || blockIn == worldIn.getBlockState(blockPos3).getBlock();
      if (bl && bl2) {
         f /= 2.0F;
      } else {
         boolean bl3 = blockIn == worldIn.getBlockState(blockPos4.north()).getBlock()
            || blockIn == worldIn.getBlockState(blockPos5.north()).getBlock()
            || blockIn == worldIn.getBlockState(blockPos5.south()).getBlock()
            || blockIn == worldIn.getBlockState(blockPos4.south()).getBlock();
         if (bl3) {
            f /= 2.0F;
         }
      }

      return f;
   }

   @Override
   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      return (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
   }

   public Item getSeed() {
      return Items.wheat_seeds;
   }

   public Item getCrop() {
      return Items.wheat;
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
      if (!worldIn.isRemote) {
         int i = state.getValue(AGE);
         if (i >= 7) {
            int j = 3 + fortune;

            for (int k = 0; k < j; k++) {
               if (worldIn.rand.nextInt(15) <= i) {
                  spawnAsEntity(worldIn, pos, new ItemStack(this.getSeed(), 1, 0));
               }
            }
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return state.getValue(AGE) == 7 ? this.getCrop() : this.getSeed();
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return this.getSeed();
   }

   @Override
   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return state.getValue(AGE) < 7;
   }

   @Override
   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   @Override
   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      this.grow(worldIn, pos, state);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, meta);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(AGE);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AGE);
   }
}
