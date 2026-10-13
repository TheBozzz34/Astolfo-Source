package net.minecraft.server.management;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class LowerStringMap<V> implements Map<String, V> {
   private final Map<String, V> internalMap = Maps.newLinkedHashMap();
   private static final long a = 134688424296662L;

   @Override
   public int size() {
      return this.internalMap.size();
   }

   @Override
   public boolean isEmpty() {
      return this.internalMap.isEmpty();
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.internalMap.containsKey(var1.toString().toLowerCase());
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.internalMap.containsKey(var1);
   }

   @Override
   public V get(Object var1) {
      return this.internalMap.get(var1.toString().toLowerCase());
   }

   public V put(String var1, V var2) {
      return this.internalMap.put(var1.toLowerCase(), (V)var2);
   }

   @Override
   public V remove(Object var1) {
      return this.internalMap.remove(var1.toString().toLowerCase());
   }

   @Override
   public void putAll(Map<? extends String, ? extends V> var1) {
      for (Entry var6 : var1.entrySet()) {
         this.put((String)var6.getKey(), (V)var6.getValue());
      }
   }

   @Override
   public void clear() {
      this.internalMap.clear();
   }

   @Override
   public Set<String> keySet() {
      return this.internalMap.keySet();
   }

   @Override
   public Collection<V> values() {
      return this.internalMap.values();
   }

   @Override
   public Set<Entry<String, V>> entrySet() {
      return this.internalMap.entrySet();
   }
}
