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
   public int tickRate(World worldIn) {
      return 4;
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      super.onBlockAdded(worldIn, pos, state);
      this.setDefaultDirection(worldIn, pos, state);
   }

   private void setDefaultDirection(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         EnumFacing enumFacing = state.getValue(FACING);
         boolean bl = worldIn.getBlockState(pos.north()).getBlock().isFullBlock();
         boolean bl2 = worldIn.getBlockState(pos.south()).getBlock().isFullBlock();
         if (enumFacing == EnumFacing.NORTH && bl && !bl2) {
            enumFacing = EnumFacing.SOUTH;
         } else if (enumFacing == EnumFacing.SOUTH && bl2 && !bl) {
            enumFacing = EnumFacing.NORTH;
         } else {
            boolean bl3 = worldIn.getBlockState(pos.west()).getBlock().isFullBlock();
            boolean bl4 = worldIn.getBlockState(pos.east()).getBlock().isFullBlock();
            if (enumFacing == EnumFacing.WEST && bl3 && !bl4) {
               enumFacing = EnumFacing.EAST;
            } else if (enumFacing == EnumFacing.EAST && bl4 && !bl3) {
               enumFacing = EnumFacing.WEST;
            }
         }

         worldIn.setBlockState(pos, state.withProperty(FACING, enumFacing).withProperty(TRIGGERED, false), 2);
      }
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityDispenser) {
         playerIn.displayGUIChest((TileEntityDispenser)tileEntity);
         if (tileEntity instanceof TileEntityDropper) {
            playerIn.triggerAchievement(StatList.field_181731_O);
         } else {
            playerIn.triggerAchievement(StatList.field_181733_Q);
         }
      }

      return true;
   }

   public void dispense(World worldIn, BlockPos pos) {
      BlockSourceImpl blockSourceImpl = new BlockSourceImpl(worldIn, pos);
      TileEntityDispenser tileEntityDispenser = blockSourceImpl.getBlockTileEntity();
      if (tileEntityDispenser != null) {
         int i = tileEntityDispenser.getDispenseSlot();
         if (i < 0) {
            worldIn.playAuxSFX(1001, pos, 0);
         } else {
            ItemStack itemStack = tileEntityDispenser.getStackInSlot(i);
            IBehaviorDispenseItem iBehaviorDispenseItem = this.getBehavior(itemStack);
            if (iBehaviorDispenseItem != IBehaviorDispenseItem.itemDispenseBehaviorProvider) {
               ItemStack itemStack2 = iBehaviorDispenseItem.dispense(blockSourceImpl, itemStack);
               tileEntityDispenser.setInventorySlotContents(i, itemStack2.stackSize <= 0 ? null : itemStack2);
            }
         }
      }
   }

   public IBehaviorDispenseItem getBehavior(ItemStack stack) {
      return dispenseBehaviorRegistry.getObject(stack == null ? null : stack.getItem());
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      boolean bl = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
      boolean bl2 = state.getValue(TRIGGERED);
      if (bl && !bl2) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
         worldIn.setBlockState(pos, state.withProperty(TRIGGERED, true), 4);
      } else if (!bl && bl2) {
         worldIn.setBlockState(pos, state.withProperty(TRIGGERED, false), 4);
      }
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         this.dispense(worldIn, pos);
      }
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityDispenser();
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, BlockPistonBase.getFacingFromEntity(worldIn, pos, placer)).withProperty(TRIGGERED, false);
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      worldIn.setBlockState(pos, state.withProperty(FACING, BlockPistonBase.getFacingFromEntity(worldIn, pos, placer)), 2);
      if (stack.hasDisplayName()) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof TileEntityDispenser) {
            ((TileEntityDispenser)tileEntity).setCustomName(stack.getDisplayName());
         }
      }
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityDispenser) {
         InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityDispenser)tileEntity);
         worldIn.updateComparatorOutputLevel(pos, this);
      }

      super.breakBlock(worldIn, pos, state);
   }

   public static IPosition getDispensePosition(IBlockSource coords) {
      EnumFacing enumFacing = getFacing(coords.getBlockMetadata());
      double d = coords.getX() + 0.7 * enumFacing.getFrontOffsetX();
      double e = coords.getY() + 0.7 * enumFacing.getFrontOffsetY();
      double d2 = coords.getZ() + 0.7 * enumFacing.getFrontOffsetZ();
      return new PositionImpl(d, e, d2);
   }

   public static EnumFacing getFacing(int meta) {
      return EnumFacing.getFront(meta & 7);
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public int getComparatorInputOverride(World worldIn, BlockPos pos) {
      return Container.calcRedstone(worldIn.getTileEntity(pos));
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public IBlockState getStateForEntityRender(IBlockState state) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(TRIGGERED, (meta & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i |= state.getValue(FACING).getIndex();
      if (state.getValue(TRIGGERED)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, TRIGGERED);
   }
}
