package net.minecraft.world.chunk.storage;

public class NibbleArrayReader {
   public final byte[] data;
   private final int depthBits;
   private final int depthBitsPlusFour;
   private static final long a = 80149099979257L;

   public NibbleArrayReader(byte[] dataIn, int depthBitsIn) {
      this.data = dataIn;
      this.depthBits = depthBitsIn;
      this.depthBitsPlusFour = depthBitsIn + 4;
   }

   public int get(int i, int j, int k) {
      int l = i << this.depthBitsPlusFour | k << this.depthBits | j;
      int m = l >> 1;
      int n = l & 1;
      return n == 0 ? this.data[m] & 15 : this.data[m] >> 4 & 15;
   }
}
