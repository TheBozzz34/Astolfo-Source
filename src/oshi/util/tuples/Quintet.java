package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Quintet<A, B, C, D, E> {
   private final A N;
   private final B b;
   private final C c;
   private final D d;
   private final E e;

   public Quintet(A a, B b, C c, D d, E eIn) {
      this.N = a;
      this.b = b;
      this.c = c;
      this.d = d;
      this.e = eIn;
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
