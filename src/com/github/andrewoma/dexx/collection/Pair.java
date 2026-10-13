package com.github.andrewoma.dexx.collection;

public class Pair<C1, C2> {
   private final C1 component1;
   private final C2 component2;
   private static final long a = 41325613306087L;
   private static final long c = -2846369909045198817L;

   public Pair(C1 var1, C2 var2) {
      this.component1 = (C1)var1;
      this.component2 = (C2)var2;
   }

   public C1 component1() {
      return this.component1;
   }

   public C2 component2() {
      return this.component2;
   }

   @Override
   public String toString() {
      return "(" + this.component1 + ", " + this.component2 + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Pair var5 = (Pair)var1;
         return (this.component1 != null ? this.component1.equals(var5.component1) : var5.component1 == null)
            && (this.component2 != null ? this.component2.equals(var5.component2) : var5.component2 == null);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var5 = this.component1 != null ? this.component1.hashCode() : 0;
      return (int)c * var5 + (this.component2 != null ? this.component2.hashCode() : 0);
   }
}
