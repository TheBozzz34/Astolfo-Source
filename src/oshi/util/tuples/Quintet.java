package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Quintet<A, B, C, D, E> {
   private final A N;
   private final B b;
   private final C c;
   private final D d;
   private final E e;
   private static final long a = 77049214425797L;

   public Quintet(A var1, B var2, C var3, D var4, E var5) {
      this.N = (A)var1;
      this.b = (B)var2;
      this.c = (C)var3;
      this.d = (D)var4;
      this.e = (E)var5;
   }

   public final A y() {
      return this.N;
   }

   public final B x() {
      return this.b;
   }

   public final C q() {
      return this.c;
   }

   public final D J() {
      return this.d;
   }

   public final E s() {
      return this.e;
   }
}
