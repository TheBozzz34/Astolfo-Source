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

   public void generate(IChunkProvider var1, World var2, int var3, int var4, ChunkPrimer var5) {
      int var9 = this.range;
      this.worldObj = var2;
      this.rand.setSeed(var2.getSeed());
      long var10 = this.rand.nextLong();
      long var12 = this.rand.nextLong();

      for (int var14 = var3 - var9; var14 <= var3 + var9; var14++) {
         for (int var15 = var4 - var9; var15 <= var4 + var9; var15++) {
            long var16 = var14 * var10;
            long var18 = var15 * var12;
            this.rand.setSeed(var16 ^ var18 ^ var2.getSeed());
            this.recursiveGenerate(var2, var14, var15, var3, var4, var5);
         }
      }
   }

   public void recursiveGenerate(World var1, int var2, int var3, int var4, int var5, ChunkPrimer var6) {
   }
}
