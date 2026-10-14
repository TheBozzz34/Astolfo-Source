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

   public CacheLocal(int maxX, int maxY, int maxZ) {
      this.t = maxX;
      this.L = maxY;
      this.w = maxZ;
      this.P = new int[maxX][maxY][maxZ];
      this.f();
   }

   public void f() {
      for (int i = 0; i < this.t; i++) {
         int[][] intss = this.P[i];

         for (int j = 0; j < this.L; j++) {
            int[] ints = intss[j];

            for (int k = 0; k < this.w; k++) {
               ints[k] = -1;
            }
         }
      }
   }

   public void t(int i, int j, int k) {
      this.S = i;
      this.X = j;
      this.E = k;
      this.f();
   }

   public int B(int i, int j, int k) {
      try {
         this.b = this.P[i - this.S][j - this.X];
         this.A = k - this.E;
         return this.b[this.A];
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
         arrayIndexOutOfBoundsException.printStackTrace();
         return -1;
      }
   }

   public void q(int i) {
      try {
         this.b[this.A] = i;
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
}
