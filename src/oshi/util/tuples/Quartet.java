package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Quartet<A, B, C, D> {
   private final A A;
   private final B b;
   private final C c;
   private final D d;
   private static final long a = 14829422886836L;

   public Quartet(A aIn, B b, C c, D d) {
      this.A = aIn;
      this.b = b;
      this.c = c;
      this.d = d;
   }

   public final A getA() {
      return this.A;
   }

   public final B getB() {
      return this.b;
   }

   public final C getC() {
      return this.c;
   }

   public final D getD() {
      return this.d;
   }
}
