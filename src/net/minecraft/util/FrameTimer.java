package net.minecraft.util;

public class FrameTimer {
   private final long[] frames = new long[240];
   private int lastIndex;
   private int counter;
   private int index;
   private static final long a = 92424025200239L;

   public void addFrame(long var1) {
      this.frames[this.index] = var1;
      this.index++;
      if (this.index == 240) {
         this.index = 0;
      }

      if (this.counter < 240) {
         this.lastIndex = 0;
         this.counter++;
      } else {
         this.lastIndex = this.parseIndex(this.index + 1);
      }
   }

   public int getLagometerValue(long var1, int var3) {
      double var4 = var1 / 1.6666666E7;
      return (int)(var4 * var3);
   }

   public int R() {
      return this.lastIndex;
   }

   public int C() {
      return this.index;
   }

   public int parseIndex(int var1) {
      return var1 % 240;
   }

   public long[] getFrames() {
      return this.frames;
   }
}
