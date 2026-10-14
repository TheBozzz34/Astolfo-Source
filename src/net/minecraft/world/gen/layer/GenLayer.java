package net.minecraft.world.gen.layer;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.ChunkProviderSettings;

public abstract class GenLayer {
   private long worldGenSeed;
   public GenLayer parent;
   private long chunkSeed;
   public long baseSeed;

   public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType worldType, String string) {
      GenLayerIsland genLayerIsland = new GenLayerIsland(1L);
      GenLayerFuzzyZoom genLayerFuzzyZoom = new GenLayerFuzzyZoom(2000L, genLayerIsland);
      GenLayerAddIsland genLayerAddIsland = new GenLayerAddIsland(1L, genLayerFuzzyZoom);
      GenLayerZoom genLayerZoom = new GenLayerZoom(2001L, genLayerAddIsland);
      GenLayerAddIsland genLayerAddIsland2 = new GenLayerAddIsland(2L, genLayerZoom);
      genLayerAddIsland2 = new GenLayerAddIsland(50L, genLayerAddIsland2);
      genLayerAddIsland2 = new GenLayerAddIsland(70L, genLayerAddIsland2);
      GenLayerRemoveTooMuchOcean genLayerRemoveTooMuchOcean = new GenLayerRemoveTooMuchOcean(2L, genLayerAddIsland2);
      GenLayerAddSnow genLayerAddSnow = new GenLayerAddSnow(2L, genLayerRemoveTooMuchOcean);
      GenLayerAddIsland genLayerAddIsland3 = new GenLayerAddIsland(3L, genLayerAddSnow);
      GenLayerEdge genLayerEdge = new GenLayerEdge(2L, genLayerAddIsland3, GenLayerEdge.Mode.COOL_WARM);
      genLayerEdge = new GenLayerEdge(2L, genLayerEdge, GenLayerEdge.Mode.HEAT_ICE);
      genLayerEdge = new GenLayerEdge(3L, genLayerEdge, GenLayerEdge.Mode.SPECIAL);
      GenLayerZoom genLayerZoom2 = new GenLayerZoom(2002L, genLayerEdge);
      genLayerZoom2 = new GenLayerZoom(2003L, genLayerZoom2);
      GenLayerAddIsland genLayerAddIsland4 = new GenLayerAddIsland(4L, genLayerZoom2);
      GenLayerAddMushroomIsland genLayerAddMushroomIsland = new GenLayerAddMushroomIsland(5L, genLayerAddIsland4);
      GenLayerDeepOcean genLayerDeepOcean = new GenLayerDeepOcean(4L, genLayerAddMushroomIsland);
      GenLayer genLayer = GenLayerZoom.magnify(1000L, genLayerDeepOcean, 0);
      ChunkProviderSettings chunkProviderSettings = null;
      int i = 4;
      int j = i;
      if (worldType == WorldType.CUSTOMIZED && string.length() > 0) {
         chunkProviderSettings = ChunkProviderSettings.Factory.jsonToFactory(string).func_177864_b();
         i = chunkProviderSettings.biomeSize;
         j = chunkProviderSettings.riverSize;
      }

      if (worldType == WorldType.LARGE_BIOMES) {
         i = 6;
      }

      GenLayer genLayer2 = GenLayerZoom.magnify(1000L, genLayer, 0);
      GenLayerRiverInit genLayerRiverInit = new GenLayerRiverInit(100L, genLayer2);
      GenLayerBiome genLayerBiome = new GenLayerBiome(200L, genLayer, worldType, string);
      GenLayer genLayer3 = GenLayerZoom.magnify(1000L, genLayerBiome, 2);
      GenLayerBiomeEdge genLayerBiomeEdge = new GenLayerBiomeEdge(1000L, genLayer3);
      GenLayer genLayer4 = GenLayerZoom.magnify(1000L, genLayerRiverInit, 2);
      GenLayer genLayer5 = new GenLayerHills(1000L, genLayerBiomeEdge, genLayer4);
      GenLayer genLayer6 = GenLayerZoom.magnify(1000L, genLayerRiverInit, 2);
      genLayer6 = GenLayerZoom.magnify(1000L, genLayer6, j);
      GenLayerRiver genLayerRiver = new GenLayerRiver(1L, genLayer6);
      GenLayerSmooth genLayerSmooth = new GenLayerSmooth(1000L, genLayerRiver);
      genLayer5 = new GenLayerRareBiome(1001L, genLayer5);

      for (int k = 0; k < i; k++) {
         genLayer5 = new GenLayerZoom(1000 + k, genLayer5);
         if (k == 0) {
            genLayer5 = new GenLayerAddIsland(3L, genLayer5);
         }

         if (k == 1 || i == 1) {
            genLayer5 = new GenLayerShore(1000L, genLayer5);
         }
      }

      GenLayerSmooth genLayerSmooth2 = new GenLayerSmooth(1000L, genLayer5);
      GenLayerRiverMix genLayerRiverMix = new GenLayerRiverMix(100L, genLayerSmooth2, genLayerSmooth);
      GenLayerVoronoiZoom genLayerVoronoiZoom = new GenLayerVoronoiZoom(10L, genLayerRiverMix);
      genLayerRiverMix.initWorldGenSeed(seed);
      genLayerVoronoiZoom.initWorldGenSeed(seed);
      return new GenLayer[]{genLayerRiverMix, genLayerVoronoiZoom, genLayerRiverMix};
   }

   public GenLayer(long l) {
      this.baseSeed = l;
      this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
      this.baseSeed += l;
      this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
      this.baseSeed += l;
      this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
      this.baseSeed += l;
   }

   public void initWorldGenSeed(long seed) {
      this.worldGenSeed = seed;
      if (this.parent != null) {
         this.parent.initWorldGenSeed(seed);
      }

      this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
      this.worldGenSeed = this.worldGenSeed + this.baseSeed;
      this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
      this.worldGenSeed = this.worldGenSeed + this.baseSeed;
      this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
      this.worldGenSeed = this.worldGenSeed + this.baseSeed;
   }

   public void initChunkSeed(long l, long m) {
      this.chunkSeed = this.worldGenSeed;
      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed += l;
      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed += m;
      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed += l;
      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed += m;
   }

   public int nextInt(int i) {
      int j = (int)((this.chunkSeed >> 24) % i);
      if (j < 0) {
         j += i;
      }

      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed = this.chunkSeed + this.worldGenSeed;
      return j;
   }

   public abstract int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight);

   public static boolean biomesEqualOrMesaPlateau(int biomeIDA, int biomeIDB) {
      if (biomeIDA == biomeIDB) {
         return true;
      }

      if (biomeIDA != BiomeGenBase.mesaPlateau_F.biomeID && biomeIDA != BiomeGenBase.mesaPlateau.biomeID) {
         final BiomeGenBase biomeGenBase = BiomeGenBase.getBiome(biomeIDA);
         final BiomeGenBase biomeGenBase2 = BiomeGenBase.getBiome(biomeIDB);

         try {
            return biomeGenBase != null && biomeGenBase2 != null ? biomeGenBase.isEqualTo(biomeGenBase2) : false;
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Comparing biomes");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Biomes being compared");
            crashReportCategory.addCrashSection("Biome A ID", biomeIDA);
            crashReportCategory.addCrashSection("Biome B ID", biomeIDB);
            crashReportCategory.addCrashSectionCallable("Biome A", new Callable<String>() {
               public String call() throws Exception {
                  return String.valueOf(biomeGenBase);
               }
            });
            crashReportCategory.addCrashSectionCallable("Biome B", new Callable<String>() {
               public String call() throws Exception {
                  return String.valueOf(biomeGenBase2);
               }
            });
            throw new ReportedException(crashReport);
         }
      } else {
         return biomeIDB == BiomeGenBase.mesaPlateau_F.biomeID || biomeIDB == BiomeGenBase.mesaPlateau.biomeID;
      }
   }

   public static boolean isBiomeOceanic(int i) {
      return i == BiomeGenBase.ocean.biomeID || i == BiomeGenBase.deepOcean.biomeID || i == BiomeGenBase.frozenOcean.biomeID;
   }

   public int selectRandom(int... ints) {
      return ints[this.nextInt(ints.length)];
   }

   public int selectModeOrRandom(int i, int j, int k, int l) {
      return j == k && k == l
         ? j
         : (
            i == j && i == k
               ? i
               : (
                  i == j && i == l
                     ? i
                     : (
                        i == k && i == l
                           ? i
                           : (
                              i == j && k != l
                                 ? i
                                 : (
                                    i == k && j != l
                                       ? i
                                       : (
                                          i == l && j != k
                                             ? i
                                             : (j == k && i != l ? j : (j == l && i != k ? j : (k == l && i != j ? k : this.selectRandom(i, j, k, l))))
                                       )
                                 )
                           )
                     )
               )
         );
   }
}
