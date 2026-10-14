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

   public BiomeCache(WorldChunkManager chunkManagerIn) {
      this.chunkManager = chunkManagerIn;
   }

   public BiomeCache.Block getBiomeCacheBlock(int x, int z) {
      x >>= 4;
      z >>= 4;
      long l = x & 4294967295L | (z & 4294967295L) << 32;
      BiomeCache.Block block = this.cacheMap.getValueByKey(l);
      if (block == null) {
         block = new BiomeCache.Block(x, z);
         this.cacheMap.add(l, block);
         this.cache.add(block);
      }

      block.lastAccessTime = MinecraftServer.getCurrentTimeMillis();
      return block;
   }

   public BiomeGenBase func_180284_a(int x, int z, BiomeGenBase biomeGenBase) {
      BiomeGenBase biomeGenBase2 = this.getBiomeCacheBlock(x, z).getBiomeGenAt(x, z);
      return biomeGenBase2 == null ? biomeGenBase : biomeGenBase2;
   }

   public void cleanupCache() {
      long l = MinecraftServer.getCurrentTimeMillis();
      long m = l - this.lastCleanupTime;
      if (m > 7500L || m < 0L) {
         this.lastCleanupTime = l;

         for (int i = 0; i < this.cache.size(); i++) {
            BiomeCache.Block block = this.cache.get(i);
            long n = l - block.lastAccessTime;
            if (n > 30000L || n < 0L) {
               this.cache.remove(i--);
               long l2 = block.xPosition & 4294967295L | (block.zPosition & 4294967295L) << 32;
               this.cacheMap.remove(l2);
            }
         }
      }
   }

   public BiomeGenBase[] getCachedBiomes(int x, int z) {
      return this.getBiomeCacheBlock(x, z).biomes;
   }

   public class Block {
      public float[] rainfallValues = new float[256];
      public BiomeGenBase[] biomes = new BiomeGenBase[256];
      public int xPosition;
      public int zPosition;
      public long lastAccessTime;

      public Block(int x, int z) {
         this.xPosition = x;
         this.zPosition = z;
         BiomeCache.this.chunkManager.getRainfall(this.rainfallValues, x << 4, z << 4, 16, 16);
         BiomeCache.this.chunkManager.getBiomeGenAt(this.biomes, x << 4, z << 4, 16, 16, false);
      }

      public BiomeGenBase getBiomeGenAt(int x, int z) {
         return this.biomes[x & 15 | (z & 15) << 4];
      }
   }
}
