package net.minecraft.src;

public class RangeListInt {
   private RangeInt[] ranges = new RangeInt[0];

   public void addRange(RangeInt ri) {
      this.ranges = (RangeInt[])Config.addObjectToArray(this.ranges, ri);
   }

   public boolean isInRange(int val) {
      for (int i = 0; i < this.ranges.length; i++) {
         RangeInt rangeInt = this.ranges[i];
         if (rangeInt.isInRange(val)) {
            return true;
         }
      }

      return false;
   }

   public int getCountRanges() {
      return this.ranges.length;
   }

   public RangeInt getRange(int i) {
      return this.ranges[i];
   }

   @Override
   public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("[");

      for (int i = 0; i < this.ranges.length; i++) {
         RangeInt rangeInt = this.ranges[i];
         if (i > 0) {
            stringBuffer.append(", ");
         }

         stringBuffer.append(rangeInt.toString());
      }

      stringBuffer.append("]");
      return stringBuffer.toString();
   }
}
