package net.minecraft.util;

public class Tuple<A, B> {
   private A first;
   private B second;

   public Tuple(A aIn, B bIn) {
      this.first = (A)aIn;
      this.second = (B)bIn;
   }

   public A getFirst() {
      return this.first;
   }

   public B getSecond() {
      return this.second;
   }
}
