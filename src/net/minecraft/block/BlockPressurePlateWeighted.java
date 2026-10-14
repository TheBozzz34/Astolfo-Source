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

   public BlockPressurePlateWeighted(Material material, int i) {
      this(material, i, material.getMaterialMapColor());
   }

   public BlockPressurePlateWeighted(Material material, int i, MapColor mapColor) {
      super(material, mapColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, 0));
      this.field_150068_a = i;
   }

   @Override
   public int computeRedstoneStrength(World worldIn, BlockPos pos) {
      int i = Math.min(worldIn.getEntitiesWithinAABB(Entity.class, this.getSensitiveAABB(pos)).size(), this.field_150068_a);
      if (i > 0) {
         float f = (float)Math.min(this.field_150068_a, i) / this.field_150068_a;
         return MathHelper.ceiling_float_int(f * 15.0F);
      } else {
         return 0;
      }
   }

   @Override
   public int getRedstoneStrength(IBlockState state) {
      return state.getValue(POWER);
   }

   @Override
   public IBlockState setRedstoneStrength(IBlockState state, int strength) {
      return state.withProperty(POWER, strength);
   }

   @Override
   public int tickRate(World worldIn) {
      return 10;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(POWER, meta);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(POWER);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, POWER);
   }
}
