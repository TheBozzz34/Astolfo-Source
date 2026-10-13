package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockTorch extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
      private static final long a = 15643699118674L;

      public boolean apply(EnumFacing var1) {
         return var1 != EnumFacing.DOWN;
      }
   });
   private static final long l = 134180871794096L;

   public BlockTorch() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
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

   private boolean canPlaceOn(World var1, BlockPos var2) {
      if (World.doesBlockHaveSolidTopSurface(var1, var2)) {
         return true;
      }

      Block var6 = var1.getBlockState(var2).getBlock();
      return var6 instanceof BlockFence || var6 == Blocks.glass || var6 == Blocks.cobblestone_wall || var6 == Blocks.stained_glass;
   }

   @Override
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      for (EnumFacing var7 : FACING.getAllowedValues()) {
         if (this.canPlaceAt(var1, var2, var7)) {
            return true;
         }
      }

      return false;
   }

   private boolean canPlaceAt(World var1, BlockPos var2, EnumFacing var3) {
      BlockPos var7 = var2.offset(var3.getOpposite());
      boolean var8 = var3.getAxis().isHorizontal();
      return var8 && var1.isBlockNormalCube(var7, true) || var3.equals(EnumFacing.UP) && this.canPlaceOn(var1, var7);
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      if (this.canPlaceAt(var1, var2, var3)) {
         return this.getDefaultState().withProperty(FACING, var3);
      }

      for (Object var13 : EnumFacing.Plane.HORIZONTAL) {
         if (var1.isBlockNormalCube(var2.offset(((EnumFacing)var13).getOpposite()), true)) {
            return this.getDefaultState().withProperty(FACING, (EnumFacing)var13);
         }
      }

      return this.getDefaultState();
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      this.checkForDrop(var1, var2, var3);
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      this.onNeighborChangeInternal(var1, var2, var3);
   }

   public boolean onNeighborChangeInternal(World var1, BlockPos var2, IBlockState var3) {
      if (!this.checkForDrop(var1, var2, var3)) {
         return true;
      }

      EnumFacing var7 = var3.getValue(FACING);
      EnumFacing.Axis var8 = var7.getAxis();
      EnumFacing var9 = var7.getOpposite();
      boolean var10 = false;
      if (var8.isHorizontal() && !var1.isBlockNormalCube(var2.offset(var9), true)) {
         var10 = true;
      } else if (var8.isVertical() && !this.canPlaceOn(var1, var2.offset(var9))) {
         var10 = true;
      }

      if (var10) {
         this.dropBlockAsItem(var1, var2, var3, 0);
         var1.setBlockToAir(var2);
         return true;
      } else {
         return false;
      }
   }

   public boolean checkForDrop(World var1, BlockPos var2, IBlockState var3) {
      if (var3.getBlock() == this && this.canPlaceAt(var1, var2, var3.getValue(FACING))) {
         return true;
      }

      if (var1.getBlockState(var2).getBlock() == this) {
         this.dropBlockAsItem(var1, var2, var3, 0);
         var1.setBlockToAir(var2);
      }

      return false;
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      EnumFacing var8 = var1.getBlockState(var2).getValue(FACING);
      float var9 = 0.15F;
      if (var8 == EnumFacing.EAST) {
         this.setBlockBounds(0.0F, 0.2F, 0.5F - var9, var9 * 2.0F, 0.8F, 0.5F + var9);
      } else if (var8 == EnumFacing.WEST) {
         this.setBlockBounds(1.0F - var9 * 2.0F, 0.2F, 0.5F - var9, 1.0F, 0.8F, 0.5F + var9);
      } else if (var8 == EnumFacing.SOUTH) {
         this.setBlockBounds(0.5F - var9, 0.2F, 0.0F, 0.5F + var9, 0.8F, var9 * 2.0F);
      } else if (var8 == EnumFacing.NORTH) {
         this.setBlockBounds(0.5F - var9, 0.2F, 1.0F - var9 * 2.0F, 0.5F + var9, 0.8F, 1.0F);
      } else {
         var9 = 0.1F;
         this.setBlockBounds(0.5F - var9, 0.0F, 0.5F - var9, 0.5F + var9, 0.6F, 0.5F + var9);
      }

      return super.collisionRayTrace(var1, var2, var3, var4);
   }

   @Override
   public void randomDisplayTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      EnumFacing var8 = var3.getValue(FACING);
      double var9 = var2.getX() + 0.5;
      double var11 = var2.getY() + 0.7;
      double var13 = var2.getZ() + 0.5;
      double var15 = 0.22;
      double var17 = 0.27;
      if (var8.getAxis().isHorizontal()) {
         EnumFacing var19 = var8.getOpposite();
         var1.spawnParticle(
            EnumParticleTypes.SMOKE_NORMAL, var9 + var17 * var19.getFrontOffsetX(), var11 + var15, var13 + var17 * var19.getFrontOffsetZ(), 0.0, 0.0, 0.0
         );
         var1.spawnParticle(
            EnumParticleTypes.FLAME, var9 + var17 * var19.getFrontOffsetX(), var11 + var15, var13 + var17 * var19.getFrontOffsetZ(), 0.0, 0.0, 0.0
         );
      } else {
         var1.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var9, var11, var13, 0.0, 0.0, 0.0);
         var1.spawnParticle(EnumParticleTypes.FLAME, var9, var11, var13, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      IBlockState var5 = this.getDefaultState();
      switch (var1) {
         case 1:
            var5 = var5.withProperty(FACING, EnumFacing.EAST);
            break;
         case 2:
            var5 = var5.withProperty(FACING, EnumFacing.WEST);
            break;
         case 3:
            var5 = var5.withProperty(FACING, EnumFacing.SOUTH);
            break;
         case 4:
            var5 = var5.withProperty(FACING, EnumFacing.NORTH);
            break;
         case 5:
         default:
            var5 = var5.withProperty(FACING, EnumFacing.UP);
      }

      return var5;
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      switch ((EnumFacing)var1.getValue(FACING)) {
         case EAST:
            var5 |= 1;
            break;
         case WEST:
            var5 |= 2;
            break;
         case SOUTH:
            var5 |= 3;
            break;
         case NORTH:
            var5 |= 4;
            break;
         case DOWN:
         case UP:
         default:
            var5 |= 5;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING);
   }
}
