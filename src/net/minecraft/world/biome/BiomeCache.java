package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

public class BiomeCache {
   private final WorldChunkManager chunkManager;
   private long lastCleanupTime;
   private LongHashMap<BiomeCache.Block> cacheMap = new LongHashMap<>();
   private List<BiomeCache.Block> cache = Lists.newArrayList();
   private static final long a = 63749063907619L;

   public BiomeCache(WorldChunkManager var1) {
      this.chunkManager = var1;
   }

   public BiomeCache.Block getBiomeCacheBlock(int var1, int var2) {
      var1 >>= 4;
      var2 >>= 4;
      long var6 = var1 & 4294967295L | (var2 & 4294967295L) << 32;
      BiomeCache.Block var8 = this.cacheMap.getValueByKey(var6);
      if (var8 == null) {
         var8 = new BiomeCache.Block(var1, var2);
         this.cacheMap.add(var6, var8);
         this.cache.add(var8);
      }

      var8.lastAccessTime = MinecraftServer.getCurrentTimeMillis();
      return var8;
   }

   public BiomeGenBase func_180284_a(int var1, int var2, BiomeGenBase var3) {
      BiomeGenBase var7 = this.getBiomeCacheBlock(var1, var2).getBiomeGenAt(var1, var2);
      return var7 == null ? var3 : var7;
   }

   public void cleanupCache() {
      long var4 = MinecraftServer.getCurrentTimeMillis();
      long var6 = var4 - this.lastCleanupTime;
      if (var6 > 7500L || var6 < 0L) {
         this.lastCleanupTime = var4;

         for (int var8 = 0; var8 < this.cache.size(); var8++) {
            BiomeCache.Block var9 = this.cache.get(var8);
            long var10 = var4 - var9.lastAccessTime;
            if (var10 > 30000L || var10 < 0L) {
               this.cache.remove(var8--);
               long var12 = var9.xPosition & 4294967295L | (var9.zPosition & 4294967295L) << 32;
               this.cacheMap.remove(var12);
            }
         }
      }
   }

   public BiomeGenBase[] getCachedBiomes(int var1, int var2) {
      return this.getBiomeCacheBlock(var1, var2).biomes;
   }

   public class Block {
      public float[] rainfallValues = new float[256];
      public BiomeGenBase[] biomes = new BiomeGenBase[256];
      public int xPosition;
      public int zPosition;
      public long lastAccessTime;
      private static final long a = 82789090083618L;

      public Block(int var2, int var3) {
         this.xPosition = var2;
         this.zPosition = var3;
         BiomeCache.this.chunkManager.getRainfall(this.rainfallValues, var2 << 4, var3 << 4, 16, 16);
         BiomeCache.this.chunkManager.getBiomeGenAt(this.biomes, var2 << 4, var3 << 4, 16, 16, false);
      }

      public BiomeGenBase getBiomeGenAt(int var1, int var2) {
         return this.biomes[var1 & 15 | (var2 & 15) << 4];
      }
   }
}
