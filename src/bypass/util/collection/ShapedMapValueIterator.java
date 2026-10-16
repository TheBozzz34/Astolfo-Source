package bypass.util.collection;

public class ShapedMapValueIterator<K, V> extends ShapedMapIterator<K, V, V> {
   public ShapedMapValueIterator(ShapedMap<K, V> shapedMap) {
      super(shapedMap);
   }

   @Override
   public V next() {
      return this.nextEntry().getValue();
   }
}
