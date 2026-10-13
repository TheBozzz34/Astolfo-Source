package bypass.util;

import java.util.Map;

public abstract class AbstractRegistry<K, V> {
   public Map<K, V> Q;

   public Map<K, V> u() {
      return this.Q;
   }

   public abstract void H();

   public void O(K var1, V var2) {
      this.Q.put((K)var1, (V)var2);
   }

   public void h(K var1) {
      this.Q.remove(var1);
   }
}
