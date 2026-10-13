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
   private static final long b = 113030085782939L;

   public static GenLayer[] initializeAllBiomeGenerators(long var0, WorldType var2, String var3) {
      GenLayerIsland var8 = new GenLayerIsland(1L);
      GenLayerFuzzyZoom var37 = new GenLayerFuzzyZoom(2000L, var8);
      GenLayerAddIsland var9 = new GenLayerAddIsland(1L, var37);
      GenLayerZoom var10 = new GenLayerZoom(2001L, var9);
      GenLayerAddIsland var11 = new GenLayerAddIsland(2L, var10);
      var11 = new GenLayerAddIsland(50L, var11);
      var11 = new GenLayerAddIsland(70L, var11);
      GenLayerRemoveTooMuchOcean var12 = new GenLayerRemoveTooMuchOcean(2L, var11);
      GenLayerAddSnow var13 = new GenLayerAddSnow(2L, var12);
      GenLayerAddIsland var14 = new GenLayerAddIsland(3L, var13);
      GenLayerEdge var15 = new GenLayerEdge(2L, var14, GenLayerEdge.Mode.COOL_WARM);
      var15 = new GenLayerEdge(2L, var15, GenLayerEdge.Mode.HEAT_ICE);
      var15 = new GenLayerEdge(3L, var15, GenLayerEdge.Mode.SPECIAL);
      GenLayerZoom var16 = new GenLayerZoom(2002L, var15);
      var16 = new GenLayerZoom(2003L, var16);
      GenLayerAddIsland var17 = new GenLayerAddIsland(4L, var16);
      GenLayerAddMushroomIsland var18 = new GenLayerAddMushroomIsland(5L, var17);
      GenLayerDeepOcean var19 = new GenLayerDeepOcean(4L, var18);
      GenLayer var20 = GenLayerZoom.magnify(1000L, var19, 0);
      ChunkProviderSettings var21 = null;
      int var22 = 4;
      int var23 = var22;
      if (var2 == WorldType.CUSTOMIZED && var3.length() > 0) {
         var21 = ChunkProviderSettings.Factory.jsonToFactory(var3).func_177864_b();
         var22 = var21.biomeSize;
         var23 = var21.riverSize;
      }

      if (var2 == WorldType.LARGE_BIOMES) {
         var22 = 6;
      }

      GenLayer var24 = GenLayerZoom.magnify(1000L, var20, 0);
      GenLayerRiverInit var25 = new GenLayerRiverInit(100L, var24);
      GenLayerBiome var26 = new GenLayerBiome(200L, var20, var2, var3);
      GenLayer var27 = GenLayerZoom.magnify(1000L, var26, 2);
      GenLayerBiomeEdge var28 = new GenLayerBiomeEdge(1000L, var27);
      GenLayer var29 = GenLayerZoom.magnify(1000L, var25, 2);
      GenLayer var30 = new GenLayerHills(1000L, var28, var29);
      GenLayer var31 = GenLayerZoom.magnify(1000L, var25, 2);
      var31 = GenLayerZoom.magnify(1000L, var31, var23);
      GenLayerRiver var32 = new GenLayerRiver(1L, var31);
      GenLayerSmooth var33 = new GenLayerSmooth(1000L, var32);
      var30 = new GenLayerRareBiome(1001L, var30);

      for (int var34 = 0; var34 < var22; var34++) {
         var30 = new GenLayerZoom(1000 + var34, var30);
         if (var34 == 0) {
            var30 = new GenLayerAddIsland(3L, var30);
         }

         if (var34 == 1 || var22 == 1) {
            var30 = new GenLayerShore(1000L, var30);
         }
      }

      GenLayerSmooth var46 = new GenLayerSmooth(1000L, var30);
      GenLayerRiverMix var35 = new GenLayerRiverMix(100L, var46, var33);
      GenLayerVoronoiZoom var36 = new GenLayerVoronoiZoom(10L, var35);
      var35.initWorldGenSeed(var0);
      var36.initWorldGenSeed(var0);
      return new GenLayer[]{var35, var36, var35};
   }

   public GenLayer(long var1) {
      this.baseSeed = var1;
      this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
      this.baseSeed += var1;
      this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
      this.baseSeed += var1;
      this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
      this.baseSeed += var1;
   }

   public void initWorldGenSeed(long var1) {
      this.worldGenSeed = var1;
      if (this.parent != null) {
         this.parent.initWorldGenSeed(var1);
      }

      this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
      this.worldGenSeed = this.worldGenSeed + this.baseSeed;
      this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
      this.worldGenSeed = this.worldGenSeed + this.baseSeed;
      this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
      this.worldGenSeed = this.worldGenSeed + this.baseSeed;
   }

   public void initChunkSeed(long var1, long var3) {
      this.chunkSeed = this.worldGenSeed;
      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed += var1;
      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed += var3;
      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed += var1;
      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed += var3;
   }

   public int nextInt(int var1) {
      int var5 = (int)((this.chunkSeed >> 24) % var1);
      if (var5 < 0) {
         var5 += var1;
      }

      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed = this.chunkSeed + this.worldGenSeed;
      return var5;
   }

   public abstract int[] getInts(int var1, int var2, int var3, int var4);

   public static boolean biomesEqualOrMesaPlateau(int var0, int var1) {
      if (var0 == var1) {
         return true;
      }

      if (var0 != BiomeGenBase.mesaPlateau_F.biomeID && var0 != BiomeGenBase.mesaPlateau.biomeID) {
         final BiomeGenBase var5 = BiomeGenBase.getBiome(var0);
         final BiomeGenBase var6 = BiomeGenBase.getBiome(var1);

         try {
            return var5 != null && var6 != null ? var5.isEqualTo(var6) : false;
         } catch (Throwable var10) {
            CrashReport var8 = CrashReport.makeCrashReport(var10, "Comparing biomes");
            CrashReportCategory var9 = var8.makeCategory("Biomes being compared");
            var9.addCrashSection("Biome A ID", var0);
            var9.addCrashSection("Biome B ID", var1);
            var9.addCrashSectionCallable("Biome A", new Callable<String>() {
               public String call() throws Exception {
                  return String.valueOf(var5);
               }
            });
            var9.addCrashSectionCallable("Biome B", new Callable<String>() {
               public String call() throws Exception {
                  return String.valueOf(var6);
               }
            });
            throw new ReportedException(var8);
         }
      } else {
         return var1 == BiomeGenBase.mesaPlateau_F.biomeID || var1 == BiomeGenBase.mesaPlateau.biomeID;
      }
   }

   public static boolean isBiomeOceanic(int var0) {
      return var0 == BiomeGenBase.ocean.biomeID || var0 == BiomeGenBase.deepOcean.biomeID || var0 == BiomeGenBase.frozenOcean.biomeID;
   }

   public int selectRandom(int... var1) {
      return var1[this.nextInt(var1.length)];
   }

   public int selectModeOrRandom(int var1, int var2, int var3, int var4) {
      return var2 == var3 && var3 == var4
         ? var2
         : (
            var1 == var2 && var1 == var3
               ? var1
               : (
                  var1 == var2 && var1 == var4
                     ? var1
                     : (
                        var1 == var3 && var1 == var4
                           ? var1
                           : (
                              var1 == var2 && var3 != var4
                                 ? var1
                                 : (
                                    var1 == var3 && var2 != var4
                                       ? var1
                                       : (
                                          var1 == var4 && var2 != var3
                                             ? var1
                                             : (
                                                var2 == var3 && var1 != var4
                                                   ? var2
                                                   : (
                                                      var2 == var4 && var1 != var3
                                                         ? var2
                                                         : (var3 == var4 && var1 != var2 ? var3 : this.selectRandom(var1, var2, var3, var4))
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }
}
