package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Pair<A, B> {
   private final A a;
   private final B b;

   public Pair(A var1, B var2) {
      this.a = (A)var1;
      this.b = (B)var2;
   }

   public final A getA() {
      return this.a;
   }

   public final B getB() {
      return this.b;
   }
}
