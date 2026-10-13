package net.minecraft.world.gen.layer;

public class GenLayerAddIsland extends GenLayer {
   private static final long c = 72198757186040L;

   public GenLayerAddIsland(long var1, GenLayer var3) {
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
            int var16 = var12[var15 + 0 + (var14 + 0) * var10];
            int var17 = var12[var15 + 2 + (var14 + 0) * var10];
            int var18 = var12[var15 + 0 + (var14 + 2) * var10];
            int var19 = var12[var15 + 2 + (var14 + 2) * var10];
            int var20 = var12[var15 + 1 + (var14 + 1) * var10];
            this.initChunkSeed(var15 + var1, var14 + var2);
            if (var20 != 0 || var16 == 0 && var17 == 0 && var18 == 0 && var19 == 0) {
               if (var20 > 0 && (var16 == 0 || var17 == 0 || var18 == 0 || var19 == 0)) {
                  if (this.nextInt(5) == 0) {
                     if (var20 == 4) {
                        var13[var15 + var14 * var3] = 4;
                     } else {
                        var13[var15 + var14 * var3] = 0;
                     }
                  } else {
                     var13[var15 + var14 * var3] = var20;
                  }
               } else {
                  var13[var15 + var14 * var3] = var20;
               }
            } else {
               int var21 = 1;
               int var22 = 1;
               if (var16 != 0 && this.nextInt(var21++) == 0) {
                  var22 = var16;
               }

               if (var17 != 0 && this.nextInt(var21++) == 0) {
                  var22 = var17;
               }

               if (var18 != 0 && this.nextInt(var21++) == 0) {
                  var22 = var18;
               }

               if (var19 != 0 && this.nextInt(var21++) == 0) {
                  var22 = var19;
               }

               if (this.nextInt(3) == 0) {
                  var13[var15 + var14 * var3] = var22;
               } else if (var22 == 4) {
                  var13[var15 + var14 * var3] = 4;
               } else {
                  var13[var15 + var14 * var3] = 0;
               }
            }
         }
      }

      return var13;
   }
}
