package bypass.util;

public class Pair<K, V> {
   private final K first;
   private final V second;

   public Pair(K object, V object2) {
      this.first = (K)object;
      this.second = (V)object2;
   }

   public K getFirst() {
      return this.first;
   }

   public V getSecond() {
      return this.second;
   }
}
