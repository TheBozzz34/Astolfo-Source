package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStem extends BlockBush implements IGrowable {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
   public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
      private static final long a = 123161288337523L;

      public boolean apply(EnumFacing var1) {
         return var1 != EnumFacing.DOWN;
      }
   });
   private final Block crop;
   private static final long e = 94411828591722L;

   public BlockStem(Block var1) {
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0).withProperty(FACING, EnumFacing.UP));
      this.crop = var1;
      this.setTickRandomly(true);
      float var2 = 0.125F;
      this.setBlockBounds(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, 0.25F, 0.5F + var2);
      this.setCreativeTab((CreativeTabs)null);
   }

   @Override
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      var1 = var1.withProperty(FACING, EnumFacing.UP);

      for (Object var8 : EnumFacing.Plane.HORIZONTAL) {
         if (var2.getBlockState(var3.offset((EnumFacing)var8)).getBlock() == this.crop) {
            var1 = var1.withProperty(FACING, (EnumFacing)var8);
            break;
         }
      }

      return var1;
   }

   @Override
   public boolean canPlaceBlockOn(Block var1) {
      return var1 == Blocks.farmland;
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      super.updateTick(var1, var2, var3, var4);
      if (var1.getLightFromNeighbors(var2.up()) >= 9) {
         float var8 = BlockCrops.getGrowthChance(this, var1, var2);
         if (var4.nextInt((int)(25.0F / var8) + 1) == 0) {
            int var9 = var3.getValue(AGE);
            if (var9 < 7) {
               var3 = var3.withProperty(AGE, var9 + 1);
               var1.setBlockState(var2, var3, 2);
            } else {
               for (Object var11 : EnumFacing.Plane.HORIZONTAL) {
                  if (var1.getBlockState(var2.offset((EnumFacing)var11)).getBlock() == this.crop) {
                     return;
                  }
               }

               var2 = var2.offset(EnumFacing.Plane.HORIZONTAL.random(var4));
               Block var14 = var1.getBlockState(var2.down()).getBlock();
               if (var1.getBlockState(var2).getBlock().blockMaterial == Material.air
                  && (var14 == Blocks.farmland || var14 == Blocks.dirt || var14 == Blocks.grass)) {
                  var1.setBlockState(var2, this.crop.getDefaultState());
               }
            }
         }
      }
   }

   public void growStem(World var1, BlockPos var2, IBlockState var3) {
      int var6 = var3.getValue(AGE) + MathHelper.getRandomIntegerInRange(var1.rand, 2, 5);
      var1.setBlockState(var2, var3.withProperty(AGE, Math.min(7, var6)), 2);
   }

   @Override
   public int getRenderColor(IBlockState var1) {
      if (var1.getBlock() != this) {
         return super.getRenderColor(var1);
      }

      int var5 = var1.getValue(AGE);
      int var6 = var5 * 32;
      int var7 = 255 - var5 * 8;
      int var8 = var5 * 4;
      return var6 << 16 | var7 << 8 | var8;
   }

   @Override
   public int colorMultiplier(IBlockAccess var1, BlockPos var2, int var3) {
      return this.getRenderColor(var1.getBlockState(var2));
   }

   @Override
   public void setBlockBoundsForItemRender() {
      float var1 = 0.125F;
      this.setBlockBounds(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.25F, 0.5F + var1);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      this.maxY = (var1.getBlockState(var2).getValue(AGE) * 2 + 2) / 16.0F;
      float var3 = 0.125F;
      this.setBlockBounds(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, (float)this.maxY, 0.5F + var3);
   }

   @Override
   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      super.dropBlockAsItemWithChance(var1, var2, var3, var4, var5);
      if (!var1.isRemote) {
         Item var9 = this.getSeedItem();
         if (var9 != null) {
            int var10 = var3.getValue(AGE);

            for (int var11 = 0; var11 < 3; var11++) {
               if (var1.rand.nextInt(15) <= var10) {
                  spawnAsEntity(var1, var2, new ItemStack(var9));
               }
            }
         }
      }
   }

   public Item getSeedItem() {
      return this.crop == Blocks.pumpkin ? Items.pumpkin_seeds : (this.crop == Blocks.melon_block ? Items.melon_seeds : null);
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return null;
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      Item var6 = this.getSeedItem();
      return var6 != null ? var6 : null;
   }

   @Override
   public boolean canGrow(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return var3.getValue(AGE) != 7;
   }

   @Override
   public boolean canUseBonemeal(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   @Override
   public void grow(World var1, Random var2, BlockPos var3, IBlockState var4) {
      this.growStem(var1, var3, var4);
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
      return new BlockState(this, AGE, FACING);
   }
}
