package net.minecraft.src;

public class RangeInt {
   private int min;
   private int max;

   public RangeInt(int minIn, int maxIn) {
      this.min = Math.min(minIn, maxIn);
      this.max = Math.max(minIn, maxIn);
   }

   public boolean isInRange(int val) {
      return val < this.min ? false : val <= this.max;
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   @Override
   public String toString() {
      return "min: " + this.min + ", max: " + this.max;
   }
}
