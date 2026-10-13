package net.minecraft.world.gen.layer;

public class GenLayerSmooth extends GenLayer {
   private static final long c = 109530341732165L;

   public GenLayerSmooth(long var1, GenLayer var3) {
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
            int var16 = var12[var15 + 0 + (var14 + 1) * var10];
            int var17 = var12[var15 + 2 + (var14 + 1) * var10];
            int var18 = var12[var15 + 1 + (var14 + 0) * var10];
            int var19 = var12[var15 + 1 + (var14 + 2) * var10];
            int var20 = var12[var15 + 1 + (var14 + 1) * var10];
            if (var16 == var17 && var18 == var19) {
               this.initChunkSeed(var15 + var1, var14 + var2);
               if (this.nextInt(2) == 0) {
                  var20 = var16;
               } else {
                  var20 = var18;
               }
            } else {
               if (var16 == var17) {
                  var20 = var16;
               }

               if (var18 == var19) {
                  var20 = var18;
               }
            }

            var13[var15 + var14 * var3] = var20;
         }
      }

      return var13;
   }
}
