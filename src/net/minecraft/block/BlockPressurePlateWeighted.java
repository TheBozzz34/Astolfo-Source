package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {
   public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
   private final int field_150068_a;
   private static final long i = 59702308586741L;

   public BlockPressurePlateWeighted(Material var1, int var2) {
      this(var1, var2, var1.getMaterialMapColor());
   }

   public BlockPressurePlateWeighted(Material var1, int var2, MapColor var3) {
      super(var1, var3);
      this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, 0));
      this.field_150068_a = var2;
   }

   @Override
   public int computeRedstoneStrength(World var1, BlockPos var2) {
      int var6 = Math.min(var1.getEntitiesWithinAABB(Entity.class, this.getSensitiveAABB(var2)).size(), this.field_150068_a);
      if (var6 > 0) {
         float var7 = (float)Math.min(this.field_150068_a, var6) / this.field_150068_a;
         return MathHelper.ceiling_float_int(var7 * 15.0F);
      } else {
         return 0;
      }
   }

   @Override
   public int getRedstoneStrength(IBlockState var1) {
      return var1.getValue(POWER);
   }

   @Override
   public IBlockState setRedstoneStrength(IBlockState var1, int var2) {
      return var1.withProperty(POWER, var2);
   }

   @Override
   public int tickRate(World var1) {
      return 10;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(POWER, var1);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(POWER);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, POWER);
   }
}
