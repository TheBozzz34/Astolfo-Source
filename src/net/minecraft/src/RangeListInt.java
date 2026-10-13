package net.minecraft.src;

public class RangeListInt {
   private RangeInt[] ranges = new RangeInt[0];
   private static final long a = 114269750763004L;

   public void addRange(RangeInt var1) {
      this.ranges = (RangeInt[])Config.addObjectToArray(this.ranges, var1);
   }

   public boolean isInRange(int var1) {
      for (int var5 = 0; var5 < this.ranges.length; var5++) {
         RangeInt var6 = this.ranges[var5];
         if (var6.isInRange(var1)) {
            return true;
         }
      }

      return false;
   }

   public int getCountRanges() {
      return this.ranges.length;
   }

   public RangeInt getRange(int var1) {
      return this.ranges[var1];
   }

   @Override
   public String toString() {
      StringBuffer var4 = new StringBuffer();
      var4.append("[");

      for (int var5 = 0; var5 < this.ranges.length; var5++) {
         RangeInt var6 = this.ranges[var5];
         if (var5 > 0) {
            var4.append(", ");
         }

         var4.append(var6.toString());
      }

      var4.append("]");
      return var4.toString();
   }
}
