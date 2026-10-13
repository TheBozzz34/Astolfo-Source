package bypass.util.collection;

public class ShapedMapValueIterator<K, V> extends ShapedMapIterator<K, V, V> {
   public ShapedMapValueIterator(ShapedMap<K, V> var1) {
      super(var1);
   }

   @Override
   public V next() {
      return this.D().getValue();
   }
}
