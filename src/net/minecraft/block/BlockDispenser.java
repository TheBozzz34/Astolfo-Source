package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.RegistryDefaulted;
import net.minecraft.world.World;

public class BlockDispenser extends BlockContainer {
   public static final PropertyDirection FACING = PropertyDirection.create("facing");
   public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");
   public static final RegistryDefaulted<Item, IBehaviorDispenseItem> dispenseBehaviorRegistry = new RegistryDefaulted<>(new BehaviorDefaultDispenseItem());
   public Random rand = new Random();
   private static final long e = 89447912875954L;

   public BlockDispenser() {
      super(Material.rock);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TRIGGERED, false));
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   @Override
   public int tickRate(World var1) {
      return 4;
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      super.onBlockAdded(var1, var2, var3);
      this.setDefaultDirection(var1, var2, var3);
   }

   private void setDefaultDirection(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.isRemote) {
         EnumFacing var7 = var3.getValue(FACING);
         boolean var8 = var1.getBlockState(var2.north()).getBlock().isFullBlock();
         boolean var9 = var1.getBlockState(var2.south()).getBlock().isFullBlock();
         if (var7 == EnumFacing.NORTH && var8 && !var9) {
            var7 = EnumFacing.SOUTH;
         } else if (var7 == EnumFacing.SOUTH && var9 && !var8) {
            var7 = EnumFacing.NORTH;
         } else {
            boolean var10 = var1.getBlockState(var2.west()).getBlock().isFullBlock();
            boolean var11 = var1.getBlockState(var2.east()).getBlock().isFullBlock();
            if (var7 == EnumFacing.WEST && var10 && !var11) {
               var7 = EnumFacing.EAST;
            } else if (var7 == EnumFacing.EAST && var11 && !var10) {
               var7 = EnumFacing.WEST;
            }
         }

         var1.setBlockState(var2, var3.withProperty(FACING, var7).withProperty(TRIGGERED, false), 2);
      }
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.isRemote) {
         return true;
      }

      TileEntity var12 = var1.getTileEntity(var2);
      if (var12 instanceof TileEntityDispenser) {
         var4.displayGUIChest((TileEntityDispenser)var12);
         if (var12 instanceof TileEntityDropper) {
            var4.triggerAchievement(StatList.field_181731_O);
         } else {
            var4.triggerAchievement(StatList.field_181733_Q);
         }
      }

      return true;
   }

   public void dispense(World var1, BlockPos var2) {
      BlockSourceImpl var6 = new BlockSourceImpl(var1, var2);
      TileEntityDispenser var7 = var6.getBlockTileEntity();
      if (var7 != null) {
         int var8 = var7.getDispenseSlot();
         if (var8 < 0) {
            var1.playAuxSFX(1001, var2, 0);
         } else {
            ItemStack var9 = var7.getStackInSlot(var8);
            IBehaviorDispenseItem var10 = this.getBehavior(var9);
            if (var10 != IBehaviorDispenseItem.itemDispenseBehaviorProvider) {
               ItemStack var11 = var10.dispense(var6, var9);
               var7.setInventorySlotContents(var8, var11.stackSize <= 0 ? null : var11);
            }
         }
      }
   }

   public IBehaviorDispenseItem getBehavior(ItemStack var1) {
      return dispenseBehaviorRegistry.getObject(var1 == null ? null : var1.getItem());
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      boolean var8 = var1.isBlockPowered(var2) || var1.isBlockPowered(var2.up());
      boolean var9 = var3.getValue(TRIGGERED);
      if (var8 && !var9) {
         var1.scheduleUpdate(var2, this, this.tickRate(var1));
         var1.setBlockState(var2, var3.withProperty(TRIGGERED, true), 4);
      } else if (!var8 && var9) {
         var1.setBlockState(var2, var3.withProperty(TRIGGERED, false), 4);
      }
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.isRemote) {
         this.dispense(var1, var2);
      }
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityDispenser();
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState().withProperty(FACING, BlockPistonBase.getFacingFromEntity(var1, var2, var8)).withProperty(TRIGGERED, false);
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      var1.setBlockState(var2, var3.withProperty(FACING, BlockPistonBase.getFacingFromEntity(var1, var2, var4)), 2);
      if (var5.hasDisplayName()) {
         TileEntity var9 = var1.getTileEntity(var2);
         if (var9 instanceof TileEntityDispenser) {
            ((TileEntityDispenser)var9).setCustomName(var5.getDisplayName());
         }
      }
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      TileEntity var7 = var1.getTileEntity(var2);
      if (var7 instanceof TileEntityDispenser) {
         InventoryHelper.dropInventoryItems(var1, var2, (TileEntityDispenser)var7);
         var1.updateComparatorOutputLevel(var2, this);
      }

      super.breakBlock(var1, var2, var3);
   }

   public static IPosition getDispensePosition(IBlockSource var0) {
      EnumFacing var1 = getFacing(var0.getBlockMetadata());
      double var2 = var0.getX() + 0.7 * var1.getFrontOffsetX();
      double var4 = var0.getY() + 0.7 * var1.getFrontOffsetY();
      double var6 = var0.getZ() + 0.7 * var1.getFrontOffsetZ();
      return new PositionImpl(var2, var4, var6);
   }

   public static EnumFacing getFacing(int var0) {
      return EnumFacing.getFront(var0 & 7);
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
   public int getRenderType() {
      return 3;
   }

   @Override
   public IBlockState getStateForEntityRender(IBlockState var1) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(FACING, getFacing(var1)).withProperty(TRIGGERED, (var1 & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(FACING).getIndex();
      if (var1.getValue(TRIGGERED)) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, TRIGGERED);
   }
}
