package bypass.util.collection;

import java.util.Map;

public class DefaultsShape<K, V> extends MapShape<K, V> {
   private final Map<K, V> A;
   private final KeyShape<K, V> C;

   public DefaultsShape(com.github.andrewoma.dexx.collection.Map<K, Integer> map, Map<K, V> map2, KeyShape<K, V> keyShape) {
      super(map);
      this.A = map2;
      this.C = keyShape;
   }

   @Override
   public Map<K, V> getDefaults() {
      return this.A;
   }

   @Override
   public KeyShape<K, V> getKeyShape() {
      return this.C;
   }
}
