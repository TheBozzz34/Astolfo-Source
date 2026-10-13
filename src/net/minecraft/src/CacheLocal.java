package net.minecraft.src;

public class CacheLocal {
   private int t = 18;
   private int L = 128;
   private int w = 18;
   private int S = 0;
   private int X = 0;
   private int E = 0;
   private int[][][] P = (int[][][])null;
   private int[] b = null;
   private int A = 0;
   private static final long a = 111164382769763L;

   public CacheLocal(int var1, int var2, int var3) {
      this.t = var1;
      this.L = var2;
      this.w = var3;
      this.P = new int[var1][var2][var3];
      this.f();
   }

   public void f() {
      for (int var4 = 0; var4 < this.t; var4++) {
         int[][] var5 = this.P[var4];

         for (int var6 = 0; var6 < this.L; var6++) {
            int[] var7 = var5[var6];

            for (int var8 = 0; var8 < this.w; var8++) {
               var7[var8] = -1;
            }
         }
      }
   }

   public void t(int var1, int var2, int var3) {
      this.S = var1;
      this.X = var2;
      this.E = var3;
      this.f();
   }

   public int B(int var1, int var2, int var3) {
      try {
         this.b = this.P[var1 - this.S][var2 - this.X];
         this.A = var3 - this.E;
         return this.b[this.A];
      } catch (ArrayIndexOutOfBoundsException var5) {
         var5.printStackTrace();
         return -1;
      }
   }

   public void q(int var1) {
      try {
         this.b[this.A] = var1;
      } catch (Exception var3) {
         var3.printStackTrace();
      }
   }
}
