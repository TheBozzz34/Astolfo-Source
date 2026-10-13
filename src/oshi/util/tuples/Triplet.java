package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Triplet<A, B, C> {
   private final A a;
   private final B b;
   private final C c;

   public Triplet(A var1, B var2, C var3) {
      this.a = (A)var1;
      this.b = (B)var2;
      this.c = (C)var3;
   }

   public final A v() {
      return this.a;
   }

   public final B q() {
      return this.b;
   }

   public final C U() {
      return this.c;
   }
}
