package net.minecraft.world.biome;

import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BiomeColorHelper {
   private static final BiomeColorHelper.ColorResolver GRASS_COLOR = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase var1, BlockPos var2) {
         return var1.getGrassColorAtPos(var2);
      }
   };
   private static final BiomeColorHelper.ColorResolver FOLIAGE_COLOR = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase var1, BlockPos var2) {
         return var1.getFoliageColorAtPos(var2);
      }
   };
   private static final BiomeColorHelper.ColorResolver WATER_COLOR_MULTIPLIER = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase var1, BlockPos var2) {
         return var1.waterColorMultiplier;
      }
   };
   private static final long b = 85552134623245L;

   private static int getColorAtPos(IBlockAccess var0, BlockPos var1, BiomeColorHelper.ColorResolver var2) {
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;

      for (BlockPos.MutableBlockPos var10 : BlockPos.getAllInBoxMutable(var1.add(-1, 0, -1), var1.add(1, 0, 1))) {
         int var11 = var2.getColorAtPos(var0.getBiomeGenForCoords(var10), var10);
         var6 += (var11 & 0xFF0000) >> 16;
         var7 += (var11 & 0xFF00) >> 8;
         var8 += var11 & 0xFF;
      }

      return (var6 / 9 & 0xFF) << 16 | (var7 / 9 & 0xFF) << 8 | var8 / 9 & 0xFF;
   }

   public static int getGrassColorAtPos(IBlockAccess var0, BlockPos var1) {
      return getColorAtPos(var0, var1, GRASS_COLOR);
   }

   public static int getFoliageColorAtPos(IBlockAccess var0, BlockPos var1) {
      return getColorAtPos(var0, var1, FOLIAGE_COLOR);
   }

   public static int getWaterColorAtPos(IBlockAccess var0, BlockPos var1) {
      return getColorAtPos(var0, var1, WATER_COLOR_MULTIPLIER);
   }

   public interface ColorResolver {
      int getColorAtPos(BiomeGenBase var1, BlockPos var2);
   }
}
