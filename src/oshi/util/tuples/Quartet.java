package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Quartet<A, B, C, D> {
   private final A A;
   private final B b;
   private final C c;
   private final D d;
   private static final long a = 14829422886836L;

   public Quartet(A var1, B var2, C var3, D var4) {
      this.A = (A)var1;
      this.b = (B)var2;
      this.c = (C)var3;
      this.d = (D)var4;
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
