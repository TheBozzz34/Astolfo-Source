package bypass.util;

import java.util.Map;

public abstract class AbstractRegistry<K, V> {
   public Map<K, V> Q;

   public Map<K, V> u() {
      return this.Q;
   }

   public abstract void H();

   public void O(K object, V object2) {
      this.Q.put((K)object, (V)object2);
   }

   public void h(K object) {
      this.Q.remove(object);
   }
}
