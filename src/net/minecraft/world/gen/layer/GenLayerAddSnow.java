package net.minecraft.world.gen.layer;

public class GenLayerAddSnow extends GenLayer {
   private static final long c = 54991600650002L;
   private static final long g = 6746533748088504326L;

   public GenLayerAddSnow(long var1, GenLayer var3) {
      super(var1);
      this.parent = var3;
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
            int var16 = var12[var15 + 1 + (var14 + 1) * var10];
            this.initChunkSeed(var15 + var1, var14 + var2);
            if (var16 == 0) {
               var13[var15 + var14 * var3] = 0;
            } else {
               int var17 = this.nextInt((int)g);
               byte var18;
               if (var17 == 0) {
                  var18 = 4;
               } else if (var17 <= 1) {
                  var18 = 3;
               } else {
                  var18 = 1;
               }

               var13[var15 + var14 * var3] = var18;
            }
         }
      }

      return var13;
   }
}
