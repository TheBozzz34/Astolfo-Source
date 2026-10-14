package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public abstract class BlockLog extends BlockRotatedPillar {
   public static final PropertyEnum<BlockLog.EnumAxis> LOG_AXIS = PropertyEnum.create("axis", BlockLog.EnumAxis.class);
   private static final long i = 67573011504003L;

   public BlockLog() {
      super(Material.wood);
      this.setCreativeTab(CreativeTabs.tabBlock);
      this.setHardness(2.0F);
      this.setStepSound(soundTypeWood);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      byte b = 4;
      int i = b + 1;
      if (worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
         for (BlockPos blockPos : BlockPos.getAllInBox(pos.add(-b, -b, -b), pos.add(b, b, b))) {
            IBlockState iBlockState = worldIn.getBlockState(blockPos);
            if (iBlockState.getBlock().getMaterial() == Material.leaves && !iBlockState.getValue(BlockLeaves.CHECK_DECAY)) {
               worldIn.setBlockState(blockPos, iBlockState.withProperty(BlockLeaves.CHECK_DECAY, true), 4);
            }
         }
      }
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
         .withProperty(LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()));
   }

   public enum EnumAxis implements IStringSerializable {
      X("x"),
      Y("y"),
      Z("z"),
      NONE("none");

      private final String name;
      private static final long a = 133766307492122L;

      EnumAxis(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockLog.EnumAxis fromFacingAxis(EnumFacing.Axis axis) {
         switch (axis) {
            case X:
               return X;
            case Y:
               return Y;
            case Z:
               return Z;
            default:
               return NONE;
         }
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
