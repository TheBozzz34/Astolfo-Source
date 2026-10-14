package bypass.util.collection;

public class ShapedMapKeyIterator<K, V> extends ShapedMapIterator<K, V, K> {
   public ShapedMapKeyIterator(ShapedMap<K, V> shapedMap) {
      super(shapedMap);
   }

   @Override
   public K next() {
      return this.D().getKey();
   }
}
