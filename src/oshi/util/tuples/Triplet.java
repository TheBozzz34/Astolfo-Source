package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Triplet<A, B, C> {
   private final A a;
   private final B b;
   private final C c;

   public Triplet(A a, B b, C c) {
      this.a = a;
      this.b = b;
      this.c = c;
   }

   public final A v() {
      return this.a;
   }

   public final B q() {
      return this.b;
   }

   public final C getC() {
      return this.c;
   }
}
