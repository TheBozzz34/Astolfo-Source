package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRailDetector extends BlockRailBase {
   public static final PropertyEnum<BlockRailBase.EnumRailDirection> SHAPE = PropertyEnum.create(
      "shape",
      BlockRailBase.EnumRailDirection.class,
      new Predicate<BlockRailBase.EnumRailDirection>() {
         private static final long a = 120744217100629L;

         public boolean apply(BlockRailBase.EnumRailDirection var1) {
            return var1 != BlockRailBase.EnumRailDirection.NORTH_EAST
               && var1 != BlockRailBase.EnumRailDirection.NORTH_WEST
               && var1 != BlockRailBase.EnumRailDirection.SOUTH_EAST
               && var1 != BlockRailBase.EnumRailDirection.SOUTH_WEST;
         }
      }
   );
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   private static final long i = 32102234342537L;

   public BlockRailDetector() {
      super(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, false).withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
      this.setTickRandomly(true);
   }

   @Override
   public int tickRate(World var1) {
      return 20;
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public void onEntityCollidedWithBlock(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      if (!var1.isRemote && !var3.getValue(POWERED)) {
         this.updatePoweredState(var1, var2, var3);
      }
   }

   @Override
   public void randomTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.isRemote && var3.getValue(POWERED)) {
         this.updatePoweredState(var1, var2, var3);
      }
   }

   @Override
   public int getWeakPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return var3.getValue(POWERED) ? 15 : 0;
   }

   @Override
   public int getStrongPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return !var3.getValue(POWERED) ? 0 : (var4 == EnumFacing.UP ? 15 : 0);
   }

   private void updatePoweredState(World var1, BlockPos var2, IBlockState var3) {
      boolean var7 = var3.getValue(POWERED);
      boolean var8 = false;
      List var9 = this.findMinecarts(var1, var2, EntityMinecart.class);
      if (!var9.isEmpty()) {
         var8 = true;
      }

      if (var8 && !var7) {
         var1.setBlockState(var2, var3.withProperty(POWERED, true), 3);
         var1.notifyNeighborsOfStateChange(var2, this);
         var1.notifyNeighborsOfStateChange(var2.down(), this);
         var1.markBlockRangeForRenderUpdate(var2, var2);
      }

      if (!var8 && var7) {
         var1.setBlockState(var2, var3.withProperty(POWERED, false), 3);
         var1.notifyNeighborsOfStateChange(var2, this);
         var1.notifyNeighborsOfStateChange(var2.down(), this);
         var1.markBlockRangeForRenderUpdate(var2, var2);
      }

      if (var8) {
         var1.scheduleUpdate(var2, this, this.tickRate(var1));
      }

      var1.updateComparatorOutputLevel(var2, this);
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      super.onBlockAdded(var1, var2, var3);
      this.updatePoweredState(var1, var2, var3);
   }

   @Override
   public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty() {
      return SHAPE;
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public int getComparatorInputOverride(World var1, BlockPos var2) {
      if (var1.getBlockState(var2).getValue(POWERED)) {
         List var6 = this.findMinecarts(var1, var2, EntityMinecartCommandBlock.class);
         if (!var6.isEmpty()) {
            return ((EntityMinecartCommandBlock)var6.get(0)).getCommandBlockLogic().getSuccessCount();
         }

         List var7 = this.findMinecarts(var1, var2, EntityMinecart.class, EntitySelectors.selectInventories);
         if (!var7.isEmpty()) {
            return Container.calcRedstoneFromInventory((IInventory)var7.get(0));
         }
      }

      return 0;
   }

   public <T extends EntityMinecart> List<T> findMinecarts(World var1, BlockPos var2, Class<T> var3, Predicate<Entity>... var4) {
      AxisAlignedBB var7 = this.getDectectionBox(var2);
      return var4.length != 1 ? var1.getEntitiesWithinAABB(var3, var7) : var1.getEntitiesWithinAABB(var3, var7, var4[0]);
   }

   private AxisAlignedBB getDectectionBox(BlockPos var1) {
      return new AxisAlignedBB(var1.getX() + 0.2F, var1.getY(), var1.getZ() + 0.2F, var1.getX() + 1 - 0.2F, var1.getY() + 1 - 0.2F, var1.getZ() + 1 - 0.2F);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(var1 & 7)).withProperty(POWERED, (var1 & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(SHAPE).getMetadata();
      if (var1.getValue(POWERED)) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, SHAPE, POWERED);
   }
}
