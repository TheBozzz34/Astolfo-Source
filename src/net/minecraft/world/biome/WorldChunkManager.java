package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class WorldChunkManager {
   private GenLayer genBiomes;
   private GenLayer biomeIndexLayer;
   private BiomeCache biomeCache = new BiomeCache(this);
   private List<BiomeGenBase> biomesToSpawnIn;
   private String generatorOptions = "";

   public WorldChunkManager() {
      this.biomesToSpawnIn = Lists.newArrayList();
      this.biomesToSpawnIn.add(BiomeGenBase.forest);
      this.biomesToSpawnIn.add(BiomeGenBase.plains);
      this.biomesToSpawnIn.add(BiomeGenBase.taiga);
      this.biomesToSpawnIn.add(BiomeGenBase.taigaHills);
      this.biomesToSpawnIn.add(BiomeGenBase.forestHills);
      this.biomesToSpawnIn.add(BiomeGenBase.jungle);
      this.biomesToSpawnIn.add(BiomeGenBase.jungleHills);
   }

   public WorldChunkManager(long seed, WorldType worldTypeIn, String options) {
      this();
      this.generatorOptions = options;
      GenLayer[] genLayers = GenLayer.initializeAllBiomeGenerators(seed, worldTypeIn, options);
      this.genBiomes = genLayers[0];
      this.biomeIndexLayer = genLayers[1];
   }

   public WorldChunkManager(World worldIn) {
      this(worldIn.getSeed(), worldIn.getWorldInfo().getTerrainType(), worldIn.getWorldInfo().getGeneratorOptions());
   }

   public List<BiomeGenBase> getBiomesToSpawnIn() {
      return this.biomesToSpawnIn;
   }

   public BiomeGenBase getBiomeGenerator(BlockPos pos) {
      return this.getBiomeGenerator(pos, (BiomeGenBase)null);
   }

   public BiomeGenBase getBiomeGenerator(BlockPos pos, BiomeGenBase biomeGenBaseIn) {
      return this.biomeCache.func_180284_a(pos.getX(), pos.getZ(), biomeGenBaseIn);
   }

   public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length) {
      IntCache.resetIntCache();
      if (listToReuse == null || listToReuse.length < width * length) {
         listToReuse = new float[width * length];
      }

      int[] ints = this.biomeIndexLayer.getInts(x, z, width, length);

      for (int i = 0; i < width * length; i++) {
         try {
            float f = BiomeGenBase.getBiomeFromBiomeList(ints[i], BiomeGenBase.field_180279_ad).getIntRainfall() / 65536.0F;
            if (f > 1.0F) {
               f = 1.0F;
            }

            listToReuse[i] = f;
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("DownfallBlock");
            crashReportCategory.addCrashSection("biome id", i);
            crashReportCategory.addCrashSection("downfalls[] size", listToReuse.length);
            crashReportCategory.addCrashSection("x", x);
            crashReportCategory.addCrashSection("z", z);
            crashReportCategory.addCrashSection("w", width);
            crashReportCategory.addCrashSection("h", length);
            throw new ReportedException(crashReport);
         }
      }

      return listToReuse;
   }

   public float getTemperatureAtHeight(float f, int i) {
      return f;
   }

   public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int width, int height) {
      IntCache.resetIntCache();
      if (biomes == null || biomes.length < width * height) {
         biomes = new BiomeGenBase[width * height];
      }

      int[] ints = this.genBiomes.getInts(x, z, width, height);

      try {
         for (int i = 0; i < width * height; i++) {
            biomes[i] = BiomeGenBase.getBiomeFromBiomeList(ints[i], BiomeGenBase.field_180279_ad);
         }

         return biomes;
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("RawBiomeBlock");
         crashReportCategory.addCrashSection("biomes[] size", biomes.length);
         crashReportCategory.addCrashSection("x", x);
         crashReportCategory.addCrashSection("z", z);
         crashReportCategory.addCrashSection("w", width);
         crashReportCategory.addCrashSection("h", height);
         throw new ReportedException(crashReport);
      }
   }

   public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] oldBiomeList, int x, int z, int width, int depth) {
      return this.getBiomeGenAt(oldBiomeList, x, z, width, depth, true);
   }

   public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
      IntCache.resetIntCache();
      if (listToReuse == null || listToReuse.length < width * length) {
         listToReuse = new BiomeGenBase[width * length];
      }

      if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0) {
         BiomeGenBase[] biomeGenBases = this.biomeCache.getCachedBiomes(x, z);
         System.arraycopy(biomeGenBases, 0, listToReuse, 0, width * length);
         return listToReuse;
      }

      int[] ints = this.biomeIndexLayer.getInts(x, z, width, length);

      for (int i = 0; i < width * length; i++) {
         listToReuse[i] = BiomeGenBase.getBiomeFromBiomeList(ints[i], BiomeGenBase.field_180279_ad);
      }

      return listToReuse;
   }

   public boolean areBiomesViable(int i, int j, int k, List<BiomeGenBase> list) {
      IntCache.resetIntCache();
      int l = i - k >> 2;
      int m = j - k >> 2;
      int n = i + k >> 2;
      int i2 = j + k >> 2;
      int i3 = n - l + 1;
      int i4 = i2 - m + 1;
      int[] ints = this.genBiomes.getInts(l, m, i3, i4);

      try {
         for (int i5 = 0; i5 < i3 * i4; i5++) {
            BiomeGenBase biomeGenBase = BiomeGenBase.getBiome(ints[i5]);
            if (!list.contains(biomeGenBase)) {
               return false;
            }
         }

         return true;
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Layer");
         crashReportCategory.addCrashSection("Layer", this.genBiomes.toString());
         crashReportCategory.addCrashSection("x", i);
         crashReportCategory.addCrashSection("z", j);
         crashReportCategory.addCrashSection("radius", k);
         crashReportCategory.addCrashSection("allowed", list);
         throw new ReportedException(crashReport);
      }
   }

   public BlockPos findBiomePosition(int x, int z, int range, List<BiomeGenBase> biomes, Random randomIn) {
      IntCache.resetIntCache();
      int i = x - range >> 2;
      int j = z - range >> 2;
      int k = x + range >> 2;
      int l = z + range >> 2;
      int m = k - i + 1;
      int n = l - j + 1;
      int[] ints = this.genBiomes.getInts(i, j, m, n);
      BlockPos blockPos = null;
      int i2 = 0;

      for (int i3 = 0; i3 < m * n; i3++) {
         int i4 = i + i3 % m << 2;
         int i5 = j + i3 / m << 2;
         BiomeGenBase biomeGenBase = BiomeGenBase.getBiome(ints[i3]);
         if (biomes.contains(biomeGenBase) && (blockPos == null || randomIn.nextInt(i2 + 1) == 0)) {
            blockPos = new BlockPos(i4, 0, i5);
            i2++;
         }
      }

      return blockPos;
   }

   public void cleanupCache() {
      this.biomeCache.cleanupCache();
   }
}
