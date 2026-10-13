package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class BlockChest extends BlockContainer {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
   public final int chestType;
   private static final long e = 81939578559680L;
   private static final long L = 2282472743536427023L;

   public BlockChest(int var1) {
      super(Material.wood);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.chestType = var1;
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
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      if (var1.getBlockState(var2.north()).getBlock() == this) {
         this.setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
      } else if (var1.getBlockState(var2.south()).getBlock() == this) {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
      } else if (var1.getBlockState(var2.west()).getBlock() == this) {
         this.setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
      } else if (var1.getBlockState(var2.east()).getBlock() == this) {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
      } else {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
      }
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      this.checkForSurroundingChests(var1, var2, var3);

      for (Object var8 : EnumFacing.Plane.HORIZONTAL) {
         BlockPos var9 = var2.offset((EnumFacing)var8);
         IBlockState var10 = var1.getBlockState(var9);
         if (var10.getBlock() == this) {
            this.checkForSurroundingChests(var1, var9, var10);
         }
      }
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState().withProperty(FACING, var8.getHorizontalFacing());
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      EnumFacing var9 = EnumFacing.getHorizontal(MathHelper.floor_double(var4.rotationYaw * 4.0F / 360.0F + 0.5) & 3).getOpposite();
      var3 = var3.withProperty(FACING, var9);
      BlockPos var10 = var2.north();
      BlockPos var11 = var2.south();
      BlockPos var12 = var2.west();
      BlockPos var13 = var2.east();
      boolean var14 = this == var1.getBlockState(var10).getBlock();
      boolean var15 = this == var1.getBlockState(var11).getBlock();
      boolean var16 = this == var1.getBlockState(var12).getBlock();
      boolean var17 = this == var1.getBlockState(var13).getBlock();
      if (!var14 && !var15 && !var16 && !var17) {
         var1.setBlockState(var2, var3, 3);
      } else if (var9.getAxis() != EnumFacing.Axis.X || !var14 && !var15) {
         if (var9.getAxis() == EnumFacing.Axis.Z && (var16 || var17)) {
            if (var16) {
               var1.setBlockState(var12, var3, 3);
            } else {
               var1.setBlockState(var13, var3, 3);
            }

            var1.setBlockState(var2, var3, 3);
         }
      } else {
         if (var14) {
            var1.setBlockState(var10, var3, 3);
         } else {
            var1.setBlockState(var11, var3, 3);
         }

         var1.setBlockState(var2, var3, 3);
      }

      if (var5.hasDisplayName()) {
         TileEntity var18 = var1.getTileEntity(var2);
         if (var18 instanceof TileEntityChest) {
            ((TileEntityChest)var18).setCustomName(var5.getDisplayName());
         }
      }
   }

   public IBlockState checkForSurroundingChests(World var1, BlockPos var2, IBlockState var3) {
      if (var1.isRemote) {
         return var3;
      }

      IBlockState var7 = var1.getBlockState(var2.north());
      IBlockState var8 = var1.getBlockState(var2.south());
      IBlockState var9 = var1.getBlockState(var2.west());
      IBlockState var10 = var1.getBlockState(var2.east());
      EnumFacing var11 = var3.getValue(FACING);
      Block var12 = var7.getBlock();
      Block var13 = var8.getBlock();
      Block var14 = var9.getBlock();
      Block var15 = var10.getBlock();
      if (var12 != this && var13 != this) {
         boolean var25 = var12.isFullBlock();
         boolean var26 = var13.isFullBlock();
         if (var14 == this || var15 == this) {
            BlockPos var27 = var14 == this ? var2.west() : var2.east();
            IBlockState var28 = var1.getBlockState(var27.north());
            IBlockState var29 = var1.getBlockState(var27.south());
            var11 = EnumFacing.SOUTH;
            EnumFacing var30;
            if (var14 == this) {
               var30 = var9.getValue(FACING);
            } else {
               var30 = var10.getValue(FACING);
            }

            if (var30 == EnumFacing.NORTH) {
               var11 = EnumFacing.NORTH;
            }

            Block var22 = var28.getBlock();
            Block var23 = var29.getBlock();
            if ((var25 || var22.isFullBlock()) && !var26 && !var23.isFullBlock()) {
               var11 = EnumFacing.SOUTH;
            }

            if ((var26 || var23.isFullBlock()) && !var25 && !var22.isFullBlock()) {
               var11 = EnumFacing.NORTH;
            }
         }
      } else {
         BlockPos var16 = var12 == this ? var2.north() : var2.south();
         IBlockState var17 = var1.getBlockState(var16.west());
         IBlockState var18 = var1.getBlockState(var16.east());
         var11 = EnumFacing.EAST;
         EnumFacing var19;
         if (var12 == this) {
            var19 = var7.getValue(FACING);
         } else {
            var19 = var8.getValue(FACING);
         }

         if (var19 == EnumFacing.WEST) {
            var11 = EnumFacing.WEST;
         }

         Block var20 = var17.getBlock();
         Block var21 = var18.getBlock();
         if ((var14.isFullBlock() || var20.isFullBlock()) && !var15.isFullBlock() && !var21.isFullBlock()) {
            var11 = EnumFacing.EAST;
         }

         if ((var15.isFullBlock() || var21.isFullBlock()) && !var14.isFullBlock() && !var20.isFullBlock()) {
            var11 = EnumFacing.WEST;
         }
      }

      var3 = var3.withProperty(FACING, var11);
      var1.setBlockState(var2, var3, 3);
      return var3;
   }

   public IBlockState correctFacing(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var7 = null;

      for (Object var9 : EnumFacing.Plane.HORIZONTAL) {
         IBlockState var10 = var1.getBlockState(var2.offset((EnumFacing)var9));
         if (var10.getBlock() == this) {
            return var3;
         }

         if (var10.getBlock().isFullBlock()) {
            if (var7 != null) {
               var7 = null;
               break;
            }

            var7 = (EnumFacing)var9;
         }
      }

      if (var7 != null) {
         return var3.withProperty(FACING, var7.getOpposite());
      }

      EnumFacing var11 = var3.getValue(FACING);
      if (var1.getBlockState(var2.offset(var11)).getBlock().isFullBlock()) {
         var11 = var11.getOpposite();
      }

      if (var1.getBlockState(var2.offset(var11)).getBlock().isFullBlock()) {
         var11 = var11.rotateY();
      }

      if (var1.getBlockState(var2.offset(var11)).getBlock().isFullBlock()) {
         var11 = var11.getOpposite();
      }

      return var3.withProperty(FACING, var11);
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      int var6 = 0;
      BlockPos var7 = var2.west();
      BlockPos var8 = var2.east();
      BlockPos var9 = var2.north();
      BlockPos var10 = var2.south();
      if (var1.getBlockState(var7).getBlock() == this) {
         if (this.isDoubleChest(var1, var7)) {
            return false;
         }

         var6++;
      }

      if (var1.getBlockState(var8).getBlock() == this) {
         if (this.isDoubleChest(var1, var8)) {
            return false;
         }

         var6++;
      }

      if (var1.getBlockState(var9).getBlock() == this) {
         if (this.isDoubleChest(var1, var9)) {
            return false;
         }

         var6++;
      }

      if (var1.getBlockState(var10).getBlock() == this) {
         if (this.isDoubleChest(var1, var10)) {
            return false;
         }

         var6++;
      }

      return var6 <= 1;
   }

   private boolean isDoubleChest(World var1, BlockPos var2) {
      if (var1.getBlockState(var2).getBlock() != this) {
         return false;
      }

      for (Object var7 : EnumFacing.Plane.HORIZONTAL) {
         if (var1.getBlockState(var2.offset((EnumFacing)var7)).getBlock() == this) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      super.onNeighborBlockChange(var1, var2, var3, var4);
      TileEntity var8 = var1.getTileEntity(var2);
      if (var8 instanceof TileEntityChest) {
         var8.updateContainingBlockInfo();
      }
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      TileEntity var7 = var1.getTileEntity(var2);
      if (var7 instanceof IInventory) {
         InventoryHelper.dropInventoryItems(var1, var2, (IInventory)var7);
         var1.updateComparatorOutputLevel(var2, this);
      }

      super.breakBlock(var1, var2, var3);
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.isRemote) {
         return true;
      }

      ILockableContainer var12 = this.getLockableContainer(var1, var2);
      if (var12 != null) {
         var4.displayGUIChest(var12);
         if (this.chestType == 0) {
            var4.triggerAchievement(StatList.field_181723_aa);
         } else if (this.chestType == 1) {
            var4.triggerAchievement(StatList.field_181737_U);
         }
      }

      return true;
   }

   public ILockableContainer getLockableContainer(World var1, BlockPos var2) {
      TileEntity var6 = var1.getTileEntity(var2);
      if (!(var6 instanceof TileEntityChest)) {
         return null;
      }

      IInteractionObject var7 = (TileEntityChest)var6;
      if (this.isBlocked(var1, var2)) {
         return null;
      }

      for (Object var9 : EnumFacing.Plane.HORIZONTAL) {
         BlockPos var10 = var2.offset((EnumFacing)var9);
         Block var11 = var1.getBlockState(var10).getBlock();
         if (var11 == this) {
            if (this.isBlocked(var1, var10)) {
               return null;
            }

            TileEntity var12 = var1.getTileEntity(var10);
            if (var12 instanceof TileEntityChest) {
               if (var9 != EnumFacing.WEST && var9 != EnumFacing.NORTH) {
                  var7 = new InventoryLargeChest("container.chestDouble", (net.minecraft.world.ILockableContainer)(var7), (TileEntityChest)var12);
               } else {
                  var7 = new InventoryLargeChest("container.chestDouble", (TileEntityChest)var12, (net.minecraft.world.ILockableContainer)(var7));
               }
            }
         }
      }

      return (net.minecraft.world.ILockableContainer)(var7);
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityChest();
   }

   @Override
   public boolean canProvidePower() {
      return this.chestType == 1;
   }

   @Override
   public int getWeakPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      if (!this.canProvidePower()) {
         return 0;
      }

      int var8 = 0;
      TileEntity var9 = var1.getTileEntity(var2);
      if (var9 instanceof TileEntityChest) {
         var8 = ((TileEntityChest)var9).numPlayersUsing;
      }

      return MathHelper.clamp_int(var8, 0, (int)L);
   }

   @Override
   public int getStrongPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return var4 == EnumFacing.UP ? this.getWeakPower(var1, var2, var3, var4) : 0;
   }

   private boolean isBlocked(World var1, BlockPos var2) {
      return this.isBelowSolidBlock(var1, var2) || this.isOcelotSittingOnChest(var1, var2);
   }

   private boolean isBelowSolidBlock(World var1, BlockPos var2) {
      return var1.getBlockState(var2.up()).getBlock().isNormalCube();
   }

   private boolean isOcelotSittingOnChest(World var1, BlockPos var2) {
      for (Entity var7 : var1.getEntitiesWithinAABB(
         EntityOcelot.class, new AxisAlignedBB(var2.getX(), var2.getY() + 1, var2.getZ(), var2.getX() + 1, var2.getY() + 2, var2.getZ() + 1)
      )) {
         EntityOcelot var8 = (EntityOcelot)var7;
         if (var8.isSitting()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public int getComparatorInputOverride(World var1, BlockPos var2) {
      return Container.calcRedstoneFromInventory(this.getLockableContainer(var1, var2));
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
