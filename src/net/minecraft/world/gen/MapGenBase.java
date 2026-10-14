package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class MapGenBase {
   public int range;
   public Random rand;
   public World worldObj;
   private static final long a = 127127707712123L;
   private static final long o = -863980616310325240L;

   public MapGenBase() {
      this.range = (int)o;
      this.rand = new Random();
   }

   public void generate(IChunkProvider chunkProviderIn, World worldIn, int x, int z, ChunkPrimer chunkPrimerIn) {
      int i = this.range;
      this.worldObj = worldIn;
      this.rand.setSeed(worldIn.getSeed());
      long l = this.rand.nextLong();
      long m = this.rand.nextLong();

      for (int j = x - i; j <= x + i; j++) {
         for (int k = z - i; k <= z + i; k++) {
            long n = j * l;
            long l2 = k * m;
            this.rand.setSeed(n ^ l2 ^ worldIn.getSeed());
            this.recursiveGenerate(worldIn, j, k, x, z, chunkPrimerIn);
         }
      }
   }

   public void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int i, int j, ChunkPrimer chunkPrimerIn) {
   }
}
