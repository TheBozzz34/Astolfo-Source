package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class BlockBrewingStand extends BlockContainer {
   public static final PropertyBool[] HAS_BOTTLE = new PropertyBool[]{
      PropertyBool.create("has_bottle_0"), PropertyBool.create("has_bottle_1"), PropertyBool.create("has_bottle_2")
   };
   private static final long e = 118653185188363L;

   public BlockBrewingStand() {
      super(Material.iron);
      this.setDefaultState(
         this.blockState.getBaseState().withProperty(HAS_BOTTLE[0], false).withProperty(HAS_BOTTLE[1], false).withProperty(HAS_BOTTLE[2], false)
      );
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal("item.brewingStand.name");
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityBrewingStand();
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public void addCollisionBoxesToList(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
      this.setBlockBoundsForItemRender();
      super.addCollisionBoxesToList(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.isRemote) {
         return true;
      }

      TileEntity var12 = var1.getTileEntity(var2);
      if (var12 instanceof TileEntityBrewingStand) {
         var4.displayGUIChest((TileEntityBrewingStand)var12);
         var4.triggerAchievement(StatList.field_181729_M);
      }

      return true;
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (var5.hasDisplayName()) {
         TileEntity var9 = var1.getTileEntity(var2);
         if (var9 instanceof TileEntityBrewingStand) {
            ((TileEntityBrewingStand)var9).setName(var5.getDisplayName());
         }
      }
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      double var5 = var2.getX() + 0.4F + var4.nextFloat() * 0.2F;
      double var7 = var2.getY() + 0.7F + var4.nextFloat() * 0.3F;
      double var9 = var2.getZ() + 0.4F + var4.nextFloat() * 0.2F;
      var1.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var5, var7, var9, 0.0, 0.0, 0.0);
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      TileEntity var7 = var1.getTileEntity(var2);
      if (var7 instanceof TileEntityBrewingStand) {
         InventoryHelper.dropInventoryItems(var1, var2, (TileEntityBrewingStand)var7);
      }

      super.breakBlock(var1, var2, var3);
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.brewing_stand;
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Items.brewing_stand;
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
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      IBlockState var5 = this.getDefaultState();

      for (int var6 = 0; var6 < 3; var6++) {
         var5 = var5.withProperty(HAS_BOTTLE[var6], (var1 & 1 << var6) > 0);
      }

      return var5;
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;

      for (int var6 = 0; var6 < 3; var6++) {
         if (var1.getValue(HAS_BOTTLE[var6])) {
            var5 |= 1 << var6;
         }
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2]);
   }
}
