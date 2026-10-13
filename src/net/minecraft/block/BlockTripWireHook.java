package net.minecraft.block;

import com.google.common.base.Objects;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWireHook extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   public static final PropertyBool ATTACHED = PropertyBool.create("attached");
   public static final PropertyBool SUSPENDED = PropertyBool.create("suspended");
   private static final long e = 46028017265276L;

   public BlockTripWireHook() {
      super(Material.circuits);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(POWERED, false)
            .withProperty(ATTACHED, false)
            .withProperty(SUSPENDED, false)
      );
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setTickRandomly(true);
   }

   @Override
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.withProperty(SUSPENDED, !World.doesBlockHaveSolidTopSurface(var2, var3.down()));
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
   public boolean canPlaceBlockOnSide(World var1, BlockPos var2, EnumFacing var3) {
      return var3.getAxis().isHorizontal() && var1.getBlockState(var2.offset(var3.getOpposite())).getBlock().isNormalCube();
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      for (Object var7 : EnumFacing.Plane.HORIZONTAL) {
         if (var1.getBlockState(var2.offset((EnumFacing)var7)).getBlock().isNormalCube()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      IBlockState var11 = this.getDefaultState().withProperty(POWERED, false).withProperty(ATTACHED, false).withProperty(SUSPENDED, false);
      if (var3.getAxis().isHorizontal()) {
         var11 = var11.withProperty(FACING, var3);
      }

      return var11;
   }

   @Override
   public void onBlockPlacedBy(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      this.func_176260_a(var1, var2, var3, false, false, -1, (IBlockState)null);
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (var4 != this && this.checkForDrop(var1, var2, var3)) {
         EnumFacing var8 = var3.getValue(FACING);
         if (!var1.getBlockState(var2.offset(var8.getOpposite())).getBlock().isNormalCube()) {
            this.dropBlockAsItem(var1, var2, var3, 0);
            var1.setBlockToAir(var2);
         }
      }
   }

   public void func_176260_a(World var1, BlockPos var2, IBlockState var3, boolean var4, boolean var5, int var6, IBlockState var7) {
      long var8 = e ^ 61214910721470L;
      EnumFacing var11 = var3.getValue(FACING);
      boolean var12 = var3.getValue(ATTACHED);
      boolean var13 = var3.getValue(POWERED);
      boolean var14 = !World.doesBlockHaveSolidTopSurface(var1, var2.down());
      boolean var15 = !var4;
      boolean var16 = false;
      long var10000 = 8503730770967623031L ^ var8;
      int var17 = 0;
      IBlockState[] var18 = new IBlockState[42];

      for (int var19 = 1; var19 < 42; var19++) {
         BlockPos var20 = var2.offset(var11, var19);
         IBlockState var21 = var1.getBlockState(var20);
         if (var21.getBlock() == Blocks.tripwire_hook) {
            if (var21.getValue(FACING) == var11.getOpposite()) {
               var17 = var19;
            }
            break;
         }

         if (var21.getBlock() != Blocks.tripwire && var19 != var6) {
            var18[var19] = null;
            var15 = false;
         } else {
            if (var19 == var6) {
               var21 = Objects.firstNonNull(var7, var21);
            }

            boolean var22 = !var21.getValue(BlockTripWire.DISARMED);
            boolean var23 = var21.getValue(BlockTripWire.POWERED);
            boolean var24 = var21.getValue(BlockTripWire.SUSPENDED);
            var15 &= var24 == var14;
            var16 |= var22 && var23;
            var18[var19] = var21;
            if (var19 == var6) {
               var1.scheduleUpdate(var2, this, this.tickRate(var1));
               var15 &= var22;
            }
         }
      }

      var15 &= var17 > 1;
      var16 &= var15;
      IBlockState var27 = this.getDefaultState().withProperty(ATTACHED, var15).withProperty(POWERED, var16);
      if (var17 > 0) {
         BlockPos var28 = var2.offset(var11, var17);
         EnumFacing var30 = var11.getOpposite();
         var1.setBlockState(var28, var27.withProperty(FACING, var30), 3);
         this.func_176262_b(var1, var28, var30);
         this.func_180694_a(var1, var28, var15, var16, var12, var13);
      }

      this.func_180694_a(var1, var2, var15, var16, var12, var13);
      if (!var4) {
         var1.setBlockState(var2, var27.withProperty(FACING, var11), 3);
         if (var5) {
            this.func_176262_b(var1, var2, var11);
         }
      }

      if (var12 != var15) {
         for (int var29 = 1; var29 < var17; var29++) {
            BlockPos var31 = var2.offset(var11, var29);
            IBlockState var32 = var18[var29];
            if (var32 != null && var1.getBlockState(var31).getBlock() != Blocks.air) {
               var1.setBlockState(var31, var32.withProperty(ATTACHED, var15), 3);
            }
         }
      }
   }

   @Override
   public void randomTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      this.func_176260_a(var1, var2, var3, false, true, -1, (IBlockState)null);
   }

   private void func_180694_a(World var1, BlockPos var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      if (var4 && !var6) {
         var1.playSoundEffect(var2.getX() + 0.5, var2.getY() + 0.1, var2.getZ() + 0.5, "random.click", 0.4F, 0.6F);
      } else if (!var4 && var6) {
         var1.playSoundEffect(var2.getX() + 0.5, var2.getY() + 0.1, var2.getZ() + 0.5, "random.click", 0.4F, 0.5F);
      } else if (var3 && !var5) {
         var1.playSoundEffect(var2.getX() + 0.5, var2.getY() + 0.1, var2.getZ() + 0.5, "random.click", 0.4F, 0.7F);
      } else if (!var3 && var5) {
         var1.playSoundEffect(var2.getX() + 0.5, var2.getY() + 0.1, var2.getZ() + 0.5, "random.bowhit", 0.4F, 1.2F / (var1.rand.nextFloat() * 0.2F + 0.9F));
      }
   }

   private void func_176262_b(World var1, BlockPos var2, EnumFacing var3) {
      var1.notifyNeighborsOfStateChange(var2, this);
      var1.notifyNeighborsOfStateChange(var2.offset(var3.getOpposite()), this);
   }

   private boolean checkForDrop(World var1, BlockPos var2, IBlockState var3) {
      if (!this.canPlaceBlockAt(var1, var2)) {
         this.dropBlockAsItem(var1, var2, var3, 0);
         var1.setBlockToAir(var2);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      float var6 = 0.1875F;
      switch ((EnumFacing)var1.getBlockState(var2).getValue(FACING)) {
         case EAST:
            this.setBlockBounds(0.0F, 0.2F, 0.5F - var6, var6 * 2.0F, 0.8F, 0.5F + var6);
            break;
         case WEST:
            this.setBlockBounds(1.0F - var6 * 2.0F, 0.2F, 0.5F - var6, 1.0F, 0.8F, 0.5F + var6);
            break;
         case SOUTH:
            this.setBlockBounds(0.5F - var6, 0.2F, 0.0F, 0.5F + var6, 0.8F, var6 * 2.0F);
            break;
         case NORTH:
            this.setBlockBounds(0.5F - var6, 0.2F, 1.0F - var6 * 2.0F, 0.5F + var6, 0.8F, 1.0F);
      }
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      boolean var7 = var3.getValue(ATTACHED);
      boolean var8 = var3.getValue(POWERED);
      if (var7 || var8) {
         this.func_176260_a(var1, var2, var3, true, false, -1, (IBlockState)null);
      }

      if (var8) {
         var1.notifyNeighborsOfStateChange(var2, this);
         var1.notifyNeighborsOfStateChange(var2.offset(var3.getValue(FACING).getOpposite()), this);
      }

      super.breakBlock(var1, var2, var3);
   }

   @Override
   public int getWeakPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return var3.getValue(POWERED) ? 15 : 0;
   }

   @Override
   public int getStrongPower(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return !var3.getValue(POWERED) ? 0 : (var3.getValue(FACING) == var4 ? 15 : 0);
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT_MIPPED;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState()
         .withProperty(FACING, EnumFacing.getHorizontal(var1 & 3))
         .withProperty(POWERED, (var1 & 8) > 0)
         .withProperty(ATTACHED, (var1 & 4) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(FACING).getHorizontalIndex();
      if (var1.getValue(POWERED)) {
         var5 |= 8;
      }

      if (var1.getValue(ATTACHED)) {
         var5 |= 4;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, POWERED, ATTACHED, SUSPENDED);
   }
}
