package net.minecraft.util;

public class Tuple<A, B> {
   private A a;
   private B b;

   public Tuple(A var1, B var2) {
      this.a = (A)var1;
      this.b = (B)var2;
   }

   public A getFirst() {
      return this.a;
   }

   public B getSecond() {
      return this.b;
   }
}
