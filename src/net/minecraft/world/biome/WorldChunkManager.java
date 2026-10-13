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
   private static final long a = 102345644744286L;

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

   public WorldChunkManager(long var1, WorldType var3, String var4) {
      this();
      this.generatorOptions = var4;
      GenLayer[] var5 = GenLayer.initializeAllBiomeGenerators(var1, var3, var4);
      this.genBiomes = var5[0];
      this.biomeIndexLayer = var5[1];
   }

   public WorldChunkManager(World var1) {
      this(var1.getSeed(), var1.getWorldInfo().getTerrainType(), var1.getWorldInfo().getGeneratorOptions());
   }

   public List<BiomeGenBase> getBiomesToSpawnIn() {
      return this.biomesToSpawnIn;
   }

   public BiomeGenBase getBiomeGenerator(BlockPos var1) {
      return this.getBiomeGenerator(var1, (BiomeGenBase)null);
   }

   public BiomeGenBase getBiomeGenerator(BlockPos var1, BiomeGenBase var2) {
      return this.biomeCache.func_180284_a(var1.getX(), var1.getZ(), var2);
   }

   public float[] getRainfall(float[] var1, int var2, int var3, int var4, int var5) {
      IntCache.resetIntCache();
      if (var1 == null || var1.length < var4 * var5) {
         var1 = new float[var4 * var5];
      }

      int[] var9 = this.biomeIndexLayer.getInts(var2, var3, var4, var5);

      for (int var10 = 0; var10 < var4 * var5; var10++) {
         try {
            float var11 = BiomeGenBase.getBiomeFromBiomeList(var9[var10], BiomeGenBase.field_180279_ad).getIntRainfall() / 65536.0F;
            if (var11 > 1.0F) {
               var11 = 1.0F;
            }

            var1[var10] = var11;
         } catch (Throwable var14) {
            CrashReport var12 = CrashReport.makeCrashReport(var14, "Invalid Biome id");
            CrashReportCategory var13 = var12.makeCategory("DownfallBlock");
            var13.addCrashSection("biome id", var10);
            var13.addCrashSection("downfalls[] size", var1.length);
            var13.addCrashSection("x", var2);
            var13.addCrashSection("z", var3);
            var13.addCrashSection("w", var4);
            var13.addCrashSection("h", var5);
            throw new ReportedException(var12);
         }
      }

      return var1;
   }

   public float getTemperatureAtHeight(float var1, int var2) {
      return var1;
   }

   public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] var1, int var2, int var3, int var4, int var5) {
      IntCache.resetIntCache();
      if (var1 == null || var1.length < var4 * var5) {
         var1 = new BiomeGenBase[var4 * var5];
      }

      int[] var9 = this.genBiomes.getInts(var2, var3, var4, var5);

      try {
         for (int var10 = 0; var10 < var4 * var5; var10++) {
            var1[var10] = BiomeGenBase.getBiomeFromBiomeList(var9[var10], BiomeGenBase.field_180279_ad);
         }

         return var1;
      } catch (Throwable var13) {
         CrashReport var11 = CrashReport.makeCrashReport(var13, "Invalid Biome id");
         CrashReportCategory var12 = var11.makeCategory("RawBiomeBlock");
         var12.addCrashSection("biomes[] size", var1.length);
         var12.addCrashSection("x", var2);
         var12.addCrashSection("z", var3);
         var12.addCrashSection("w", var4);
         var12.addCrashSection("h", var5);
         throw new ReportedException(var11);
      }
   }

   public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] var1, int var2, int var3, int var4, int var5) {
      return this.getBiomeGenAt(var1, var2, var3, var4, var5, true);
   }

   public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] var1, int var2, int var3, int var4, int var5, boolean var6) {
      IntCache.resetIntCache();
      if (var1 == null || var1.length < var4 * var5) {
         var1 = new BiomeGenBase[var4 * var5];
      }

      if (var6 && var4 == 16 && var5 == 16 && (var2 & 15) == 0 && (var3 & 15) == 0) {
         BiomeGenBase[] var12 = this.biomeCache.getCachedBiomes(var2, var3);
         System.arraycopy(var12, 0, var1, 0, var4 * var5);
         return var1;
      }

      int[] var10 = this.biomeIndexLayer.getInts(var2, var3, var4, var5);

      for (int var11 = 0; var11 < var4 * var5; var11++) {
         var1[var11] = BiomeGenBase.getBiomeFromBiomeList(var10[var11], BiomeGenBase.field_180279_ad);
      }

      return var1;
   }

   public boolean areBiomesViable(int var1, int var2, int var3, List<BiomeGenBase> var4) {
      IntCache.resetIntCache();
      int var8 = var1 - var3 >> 2;
      int var9 = var2 - var3 >> 2;
      int var10 = var1 + var3 >> 2;
      int var11 = var2 + var3 >> 2;
      int var12 = var10 - var8 + 1;
      int var13 = var11 - var9 + 1;
      int[] var14 = this.genBiomes.getInts(var8, var9, var12, var13);

      try {
         for (int var15 = 0; var15 < var12 * var13; var15++) {
            BiomeGenBase var19 = BiomeGenBase.getBiome(var14[var15]);
            if (!var4.contains(var19)) {
               return false;
            }
         }

         return true;
      } catch (Throwable var18) {
         CrashReport var16 = CrashReport.makeCrashReport(var18, "Invalid Biome id");
         CrashReportCategory var17 = var16.makeCategory("Layer");
         var17.addCrashSection("Layer", this.genBiomes.toString());
         var17.addCrashSection("x", var1);
         var17.addCrashSection("z", var2);
         var17.addCrashSection("radius", var3);
         var17.addCrashSection("allowed", var4);
         throw new ReportedException(var16);
      }
   }

   public BlockPos findBiomePosition(int var1, int var2, int var3, List<BiomeGenBase> var4, Random var5) {
      IntCache.resetIntCache();
      int var9 = var1 - var3 >> 2;
      int var10 = var2 - var3 >> 2;
      int var11 = var1 + var3 >> 2;
      int var12 = var2 + var3 >> 2;
      int var13 = var11 - var9 + 1;
      int var14 = var12 - var10 + 1;
      int[] var15 = this.genBiomes.getInts(var9, var10, var13, var14);
      BlockPos var16 = null;
      int var17 = 0;

      for (int var18 = 0; var18 < var13 * var14; var18++) {
         int var19 = var9 + var18 % var13 << 2;
         int var20 = var10 + var18 / var13 << 2;
         BiomeGenBase var21 = BiomeGenBase.getBiome(var15[var18]);
         if (var4.contains(var21) && (var16 == null || var5.nextInt(var17 + 1) == 0)) {
            var16 = new BlockPos(var19, 0, var20);
            var17++;
         }
      }

      return var16;
   }

   public void cleanupCache() {
      this.biomeCache.cleanupCache();
   }
}
