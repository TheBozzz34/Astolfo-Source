package net.minecraft.src;

public class CacheLocalByte {
   private int m = 18;
   private int f = 128;
   private int b = 18;
   private int T = 0;
   private int d = 0;
   private int D = 0;
   private byte[][][] k = (byte[][][])null;
   private byte[] j = null;
   private int y = 0;
   private static final long a = 69421592994260L;

   public CacheLocalByte(int var1, int var2, int var3) {
      this.m = var1;
      this.f = var2;
      this.b = var3;
      this.k = new byte[var1][var2][var3];
      this.I();
   }

   public void I() {
      for (int var4 = 0; var4 < this.m; var4++) {
         byte[][] var5 = this.k[var4];

         for (int var6 = 0; var6 < this.f; var6++) {
            byte[] var7 = var5[var6];

            for (int var8 = 0; var8 < this.b; var8++) {
               var7[var8] = -1;
            }
         }
      }
   }

   public void r(int var1, int var2, int var3) {
      this.T = var1;
      this.d = var2;
      this.D = var3;
      this.I();
   }

   public byte P(int var1, int var2, int var3) {
      try {
         this.j = this.k[var1 - this.T][var2 - this.d];
         this.y = var3 - this.D;
         return this.j[this.y];
      } catch (ArrayIndexOutOfBoundsException var5) {
         var5.printStackTrace();
         return -1;
      }
   }

   public void x(byte var1) {
      try {
         this.j[this.y] = var1;
      } catch (Exception var3) {
         var3.printStackTrace();
      }
   }
}
