package bypass.util;

import java.util.Map;

public abstract class AbstractRegistry<K, V> {
   public Map<K, V> registry;

   public Map<K, V> getRegistry() {
      return this.registry;
   }

   public abstract void initialize();

   public void register(K object, V object2) {
      this.registry.put((K)object, (V)object2);
   }

   public void unregister(K object) {
      this.registry.remove(object);
   }
}
