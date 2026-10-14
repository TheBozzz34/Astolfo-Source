package bypass.util;

public class Pair<K, V> {
   private final K c;
   private final V f;

   public Pair(K object, V object2) {
      this.c = (K)object;
      this.f = (V)object2;
   }

   public K n() {
      return this.c;
   }

   public V f() {
      return this.f;
   }
}
