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

   public CacheLocalByte(int maxX, int maxY, int maxZ) {
      this.m = maxX;
      this.f = maxY;
      this.b = maxZ;
      this.k = new byte[maxX][maxY][maxZ];
      this.I();
   }

   public void I() {
      for (int i = 0; i < this.m; i++) {
         byte[][] bytess = this.k[i];

         for (int j = 0; j < this.f; j++) {
            byte[] bytes = bytess[j];

            for (int l = 0; l < this.b; l++) {
               bytes[l] = -1;
            }
         }
      }
   }

   public void r(int i, int j, int k) {
      this.T = i;
      this.d = j;
      this.D = k;
      this.I();
   }

   public byte P(int i, int j, int k) {
      try {
         this.j = this.k[i - this.T][j - this.d];
         this.y = k - this.D;
         return this.j[this.y];
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
         arrayIndexOutOfBoundsException.printStackTrace();
         return -1;
      }
   }

   public void x(byte b) {
      try {
         this.j[this.y] = b;
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
}
