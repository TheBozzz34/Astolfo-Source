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
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(SUSPENDED, !World.doesBlockHaveSolidTopSurface(worldIn, pos.down()));
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
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
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      return side.getAxis().isHorizontal() && worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock().isNormalCube();
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      for (Object object : EnumFacing.Plane.HORIZONTAL) {
         if (worldIn.getBlockState(pos.offset((EnumFacing)object)).getBlock().isNormalCube()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState iBlockState = this.getDefaultState().withProperty(POWERED, false).withProperty(ATTACHED, false).withProperty(SUSPENDED, false);
      if (facing.getAxis().isHorizontal()) {
         iBlockState = iBlockState.withProperty(FACING, facing);
      }

      return iBlockState;
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      this.func_176260_a(worldIn, pos, state, false, false, -1, (IBlockState)null);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (neighborBlock != this && this.checkForDrop(worldIn, pos, state)) {
         EnumFacing enumFacing = state.getValue(FACING);
         if (!worldIn.getBlockState(pos.offset(enumFacing.getOpposite())).getBlock().isNormalCube()) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         }
      }
   }

   public void func_176260_a(World worldIn, BlockPos pos, IBlockState hookState, boolean bl, boolean bl2, int i, IBlockState iBlockState) {
      long l = e ^ 61214910721470L;
      EnumFacing enumFacing = hookState.getValue(FACING);
      boolean bl3 = hookState.getValue(ATTACHED);
      boolean bl4 = hookState.getValue(POWERED);
      boolean bl5 = !World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
      boolean bl6 = !bl;
      boolean bl7 = false;
      long m = 8503730770967623031L ^ l;
      int j = 0;
      IBlockState[] iBlockStates = new IBlockState[42];

      for (int k = 1; k < 42; k++) {
         BlockPos blockPos = pos.offset(enumFacing, k);
         IBlockState iBlockState2 = worldIn.getBlockState(blockPos);
         if (iBlockState2.getBlock() == Blocks.tripwire_hook) {
            if (iBlockState2.getValue(FACING) == enumFacing.getOpposite()) {
               j = k;
            }
            break;
         }

         if (iBlockState2.getBlock() != Blocks.tripwire && k != i) {
            iBlockStates[k] = null;
            bl6 = false;
         } else {
            if (k == i) {
               iBlockState2 = Objects.firstNonNull(iBlockState, iBlockState2);
            }

            boolean bl8 = !iBlockState2.getValue(BlockTripWire.DISARMED);
            boolean bl9 = iBlockState2.getValue(BlockTripWire.POWERED);
            boolean bl10 = iBlockState2.getValue(BlockTripWire.SUSPENDED);
            bl6 &= bl10 == bl5;
            bl7 |= bl8 && bl9;
            iBlockStates[k] = iBlockState2;
            if (k == i) {
               worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
               bl6 &= bl8;
            }
         }
      }

      bl6 &= j > 1;
      bl7 &= bl6;
      IBlockState iBlockState3 = this.getDefaultState().withProperty(ATTACHED, bl6).withProperty(POWERED, bl7);
      if (j > 0) {
         BlockPos blockPos2 = pos.offset(enumFacing, j);
         EnumFacing enumFacing2 = enumFacing.getOpposite();
         worldIn.setBlockState(blockPos2, iBlockState3.withProperty(FACING, enumFacing2), 3);
         this.func_176262_b(worldIn, blockPos2, enumFacing2);
         this.func_180694_a(worldIn, blockPos2, bl6, bl7, bl3, bl4);
      }

      this.func_180694_a(worldIn, pos, bl6, bl7, bl3, bl4);
      if (!bl) {
         worldIn.setBlockState(pos, iBlockState3.withProperty(FACING, enumFacing), 3);
         if (bl2) {
            this.func_176262_b(worldIn, pos, enumFacing);
         }
      }

      if (bl3 != bl6) {
         for (int n = 1; n < j; n++) {
            BlockPos blockPos3 = pos.offset(enumFacing, n);
            IBlockState iBlockState4 = iBlockStates[n];
            if (iBlockState4 != null && worldIn.getBlockState(blockPos3).getBlock() != Blocks.air) {
               worldIn.setBlockState(blockPos3, iBlockState4.withProperty(ATTACHED, bl6), 3);
            }
         }
      }
   }

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random randomIn) {
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      this.func_176260_a(worldIn, pos, state, false, true, -1, (IBlockState)null);
   }

   private void func_180694_a(World worldIn, BlockPos pos, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
      if (bl2 && !bl4) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.click", 0.4F, 0.6F);
      } else if (!bl2 && bl4) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.click", 0.4F, 0.5F);
      } else if (bl && !bl3) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.click", 0.4F, 0.7F);
      } else if (!bl && bl3) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.bowhit", 0.4F, 1.2F / (worldIn.rand.nextFloat() * 0.2F + 0.9F));
      }
   }

   private void func_176262_b(World worldIn, BlockPos blockPos, EnumFacing enumFacing) {
      worldIn.notifyNeighborsOfStateChange(blockPos, this);
      worldIn.notifyNeighborsOfStateChange(blockPos.offset(enumFacing.getOpposite()), this);
   }

   private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.canPlaceBlockAt(worldIn, pos)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      float f = 0.1875F;
      switch ((EnumFacing)worldIn.getBlockState(pos).getValue(FACING)) {
         case EAST:
            this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
            break;
         case WEST:
            this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
            break;
         case SOUTH:
            this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
            break;
         case NORTH:
            this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
      }
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      boolean bl = state.getValue(ATTACHED);
      boolean bl2 = state.getValue(POWERED);
      if (bl || bl2) {
         this.func_176260_a(worldIn, pos, state, true, false, -1, (IBlockState)null);
      }

      if (bl2) {
         worldIn.notifyNeighborsOfStateChange(pos, this);
         worldIn.notifyNeighborsOfStateChange(pos.offset(state.getValue(FACING).getOpposite()), this);
      }

      super.breakBlock(worldIn, pos, state);
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return state.getValue(POWERED) ? 15 : 0;
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return !state.getValue(POWERED) ? 0 : (state.getValue(FACING) == side ? 15 : 0);
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
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(FACING, EnumFacing.getHorizontal(meta & 3))
         .withProperty(POWERED, (meta & 8) > 0)
         .withProperty(ATTACHED, (meta & 4) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i |= state.getValue(FACING).getHorizontalIndex();
      if (state.getValue(POWERED)) {
         i |= 8;
      }

      if (state.getValue(ATTACHED)) {
         i |= 4;
      }

      return i;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, POWERED, ATTACHED, SUSPENDED);
   }
}
