package net.minecraft.src;

public class CacheLocalByte {
   private int maxX = 18;
   private int maxY = 128;
   private int maxZ = 18;
   private int offsetX = 0;
   private int offsetY = 0;
   private int offsetZ = 0;
   private byte[][][] cache = (byte[][][])null;
   private byte[] lastZs = null;
   private int y = 0;

   public CacheLocalByte(int maxX, int maxY, int maxZ) {
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
      this.cache = new byte[maxX][maxY][maxZ];
      this.resetCache();
   }

   public void resetCache() {
      for (int i = 0; i < this.maxX; i++) {
         byte[][] bytess = this.cache[i];

         for (int j = 0; j < this.maxY; j++) {
            byte[] bytes = bytess[j];

            for (int k = 0; k < this.maxZ; k++) {
               bytes[k] = -1;
            }
         }
      }
   }

   public void setOffset(int x, int y, int z) {
      this.offsetX = x;
      this.offsetY = y;
      this.offsetZ = z;
      this.resetCache();
   }

   public byte get(int x, int y, int z) {
      try {
         this.lastZs = this.cache[x - this.offsetX][y - this.offsetY];
         this.y = z - this.offsetZ;
         return this.lastZs[this.y];
      } catch (ArrayIndexOutOfBoundsException e) {
         e.printStackTrace();
         return -1;
      }
   }

   public void x(byte b) {
      try {
         this.lastZs[this.y] = b;
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
}
