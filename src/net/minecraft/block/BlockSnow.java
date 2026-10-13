package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSnow extends Block {
   public static final PropertyInteger LAYERS = PropertyInteger.create("layers", 1, 8);
   private static final long e = 40573588340648L;

   public BlockSnow() {
      super(Material.snow);
      this.setDefaultState(this.blockState.getBaseState().withProperty(LAYERS, 1));
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setBlockBoundsForItemRender();
   }

   @Override
   public boolean isPassable(IBlockAccess var1, BlockPos var2) {
      return var1.getBlockState(var2).getValue(LAYERS) < 5;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      int var4 = var3.getValue(LAYERS) - 1;
      float var5 = 0.125F;
      return new AxisAlignedBB(
         var2.getX() + this.minX, var2.getY() + this.minY, var2.getZ() + this.minZ, var2.getX() + this.maxX, var2.getY() + var4 * var5, var2.getZ() + this.maxZ
      );
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.getBoundsForLayers(0);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      IBlockState var3 = var1.getBlockState(var2);
      this.getBoundsForLayers(var3.getValue(LAYERS));
   }

   public void getBoundsForLayers(int var1) {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, var1 / 8.0F, 1.0F);
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2.down());
      Block var7 = var6.getBlock();
      return var7 != Blocks.ice && var7 != Blocks.packed_ice
         ? (
            var7.getMaterial() == Material.leaves
               ? true
               : (var7 == this && var6.getValue(LAYERS) >= 7 ? true : var7.isOpaqueCube() && var7.blockMaterial.blocksMovement())
         )
         : false;
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      this.checkAndDropBlock(var1, var2, var3);
   }

   private boolean checkAndDropBlock(World var1, BlockPos var2, IBlockState var3) {
      if (!this.canPlaceBlockAt(var1, var2)) {
         this.dropBlockAsItem(var1, var2, var3, 0);
         var1.setBlockToAir(var2);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public void harvestBlock(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      spawnAsEntity(var1, var3, new ItemStack(Items.snowball, var4.getValue(LAYERS) + 1, 0));
      var1.setBlockToAir(var3);
      var2.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.snowball;
   }

   @Override
   public int quantityDropped(Random var1) {
      return 0;
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var1.getLightFor(EnumSkyBlock.BLOCK, var2) > 11) {
         this.dropBlockAsItem(var1, var2, var1.getBlockState(var2), 0);
         var1.setBlockToAir(var2);
      }
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var3 == EnumFacing.UP ? true : super.shouldSideBeRendered(var1, var2, var3);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(LAYERS, (var1 & 7) + 1);
   }

   @Override
   public boolean isReplaceable(World var1, BlockPos var2) {
      return var1.getBlockState(var2).getValue(LAYERS) == 1;
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(LAYERS) - 1;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, LAYERS);
   }
}
