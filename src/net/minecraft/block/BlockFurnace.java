package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockFurnace extends BlockContainer {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
   private final boolean isBurning;
   private static boolean keepInventory;
   private static final long e = 111170725760834L;

   public BlockFurnace(boolean var1) {
      super(Material.rock);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.isBurning = var1;
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Item.getItemFromBlock(Blocks.furnace);
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      this.y(var1, var2, var3);
   }

   private void y(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.isRemote) {
         Block var7 = var1.getBlockState(var2.north()).getBlock();
         Block var8 = var1.getBlockState(var2.south()).getBlock();
         Block var9 = var1.getBlockState(var2.west()).getBlock();
         Block var10 = var1.getBlockState(var2.east()).getBlock();
         EnumFacing var11 = var3.getValue(FACING);
         if (var11 == EnumFacing.NORTH && var7.isFullBlock() && !var8.isFullBlock()) {
            var11 = EnumFacing.SOUTH;
         } else if (var11 == EnumFacing.SOUTH && var8.isFullBlock() && !var7.isFullBlock()) {
            var11 = EnumFacing.NORTH;
         } else if (var11 == EnumFacing.WEST && var9.isFullBlock() && !var10.isFullBlock()) {
            var11 = EnumFacing.EAST;
         } else if (var11 == EnumFacing.EAST && var10.isFullBlock() && !var9.isFullBlock()) {
            var11 = EnumFacing.WEST;
         }

         var1.setBlockState(var2, var3.withProperty(FACING, var11), 2);
      }
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (this.isBurning) {
         EnumFacing var8 = var3.getValue(FACING);
         double var9 = var2.getX() + 0.5;
         double var11 = var2.getY() + var4.nextDouble() * 6.0 / 16.0;
         double var13 = var2.getZ() + 0.5;
         double var15 = 0.52;
         double var17 = var4.nextDouble() * 0.6 - 0.3;
         switch (var8) {
            case WEST:
               var1.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var9 - var15, var11, var13 + var17, 0.0, 0.0, 0.0);
               var1.spawnParticle(EnumParticleTypes.FLAME, var9 - var15, var11, var13 + var17, 0.0, 0.0, 0.0);
               break;
            case EAST:
               var1.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var9 + var15, var11, var13 + var17, 0.0, 0.0, 0.0);
               var1.spawnParticle(EnumParticleTypes.FLAME, var9 + var15, var11, var13 + var17, 0.0, 0.0, 0.0);
               break;
            case NORTH:
               var1.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var9 + var17, var11, var13 - var15, 0.0, 0.0, 0.0);
               var1.spawnParticle(EnumParticleTypes.FLAME, var9 + var17, var11, var13 - var15, 0.0, 0.0, 0.0);
               break;
            case SOUTH:
               var1.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var9 + var17, var11, var13 + var15, 0.0, 0.0, 0.0);
               var1.spawnParticle(EnumParticleTypes.FLAME, var9 + var17, var11, var13 + var15, 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.isRemote) {
         return true;
      }

      TileEntity var12 = var1.getTileEntity(var2);
      if (var12 instanceof TileEntityFurnace) {
         var4.displayGUIChest((TileEntityFurnace)var12);
         var4.triggerAchievement(StatList.field_181741_Y);
      }

      return true;
   }

   public static void setState(boolean var0, World var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      TileEntity var7 = var1.getTileEntity(var2);
      keepInventory = true;
      if (var0) {
         var1.setBlockState(var2, Blocks.lit_furnace.getDefaultState().withProperty(FACING, var6.getValue(FACING)), 3);
         var1.setBlockState(var2, Blocks.lit_furnace.getDefaultState().withProperty(FACING, var6.getValue(FACING)), 3);
      } else {
         var1.setBlockState(var2, Blocks.furnace.getDefaultState().withProperty(FACING, var6.getValue(FACING)), 3);
         var1.setBlockState(var2, Blocks.furnace.getDefaultState().withProperty(FACING, var6.getValue(FACING)), 3);
      }

      keepInventory = false;
      if (var7 != null) {
         var7.validate();
         var1.setTileEntity(var2, var7);
      }
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityFurnace();
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState().withProperty(FACING, var8.getHorizontalFacing().getOpposite());
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      var1.setBlockState(var2, var3.withProperty(FACING, var4.getHorizontalFacing().getOpposite()), 2);
      if (var5.hasDisplayName()) {
         TileEntity var9 = var1.getTileEntity(var2);
         if (var9 instanceof TileEntityFurnace) {
            ((TileEntityFurnace)var9).setCustomInventoryName(var5.getDisplayName());
         }
      }
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      if (!keepInventory) {
         TileEntity var7 = var1.getTileEntity(var2);
         if (var7 instanceof TileEntityFurnace) {
            InventoryHelper.dropInventoryItems(var1, var2, (TileEntityFurnace)var7);
            var1.updateComparatorOutputLevel(var2, this);
         }
      }

      super.breakBlock(var1, var2, var3);
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
   public Item getItem(World var1, BlockPos var2) {
      return Item.getItemFromBlock(Blocks.furnace);
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public IBlockState getStateForEntityRender(IBlockState var1) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
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
