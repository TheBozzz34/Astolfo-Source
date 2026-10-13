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
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      byte var7 = 4;
      int var8 = var7 + 1;
      if (var1.isAreaLoaded(var2.add(-var8, -var8, -var8), var2.add(var8, var8, var8))) {
         for (BlockPos var10 : BlockPos.getAllInBox(var2.add(-var7, -var7, -var7), var2.add(var7, var7, var7))) {
            IBlockState var11 = var1.getBlockState(var10);
            if (var11.getBlock().getMaterial() == Material.leaves && !var11.getValue(BlockLeaves.CHECK_DECAY)) {
               var1.setBlockState(var10, var11.withProperty(BlockLeaves.CHECK_DECAY, true), 4);
            }
         }
      }
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return super.onBlockPlaced(var1, var2, var3, var4, var5, var6, var7, var8).withProperty(LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(var3.getAxis()));
   }

   public enum EnumAxis implements IStringSerializable {
      X("x"),
      Y("y"),
      Z("z"),
      NONE("none");

      private final String name;
      private static final long a = 133766307492122L;

      EnumAxis(String var3) {
         this.name = var3;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockLog.EnumAxis fromFacingAxis(EnumFacing.Axis var0) {
         switch (var0) {
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
