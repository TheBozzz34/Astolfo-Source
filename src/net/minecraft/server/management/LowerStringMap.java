package net.minecraft.server.management;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class LowerStringMap<V> implements Map<String, V> {
   private final Map<String, V> internalMap = Maps.newLinkedHashMap();

   @Override
   public int size() {
      return this.internalMap.size();
   }

   @Override
   public boolean isEmpty() {
      return this.internalMap.isEmpty();
   }

   @Override
   public boolean containsKey(Object object) {
      return this.internalMap.containsKey(object.toString().toLowerCase());
   }

   @Override
   public boolean containsValue(Object object) {
      return this.internalMap.containsKey(object);
   }

   @Override
   public V get(Object object) {
      return this.internalMap.get(object.toString().toLowerCase());
   }

   public V put(String string, V object) {
      return this.internalMap.put(string.toLowerCase(), (V)object);
   }

   @Override
   public V remove(Object object) {
      return this.internalMap.remove(object.toString().toLowerCase());
   }

   @Override
   public void putAll(Map<? extends String, ? extends V> map) {
      for (Entry entry : map.entrySet()) {
         this.put((String)entry.getKey(), (V)entry.getValue());
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
