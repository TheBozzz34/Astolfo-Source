package com.github.andrewoma.dexx.collection;

public class Pair<C1, C2> {
   private final C1 component1;
   private final C2 component2;
   private static final long a = 41325613306087L;
   private static final long c = -2846369909045198817L;

   public Pair(C1 component1, C2 component2) {
      this.component1 = component1;
      this.component2 = component2;
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
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Pair pair = (Pair)o;
         return (this.component1 != null ? this.component1.equals(pair.component1) : pair.component1 == null)
            && (this.component2 != null ? this.component2.equals(pair.component2) : pair.component2 == null);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int i = this.component1 != null ? this.component1.hashCode() : 0;
      return (int)c * i + (this.component2 != null ? this.component2.hashCode() : 0);
   }
}
