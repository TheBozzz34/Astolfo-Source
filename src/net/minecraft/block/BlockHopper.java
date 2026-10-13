package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHopper extends BlockContainer {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
      private static final long a = 91987971606330L;

      public boolean apply(EnumFacing var1) {
         return var1 != EnumFacing.UP;
      }
   });
   public static final PropertyBool ENABLED = PropertyBool.create("enabled");
   private static final long e = 68582706379181L;

   public BlockHopper() {
      super(Material.iron, MapColor.stoneColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN).withProperty(ENABLED, true));
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void addCollisionBoxesToList(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      float var7 = 0.125F;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, var7, 1.0F, 1.0F);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var7);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      this.setBlockBounds(1.0F - var7, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      this.setBlockBounds(0.0F, 0.0F, 1.0F - var7, 1.0F, 1.0F, 1.0F);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      EnumFacing var12 = var3.getOpposite();
      if (var12 == EnumFacing.UP) {
         var12 = EnumFacing.DOWN;
      }

      return this.getDefaultState().withProperty(FACING, var12).withProperty(ENABLED, true);
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityHopper();
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      super.onBlockPlacedBy(var1, var2, var3, var4, var5);
      if (var5.hasDisplayName()) {
         TileEntity var9 = var1.getTileEntity(var2);
         if (var9 instanceof TileEntityHopper) {
            ((TileEntityHopper)var9).setCustomName(var5.getDisplayName());
         }
      }
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      this.updateState(var1, var2, var3);
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.isRemote) {
         return true;
      }

      TileEntity var12 = var1.getTileEntity(var2);
      if (var12 instanceof TileEntityHopper) {
         var4.displayGUIChest((TileEntityHopper)var12);
         var4.triggerAchievement(StatList.field_181732_P);
      }

      return true;
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      this.updateState(var1, var2, var3);
   }

   private void updateState(World var1, BlockPos var2, IBlockState var3) {
      boolean var7 = !var1.isBlockPowered(var2);
      if (var7 != var3.getValue(ENABLED)) {
         var1.setBlockState(var2, var3.withProperty(ENABLED, var7), 4);
      }
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      TileEntity var7 = var1.getTileEntity(var2);
      if (var7 instanceof TileEntityHopper) {
         InventoryHelper.dropInventoryItems(var1, var2, (TileEntityHopper)var7);
         var1.updateComparatorOutputLevel(var2, this);
      }

      super.breakBlock(var1, var2, var3);
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return true;
   }

   public static EnumFacing getFacing(int var0) {
      return EnumFacing.getFront(var0 & 7);
   }

   public static boolean isEnabled(int var0) {
      return (var0 & 8) != 8;
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public int getComparatorInputOverride(World var1, BlockPos var2) {
      return Container.calcRedstone(var1.getTileEntity(var2));
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT_MIPPED;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(FACING, getFacing(var1)).withProperty(ENABLED, isEnabled(var1));
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(FACING).getIndex();
      if (!var1.getValue(ENABLED)) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, ENABLED);
   }
}
