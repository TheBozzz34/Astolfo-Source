package net.minecraft.src;

public class IntArray {
   private int[] c = null;
   private int M = 0;
   private int r = 0;
   private static final long a = 78834527743730L;

   public IntArray(int size) {
      this.c = new int[size];
   }

   public void W(int i) {
      this.c[this.M] = i;
      this.M++;
      if (this.r < this.M) {
         this.r = this.M;
      }
   }

   public void m(int i, int j) {
      this.c[i] = j;
      if (this.r < i) {
         this.r = i;
      }
   }

   public void J(int i) {
      this.M = i;
   }

   public void s(int[] ints) {
      int i = ints.length;

      for (int j = 0; j < i; j++) {
         this.c[this.M] = ints[j];
         this.M++;
      }

      if (this.r < this.M) {
         this.r = this.M;
      }
   }

   public int D(int i) {
      return this.c[i];
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
