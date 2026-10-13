package bypass.util.collection;

public class ShapedMapKeyIterator<K, V> extends ShapedMapIterator<K, V, K> {
   public ShapedMapKeyIterator(ShapedMap<K, V> var1) {
      super(var1);
   }

   @Override
   public K next() {
      return this.D().getKey();
   }
}
