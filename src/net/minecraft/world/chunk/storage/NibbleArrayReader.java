package net.minecraft.world.chunk.storage;

public class NibbleArrayReader {
   public final byte[] data;
   private final int depthBits;
   private final int depthBitsPlusFour;
   private static final long a = 80149099979257L;

   public NibbleArrayReader(byte[] var1, int var2) {
      this.data = var1;
      this.depthBits = var2;
      this.depthBitsPlusFour = var2 + 4;
   }

   public int get(int var1, int var2, int var3) {
      int var7 = var1 << this.depthBitsPlusFour | var3 << this.depthBits | var2;
      int var8 = var7 >> 1;
      int var9 = var7 & 1;
      return var9 == 0 ? this.data[var8] & 15 : this.data[var8] >> 4 & 15;
   }
}
