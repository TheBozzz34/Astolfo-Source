package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockEnderChest extends BlockContainer {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
   private static final long e = 65418375989758L;
   private static final long i = 3949884996096884744L;

   public BlockEnderChest() {
      super(Material.rock);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
   public int getRenderType() {
      return 2;
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Item.getItemFromBlock(Blocks.obsidian);
   }

   @Override
   public int quantityDropped(Random var1) {
      return (int)i;
   }

   @Override
   public boolean canSilkHarvest() {
      return true;
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState().withProperty(FACING, var8.getHorizontalFacing().getOpposite());
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      var1.setBlockState(var2, var3.withProperty(FACING, var4.getHorizontalFacing().getOpposite()), 2);
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      InventoryEnderChest var12 = var4.getInventoryEnderChest();
      TileEntity var13 = var1.getTileEntity(var2);
      if (var12 == null || !(var13 instanceof TileEntityEnderChest)) {
         return true;
      }

      if (var1.getBlockState(var2.up()).getBlock().isNormalCube()) {
         return true;
      }

      if (var1.isRemote) {
         return true;
      }

      var12.setChestTileEntity((TileEntityEnderChest)var13);
      var4.displayGUIChest(var12);
      var4.triggerAchievement(StatList.field_181738_V);
      return true;
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityEnderChest();
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      for (int var8 = 0; var8 < 3; var8++) {
         int var9 = var4.nextInt(2) * 2 - 1;
         int var10 = var4.nextInt(2) * 2 - 1;
         double var11 = var2.getX() + 0.5 + 0.25 * var9;
         double var13 = var2.getY() + var4.nextFloat();
         double var15 = var2.getZ() + 0.5 + 0.25 * var10;
         double var17 = var4.nextFloat() * var9;
         double var19 = (var4.nextFloat() - 0.5) * 0.125;
         double var21 = var4.nextFloat() * var10;
         var1.spawnParticle(EnumParticleTypes.PORTAL, var11, var13, var15, var17, var19, var21);
      }
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      EnumFacing var5 = EnumFacing.getFront(var1);
      if (var5.getAxis() == EnumFacing.Axis.Y) {
         var5 = EnumFacing.NORTH;
      }

      return this.getDefaultState().withProperty(FACING, var5);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(FACING).getIndex();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING);
   }
}
