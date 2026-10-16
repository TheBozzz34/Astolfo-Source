package bypass.util.container;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class MapContainer<K, V> extends Container<Map<K, V>> {
   public MapContainer() {
      this(new HashMap<>());
   }

   public MapContainer(Map<K, V> map) {
      super(map);
   }

   @Override
   public void initialize() {
      this.ensureMap();
   }

   @Override
   public void destroy() throws ContainerError {
      this.releaseMap();
   }

   public Map<K, V> W_Map() {
      return Collections.unmodifiableMap(this.getValue());
   }

   public void ensureMap() {
      this.setValue(!this.isMapNull() ? this.getValue() : new HashMap<>());
   }

   public void releaseMap() {
      this.setValue(null);
   }

   public V put(K object, V object2) {
      return this.getValue().put((K)object, (V)object2);
   }

   public void putAllKeyed(Function<V, K> function, V... objects) {
      for (Object object : objects) {
         this.put((K)function.apply((V)(object)), (V)object);
      }
   }

   public boolean isMapNull() {
      return this.getValue() == null;
   }

   public void putAll(Map<? extends K, ? extends V> map) {
      this.getValue().putAll(map);
   }

   public Set<K> getKeys() {
      return Collections.unmodifiableSet(this.getValue().keySet());
   }

   public Collection<V> getValues() {
      return Collections.unmodifiableCollection(this.getValue().values());
   }

   public V get(K object) {
      return this.getValue().get(object);
   }

   public Optional<V> getOptional(K object) {
      return Optional.ofNullable(this.getValue().get(object));
   }

   public <T extends V> Optional<T> findValueByClass(Class<T> class2) {
      for (Object object : this.getValues()) {
         if (object.getClass() == class2) {
            return Optional.of((T)object);
         }
      }

      return Optional.empty();
   }

   public V remove(K object) {
      return this.getValue().remove(object);
   }

   public boolean removeEntry(K object, V object2) {
      return this.getValue().remove(object, object2);
   }

   public boolean containsKey(K object) {
      return this.getValue().containsKey(object);
   }

   public boolean containsValue(V object) {
      return this.getValue().containsValue(object);
   }

   public void clear() {
      this.getValue().clear();
   }
}
