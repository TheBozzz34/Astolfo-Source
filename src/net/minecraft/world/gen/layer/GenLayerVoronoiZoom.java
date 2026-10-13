package net.minecraft.world.gen.layer;

public class GenLayerVoronoiZoom extends GenLayer {
   private static final long c = 51371569411676L;

   public GenLayerVoronoiZoom(long var1, GenLayer var3) {
      super(var1);
      super.parent = var3;
   }

   @Override
   public int[] getInts(int var1, int var2, int var3, int var4) {
      var1 -= 2;
      var2 -= 2;
      int var8 = var1 >> 2;
      int var9 = var2 >> 2;
      int var10 = (var3 >> 2) + 2;
      int var11 = (var4 >> 2) + 2;
      int[] var12 = this.parent.getInts(var8, var9, var10, var11);
      int var13 = var10 - 1 << 2;
      int var14 = var11 - 1 << 2;
      int[] var15 = IntCache.getIntCache(var13 * var14);

      for (int var16 = 0; var16 < var11 - 1; var16++) {
         int var17 = 0;
         int var18 = var12[var17 + 0 + (var16 + 0) * var10];
         int var19 = var12[var17 + 0 + (var16 + 1) * var10];

         while (var17 < var10 - 1) {
            this.initChunkSeed(var17 + var8 << 2, var16 + var9 << 2);
            double var22 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            double var24 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            this.initChunkSeed(var17 + var8 + 1 << 2, var16 + var9 << 2);
            double var26 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            double var28 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            this.initChunkSeed(var17 + var8 << 2, var16 + var9 + 1 << 2);
            double var30 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
            double var32 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            this.initChunkSeed(var17 + var8 + 1 << 2, var16 + var9 + 1 << 2);
            double var34 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            double var36 = (this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
            int var38 = var12[var17 + 1 + (var16 + 0) * var10] & 0xFF;
            int var39 = var12[var17 + 1 + (var16 + 1) * var10] & 0xFF;

            for (int var40 = 0; var40 < 4; var40++) {
               int var41 = ((var16 << 2) + var40) * var13 + (var17 << 2);

               for (int var42 = 0; var42 < 4; var42++) {
                  double var43 = (var40 - var24) * (var40 - var24) + (var42 - var22) * (var42 - var22);
                  double var45 = (var40 - var28) * (var40 - var28) + (var42 - var26) * (var42 - var26);
                  double var47 = (var40 - var32) * (var40 - var32) + (var42 - var30) * (var42 - var30);
                  double var49 = (var40 - var36) * (var40 - var36) + (var42 - var34) * (var42 - var34);
                  if (var43 < var45 && var43 < var47 && var43 < var49) {
                     var15[var41++] = var18;
                  } else if (var45 < var43 && var45 < var47 && var45 < var49) {
                     var15[var41++] = var38;
                  } else if (var47 < var43 && var47 < var45 && var47 < var49) {
                     var15[var41++] = var19;
                  } else {
                     var15[var41++] = var39;
                  }
               }
            }

            var18 = var38;
            var19 = var39;
            var17++;
         }
      }

      int[] var53 = IntCache.getIntCache(var3 * var4);

      for (int var54 = 0; var54 < var4; var54++) {
         System.arraycopy(var15, (var54 + (var2 & 3)) * var13 + (var1 & 3), var53, var54 * var3, var3);
      }

      return var53;
   }
}
