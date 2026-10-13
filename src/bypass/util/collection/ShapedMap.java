package bypass.util.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ShapedMap<K, V> implements Map<K, V>, Serializable {
   public MapShape<K, V> shape = MapShape.EMPTY;
   public Object w;
   public Object D;
   public Object M;
   private static final long c = 53316868147433L;

   @Override
   public int size() {
      return this.shape.P(this);
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.shape.O(this, var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.values().contains(var1);
   }

   @Override
   public V get(Object var1) {
      return this.shape.k(this, (K)var1);
   }

   @Override
   public V put(K var1, V var2) {
      return this.shape.n(this, (K)var1, var2);
   }

   public V F(K var1, Object var2) {
      return this.shape.n(this, (K)var1, var2);
   }

   @Override
   public V remove(Object var1) {
      return this.shape.n(this, (K)var1, MapShape.REMOVED);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> var1) {
      for (Entry var6 : var1.entrySet()) {
         this.put((K)var6.getKey(), (V)var6.getValue());
      }
   }

   @Override
   public void clear() {
      this.shape = MapShape.EMPTY;
      this.w = this.D = this.M = null;
   }

   @Override
   public Set<K> keySet() {
      return this.shape.J(this);
   }

   @Override
   public Collection<V> values() {
      return this.shape.i(this);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.shape.f(this);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof Map)) {
         return false;
      }

      Map var5 = (Map)var1;
      if (var5.size() != this.size()) {
         return false;
      }

      for (Entry var7 : this.entrySet()) {
         Object var8 = var7.getKey();
         Object var9 = var7.getValue();
         if (var9 == null) {
            if (var5.get(var8) != null || !var5.containsKey(var8)) {
               return false;
            }
         } else if (!var9.equals(var5.get(var8))) {
            return false;
         }
      }

      return true;
   }

   @Override
   public String toString() {
      Iterator var4 = this.entrySet().iterator();
      if (!var4.hasNext()) {
         return "{}";
      }

      StringBuilder var5 = new StringBuilder();
      var5.append('{');

      while (var4.hasNext()) {
         Entry var6 = (Entry)var4.next();
         Object var7 = var6.getKey();
         Object var8 = var6.getValue();
         var5.append(var7).append('=').append(var8);
         var5.append(',').append(' ');
      }

      var5.setLength(var5.length() - 2);
      return var5.append('}').toString();
   }

   @Override
   public int hashCode() {
      int var4 = 0;

      for (Entry var6 : this.entrySet()) {
         var4 += var6.hashCode();
      }

      return var4;
   }
}
