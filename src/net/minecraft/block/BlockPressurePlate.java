package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockPressurePlate extends BlockBasePressurePlate {
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   private final BlockPressurePlate.Sensitivity sensitivity;
   private static final long i = 38913928655762L;

   public BlockPressurePlate(Material var1, BlockPressurePlate.Sensitivity var2) {
      super(var1);
      this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, false));
      this.sensitivity = var2;
   }

   @Override
   public int getRedstoneStrength(IBlockState var1) {
      return var1.getValue(POWERED) ? 15 : 0;
   }

   @Override
   public IBlockState setRedstoneStrength(IBlockState var1, int var2) {
      return var1.withProperty(POWERED, var2 > 0);
   }

   @Override
   public int computeRedstoneStrength(World var1, BlockPos var2) {
      AxisAlignedBB var6 = this.getSensitiveAABB(var2);
      List var7;
      switch (this.sensitivity) {
         case EVERYTHING:
            var7 = var1.getEntitiesWithinAABBExcludingEntity((Entity)null, var6);
            break;
         case MOBS:
            var7 = var1.getEntitiesWithinAABB(EntityLivingBase.class, var6);
            break;
         default:
            return 0;
      }

      if (!var7.isEmpty()) {
         for (Object var9$raw : var7) {
            Entity var9 = (Entity)var9$raw;
            if (!var9.doesEntityNotTriggerPressurePlate()) {
               return 15;
            }
         }
      }

      return 0;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(POWERED, var1 == 1);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(POWERED) ? 1 : 0;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, POWERED);
   }

   public enum Sensitivity {
      EVERYTHING,
      MOBS;
   }
}
