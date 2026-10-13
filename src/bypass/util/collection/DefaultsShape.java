package bypass.util.collection;

import java.util.Map;

public class DefaultsShape<K, V> extends MapShape<K, V> {
   private final Map<K, V> A;
   private final KeyShape<K, V> C;

   public DefaultsShape(com.github.andrewoma.dexx.collection.Map<K, Integer> var1, Map<K, V> var2, KeyShape<K, V> var3) {
      super(var1);
      this.A = var2;
      this.C = var3;
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
