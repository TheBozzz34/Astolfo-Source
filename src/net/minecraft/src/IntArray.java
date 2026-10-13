package net.minecraft.src;

public class IntArray {
   private int[] c = null;
   private int M = 0;
   private int r = 0;
   private static final long a = 78834527743730L;

   public IntArray(int var1) {
      this.c = new int[var1];
   }

   public void W(int var1) {
      this.c[this.M] = var1;
      this.M++;
      if (this.r < this.M) {
         this.r = this.M;
      }
   }

   public void m(int var1, int var2) {
      this.c[var1] = var2;
      if (this.r < var1) {
         this.r = var1;
      }
   }

   public void J(int var1) {
      this.M = var1;
   }

   public void s(int[] var1) {
      int var5 = var1.length;

      for (int var6 = 0; var6 < var5; var6++) {
         this.c[this.M] = var1[var6];
         this.M++;
      }

      if (this.r < this.M) {
         this.r = this.M;
      }
   }

   public int D(int var1) {
      return this.c[var1];
   }

   public int[] J() {
      return this.c;
   }

   public void h() {
      this.M = 0;
      this.r = 0;
   }

   public int Z() {
      return this.r;
   }

   public int T() {
      return this.M;
   }
}
