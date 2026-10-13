package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWire extends Block {
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   public static final PropertyBool SUSPENDED = PropertyBool.create("suspended");
   public static final PropertyBool ATTACHED = PropertyBool.create("attached");
   public static final PropertyBool DISARMED = PropertyBool.create("disarmed");
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   private static final long l = 25707111178436L;

   public BlockTripWire() {
      super(Material.circuits);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(POWERED, false)
            .withProperty(SUSPENDED, false)
            .withProperty(ATTACHED, false)
            .withProperty(DISARMED, false)
            .withProperty(NORTH, false)
            .withProperty(EAST, false)
            .withProperty(SOUTH, false)
            .withProperty(WEST, false)
      );
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
      this.setTickRandomly(true);
   }

   @Override
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.withProperty(NORTH, isConnectedTo(var2, var3, var1, EnumFacing.NORTH))
         .withProperty(EAST, isConnectedTo(var2, var3, var1, EnumFacing.EAST))
         .withProperty(SOUTH, isConnectedTo(var2, var3, var1, EnumFacing.SOUTH))
         .withProperty(WEST, isConnectedTo(var2, var3, var1, EnumFacing.WEST));
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      return null;
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
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.TRANSLUCENT;
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.string;
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Items.string;
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      boolean var8 = var3.getValue(SUSPENDED);
      boolean var9 = !World.doesBlockHaveSolidTopSurface(var1, var2.down());
      if (var8 != var9) {
         this.dropBlockAsItem(var1, var2, var3, 0);
         var1.setBlockToAir(var2);
      }
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      boolean var7 = var6.getValue(ATTACHED);
      boolean var8 = var6.getValue(SUSPENDED);
      if (!var8) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
      } else if (!var7) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      } else {
         this.setBlockBounds(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
      }
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      var3 = var3.withProperty(SUSPENDED, !World.doesBlockHaveSolidTopSurface(var1, var2.down()));
      var1.setBlockState(var2, var3, 3);
      this.notifyHook(var1, var2, var3);
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      this.notifyHook(var1, var2, var3.withProperty(POWERED, true));
   }

   @Override
   public void onBlockHarvested(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (!var1.isRemote && var4.getCurrentEquippedItem() != null && var4.getCurrentEquippedItem().getItem() == Items.shears) {
         var1.setBlockState(var2, var3.withProperty(DISARMED, true), 4);
      }
   }

   private void notifyHook(World var1, BlockPos var2, IBlockState var3) {
      for (EnumFacing var10 : new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.WEST}) {
         for (int var11 = 1; var11 < 42; var11++) {
            BlockPos var12 = var2.offset(var10, var11);
            IBlockState var13 = var1.getBlockState(var12);
            if (var13.getBlock() == Blocks.tripwire_hook) {
               if (var13.getValue(BlockTripWireHook.FACING) == var10.getOpposite()) {
                  Blocks.tripwire_hook.func_176260_a(var1, var12, var13, false, true, var11, var3);
               }
               break;
            }

            if (var13.getBlock() != Blocks.tripwire) {
               break;
            }
         }
      }
   }

   @Override
   public void onEntityCollidedWithBlock(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      if (!var1.isRemote && !var3.getValue(POWERED)) {
         this.updateState(var1, var2);
      }
   }

   @Override
   public void randomTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.isRemote && var1.getBlockState(var2).getValue(POWERED)) {
         this.updateState(var1, var2);
      }
   }

   private void updateState(World var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      boolean var7 = var6.getValue(POWERED);
      boolean var8 = false;
      List var9 = var1.getEntitiesWithinAABBExcludingEntity(
         (Entity)null,
         new AxisAlignedBB(
            var2.getX() + this.minX,
            var2.getY() + this.minY,
            var2.getZ() + this.minZ,
            var2.getX() + this.maxX,
            var2.getY() + this.maxY,
            var2.getZ() + this.maxZ
         )
      );
      if (!var9.isEmpty()) {
         for (Object var11$raw : var9) {
            Entity var11 = (Entity)var11$raw;
            if (!var11.doesEntityNotTriggerPressurePlate()) {
               var8 = true;
               break;
            }
         }
      }

      if (var8 != var7) {
         var6 = var6.withProperty(POWERED, var8);
         var1.setBlockState(var2, var6, 3);
         this.notifyHook(var1, var2, var6);
      }

      if (var8) {
         var1.scheduleUpdate(var2, this, this.tickRate(var1));
      }
   }

   public static boolean isConnectedTo(IBlockAccess var0, BlockPos var1, IBlockState var2, EnumFacing var3) {
      BlockPos var7 = var1.offset(var3);
      IBlockState var8 = var0.getBlockState(var7);
      Block var9 = var8.getBlock();
      if (var9 == Blocks.tripwire_hook) {
         EnumFacing var12 = var3.getOpposite();
         return var8.getValue(BlockTripWireHook.FACING) == var12;
      } else if (var9 == Blocks.tripwire) {
         boolean var10 = var2.getValue(SUSPENDED);
         boolean var11 = var8.getValue(SUSPENDED);
         return var10 == var11;
      } else {
         return false;
      }
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState()
         .withProperty(POWERED, (var1 & 1) > 0)
         .withProperty(SUSPENDED, (var1 & 2) > 0)
         .withProperty(ATTACHED, (var1 & 4) > 0)
         .withProperty(DISARMED, (var1 & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      byte var5 = 0;
      if (var1.getValue(POWERED)) {
         var5 |= 1;
      }

      if (var1.getValue(SUSPENDED)) {
         var5 |= 2;
      }

      if (var1.getValue(ATTACHED)) {
         var5 |= 4;
      }

      if (var1.getValue(DISARMED)) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, POWERED, SUSPENDED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
   }
}
