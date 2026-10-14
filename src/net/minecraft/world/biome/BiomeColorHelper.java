package net.minecraft.world.biome;

import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BiomeColorHelper {
   private static final BiomeColorHelper.ColorResolver GRASS_COLOR = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition) {
         return biome.getGrassColorAtPos(blockPosition);
      }
   };
   private static final BiomeColorHelper.ColorResolver FOLIAGE_COLOR = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition) {
         return biome.getFoliageColorAtPos(blockPosition);
      }
   };
   private static final BiomeColorHelper.ColorResolver WATER_COLOR_MULTIPLIER = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition) {
         return biome.waterColorMultiplier;
      }
   };

   private static int getColorAtPos(IBlockAccess blockAccess, BlockPos pos, BiomeColorHelper.ColorResolver colorResolver) {
      int i = 0;
      int j = 0;
      int k = 0;

      for (BlockPos.MutableBlockPos mutableBlockPos : BlockPos.getAllInBoxMutable(pos.add(-1, 0, -1), pos.add(1, 0, 1))) {
         int l = colorResolver.getColorAtPos(blockAccess.getBiomeGenForCoords(mutableBlockPos), mutableBlockPos);
         i += (l & 0xFF0000) >> 16;
         j += (l & 0xFF00) >> 8;
         k += l & 0xFF;
      }

      return (i / 9 & 0xFF) << 16 | (j / 9 & 0xFF) << 8 | k / 9 & 0xFF;
   }

   public static int getGrassColorAtPos(IBlockAccess iBlockAccess, BlockPos blockPos) {
      return getColorAtPos(iBlockAccess, blockPos, GRASS_COLOR);
   }

   public static int getFoliageColorAtPos(IBlockAccess iBlockAccess, BlockPos blockPos) {
      return getColorAtPos(iBlockAccess, blockPos, FOLIAGE_COLOR);
   }

   public static int getWaterColorAtPos(IBlockAccess iBlockAccess, BlockPos blockPos) {
      return getColorAtPos(iBlockAccess, blockPos, WATER_COLOR_MULTIPLIER);
   }

   public interface ColorResolver {
      int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition);
   }
}
