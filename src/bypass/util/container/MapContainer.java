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
   public void j() {
      this.x();
   }

   @Override
   public void m() throws ContainerError {
      this.F();
   }

   public Map<K, V> W_Map() {
      return Collections.unmodifiableMap(this.getValue());
   }

   public void x() {
      this.setValue(!this.X() ? this.getValue() : new HashMap<>());
   }

   public void F() {
      this.setValue(null);
   }

   public V D(K object, V object2) {
      return this.getValue().put((K)object, (V)object2);
   }

   public void z(Function<V, K> function, V... objects) {
      for (Object object : objects) {
         this.D((K)function.apply((V)(object)), (V)object);
      }
   }

   public boolean X() {
      return this.getValue() == null;
   }

   public void p(Map<? extends K, ? extends V> map) {
      this.getValue().putAll(map);
   }

   public Set<K> Z() {
      return Collections.unmodifiableSet(this.getValue().keySet());
   }

   public Collection<V> d() {
      return Collections.unmodifiableCollection(this.getValue().values());
   }

   public V e(K object) {
      return this.getValue().get(object);
   }

   public Optional<V> Z(K object) {
      return Optional.ofNullable(this.getValue().get(object));
   }

   public <T extends V> Optional<T> A(Class<T> class2) {
      for (Object object : this.d()) {
         if (object.getClass() == class2) {
            return Optional.of((T)object);
         }
      }

      return Optional.empty();
   }

   public V E(K object) {
      return this.getValue().remove(object);
   }

   public boolean v(K object, V object2) {
      return this.getValue().remove(object, object2);
   }

   public boolean c(K object) {
      return this.getValue().containsKey(object);
   }

   public boolean j(V object) {
      return this.getValue().containsValue(object);
   }

   public void H() {
      this.getValue().clear();
   }
}
