package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiver extends GenLayer {
   private static final long c = 54214130472264L;

   public GenLayerRiver(long var1, GenLayer var3) {
      super(var1);
      super.parent = var3;
   }

   @Override
   public int[] getInts(int var1, int var2, int var3, int var4) {
      int var8 = var1 - 1;
      int var9 = var2 - 1;
      int var10 = var3 + 2;
      int var11 = var4 + 2;
      int[] var12 = this.parent.getInts(var8, var9, var10, var11);
      int[] var13 = IntCache.getIntCache(var3 * var4);

      for (int var14 = 0; var14 < var4; var14++) {
         for (int var15 = 0; var15 < var3; var15++) {
            int var16 = this.func_151630_c(var12[var15 + 0 + (var14 + 1) * var10]);
            int var17 = this.func_151630_c(var12[var15 + 2 + (var14 + 1) * var10]);
            int var18 = this.func_151630_c(var12[var15 + 1 + (var14 + 0) * var10]);
            int var19 = this.func_151630_c(var12[var15 + 1 + (var14 + 2) * var10]);
            int var20 = this.func_151630_c(var12[var15 + 1 + (var14 + 1) * var10]);
            if (var20 == var16 && var20 == var18 && var20 == var17 && var20 == var19) {
               var13[var15 + var14 * var3] = -1;
            } else {
               var13[var15 + var14 * var3] = BiomeGenBase.river.biomeID;
            }
         }
      }

      return var13;
   }

   private int func_151630_c(int var1) {
      return var1 >= 2 ? 2 + (var1 & 1) : var1;
   }
}
