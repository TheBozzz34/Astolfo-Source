package net.minecraft.world.gen.feature;

import com.google.common.base.Predicates;
import java.util.Random;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenDesertWells extends WorldGenerator {
   private static final BlockStateHelper field_175913_a = BlockStateHelper.forBlock(Blocks.sand)
      .where(BlockSand.VARIANT, Predicates.equalTo(BlockSand.EnumType.SAND));
   private final IBlockState field_175911_b = Blocks.stone_slab
      .getDefaultState()
      .withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SAND)
      .withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM);
   private final IBlockState field_175912_c = Blocks.sandstone.getDefaultState();
   private final IBlockState field_175910_d = Blocks.flowing_water.getDefaultState();
   private static final long a = 64668807484802L;

   @Override
   public boolean generate(World var1, Random var2, BlockPos var3) {
      while (var1.isAirBlock(var3) && var3.getY() > 2) {
         var3 = var3.down();
      }

      if (!field_175913_a.apply(var1.getBlockState(var3))) {
         return false;
      }

      for (int var7 = -2; var7 <= 2; var7++) {
         for (int var8 = -2; var8 <= 2; var8++) {
            if (var1.isAirBlock(var3.add(var7, -1, var8)) && var1.isAirBlock(var3.add(var7, -2, var8))) {
               return false;
            }
         }
      }

      for (int var10 = -1; var10 <= 0; var10++) {
         for (int var15 = -2; var15 <= 2; var15++) {
            for (int var9 = -2; var9 <= 2; var9++) {
               var1.setBlockState(var3.add(var15, var10, var9), this.field_175912_c, 2);
            }
         }
      }

      var1.setBlockState(var3, this.field_175910_d, 2);

      for (Object var16 : EnumFacing.Plane.HORIZONTAL) {
         var1.setBlockState(var3.offset((EnumFacing)var16), this.field_175910_d, 2);
      }

      for (int var12 = -2; var12 <= 2; var12++) {
         for (int var17 = -2; var17 <= 2; var17++) {
            if (var12 == -2 || var12 == 2 || var17 == -2 || var17 == 2) {
               var1.setBlockState(var3.add(var12, 1, var17), this.field_175912_c, 2);
            }
         }
      }

      var1.setBlockState(var3.add(2, 1, 0), this.field_175911_b, 2);
      var1.setBlockState(var3.add(-2, 1, 0), this.field_175911_b, 2);
      var1.setBlockState(var3.add(0, 1, 2), this.field_175911_b, 2);
      var1.setBlockState(var3.add(0, 1, -2), this.field_175911_b, 2);

      for (int var13 = -1; var13 <= 1; var13++) {
         for (int var18 = -1; var18 <= 1; var18++) {
            if (var13 == 0 && var18 == 0) {
               var1.setBlockState(var3.add(var13, 4, var18), this.field_175912_c, 2);
            } else {
               var1.setBlockState(var3.add(var13, 4, var18), this.field_175911_b, 2);
            }
         }
      }

      for (int var14 = 1; var14 <= 3; var14++) {
         var1.setBlockState(var3.add(-1, var14, -1), this.field_175912_c, 2);
         var1.setBlockState(var3.add(-1, var14, 1), this.field_175912_c, 2);
         var1.setBlockState(var3.add(1, var14, -1), this.field_175912_c, 2);
         var1.setBlockState(var3.add(1, var14, 1), this.field_175912_c, 2);
      }

      return true;
   }
}
