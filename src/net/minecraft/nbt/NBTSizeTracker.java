package net.minecraft.nbt;

public class NBTSizeTracker {
   public static final NBTSizeTracker INFINITE = new NBTSizeTracker(0L) {
      @Override
      public void read(long var1) {
      }
   };
   private final long max;
   private long read;
   private static final long a = 138927618925568L;

   public NBTSizeTracker(long var1) {
      this.max = var1;
   }

   public void read(long var1) {
      this.read += var1 / 8L;
      if (this.read > this.max) {
         throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.read + "bytes where max allowed: " + this.max);
      }
   }
}
