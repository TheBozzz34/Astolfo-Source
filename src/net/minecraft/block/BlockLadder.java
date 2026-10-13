package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLadder extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
   private static final long e = 28056332845837L;

   public BlockLadder() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      this.setBlockBoundsBasedOnState(var1, var2);
      return super.getCollisionBoundingBox(var1, var2, var3);
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World var1, BlockPos var2) {
      this.setBlockBoundsBasedOnState(var1, var2);
      return super.getSelectedBoundingBox(var1, var2);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      IBlockState var6 = var1.getBlockState(var2);
      if (var6.getBlock() == this) {
         float var7 = 0.125F;
         switch ((EnumFacing)var6.getValue(FACING)) {
            case NORTH:
               this.setBlockBounds(0.0F, 0.0F, 1.0F - var7, 1.0F, 1.0F, 1.0F);
               break;
            case SOUTH:
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var7);
               break;
            case WEST:
               this.setBlockBounds(1.0F - var7, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
               break;
            case EAST:
            default:
               this.setBlockBounds(0.0F, 0.0F, 0.0F, var7, 1.0F, 1.0F);
         }
      }
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
   public boolean canPlaceBlockAt(World var1, BlockPos var2) {
      return var1.getBlockState(var2.west()).getBlock().isNormalCube()
         ? true
         : (
            var1.getBlockState(var2.east()).getBlock().isNormalCube()
               ? true
               : (var1.getBlockState(var2.north()).getBlock().isNormalCube() ? true : var1.getBlockState(var2.south()).getBlock().isNormalCube())
         );
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      if (var3.getAxis().isHorizontal() && this.canBlockStay(var1, var2, var3)) {
         return this.getDefaultState().withProperty(FACING, var3);
      }

      for (Object var13 : EnumFacing.Plane.HORIZONTAL) {
         if (this.canBlockStay(var1, var2, (EnumFacing)var13)) {
            return this.getDefaultState().withProperty(FACING, (EnumFacing)var13);
         }
      }

      return this.getDefaultState();
   }

   @Override
   public void onNeighborBlockChange(World var1, BlockPos var2, IBlockState var3, Block var4) {
      EnumFacing var8 = var3.getValue(FACING);
      if (!this.canBlockStay(var1, var2, var8)) {
         this.dropBlockAsItem(var1, var2, var3, 0);
         var1.setBlockToAir(var2);
      }

      super.onNeighborBlockChange(var1, var2, var3, var4);
   }

   public boolean canBlockStay(World var1, BlockPos var2, EnumFacing var3) {
      return var1.getBlockState(var2.offset(var3.getOpposite())).getBlock().isNormalCube();
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
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
