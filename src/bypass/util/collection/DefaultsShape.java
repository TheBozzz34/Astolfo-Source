package bypass.util.collection;

import java.util.Map;

public class DefaultsShape<K, V> extends MapShape<K, V> {
   private final Map<K, V> defaults;
   private final KeyShape<K, V> keyShape;

   public DefaultsShape(com.github.andrewoma.dexx.collection.Map<K, Integer> map, Map<K, V> map2, KeyShape<K, V> keyShape) {
      super(map);
      this.defaults = map2;
      this.keyShape = keyShape;
   }

   @Override
   public Map<K, V> getDefaults() {
      return this.defaults;
   }

   @Override
   public KeyShape<K, V> getKeyShape() {
      return this.keyShape;
   }
}
