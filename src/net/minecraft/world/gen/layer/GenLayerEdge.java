package net.minecraft.world.gen.layer;

public class GenLayerEdge extends GenLayer {
   private final GenLayerEdge.Mode field_151627_c;
   private static final long c = 114190713038209L;

   public GenLayerEdge(long var1, GenLayer var3, GenLayerEdge.Mode var4) {
      super(var1);
      this.parent = var3;
      this.field_151627_c = var4;
   }

   @Override
   public int[] getInts(int var1, int var2, int var3, int var4) {
      switch (this.field_151627_c) {
         case COOL_WARM:
         default:
            return this.n(var1, var2, var3, var4);
         case HEAT_ICE:
            return this.B(var1, var2, var3, var4);
         case SPECIAL:
            return this.getIntsSpecial(var1, var2, var3, var4);
      }
   }

   private int[] n(int var1, int var2, int var3, int var4) {
      int var8 = var1 - 1;
      int var9 = var2 - 1;
      int var10 = 1 + var3 + 1;
      int var11 = 1 + var4 + 1;
      int[] var12 = this.parent.getInts(var8, var9, var10, var11);
      int[] var13 = IntCache.getIntCache(var3 * var4);

      for (int var14 = 0; var14 < var4; var14++) {
         for (int var15 = 0; var15 < var3; var15++) {
            this.initChunkSeed(var15 + var1, var14 + var2);
            int var16 = var12[var15 + 1 + (var14 + 1) * var10];
            if (var16 == 1) {
               int var17 = var12[var15 + 1 + (var14 + 1 - 1) * var10];
               int var18 = var12[var15 + 1 + 1 + (var14 + 1) * var10];
               int var19 = var12[var15 + 1 - 1 + (var14 + 1) * var10];
               int var20 = var12[var15 + 1 + (var14 + 1 + 1) * var10];
               boolean var21 = var17 == 3 || var18 == 3 || var19 == 3 || var20 == 3;
               boolean var22 = var17 == 4 || var18 == 4 || var19 == 4 || var20 == 4;
               if (var21 || var22) {
                  var16 = 2;
               }
            }

            var13[var15 + var14 * var3] = var16;
         }
      }

      return var13;
   }

   private int[] B(int var1, int var2, int var3, int var4) {
      int var8 = var1 - 1;
      int var9 = var2 - 1;
      int var10 = 1 + var3 + 1;
      int var11 = 1 + var4 + 1;
      int[] var12 = this.parent.getInts(var8, var9, var10, var11);
      int[] var13 = IntCache.getIntCache(var3 * var4);

      for (int var14 = 0; var14 < var4; var14++) {
         for (int var15 = 0; var15 < var3; var15++) {
            int var16 = var12[var15 + 1 + (var14 + 1) * var10];
            if (var16 == 4) {
               int var17 = var12[var15 + 1 + (var14 + 1 - 1) * var10];
               int var18 = var12[var15 + 1 + 1 + (var14 + 1) * var10];
               int var19 = var12[var15 + 1 - 1 + (var14 + 1) * var10];
               int var20 = var12[var15 + 1 + (var14 + 1 + 1) * var10];
               boolean var21 = var17 == 2 || var18 == 2 || var19 == 2 || var20 == 2;
               boolean var22 = var17 == 1 || var18 == 1 || var19 == 1 || var20 == 1;
               if (var22 || var21) {
                  var16 = 3;
               }
            }

            var13[var15 + var14 * var3] = var16;
         }
      }

      return var13;
   }

   private int[] getIntsSpecial(int var1, int var2, int var3, int var4) {
      int[] var8 = this.parent.getInts(var1, var2, var3, var4);
      int[] var9 = IntCache.getIntCache(var3 * var4);

      for (int var10 = 0; var10 < var4; var10++) {
         for (int var11 = 0; var11 < var3; var11++) {
            this.initChunkSeed(var11 + var1, var10 + var2);
            int var12 = var8[var11 + var10 * var3];
            if (var12 != 0 && this.nextInt(13) == 0) {
               var12 |= 1 + this.nextInt(15) << 8 & 3840;
            }

            var9[var11 + var10 * var3] = var12;
         }
      }

      return var9;
   }

   public enum Mode {
      COOL_WARM,
      HEAT_ICE,
      SPECIAL;
   }
}
