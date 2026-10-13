package bypass.util.container;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class MapContainer<K, V> extends Container<Map<K, V>> {
   private static final long c = 6052820631884L;

   public MapContainer() {
      this(new HashMap<>());
   }

   public MapContainer(Map<K, V> var1) {
      super(var1);
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

   public V D(K var1, V var2) {
      return this.getValue().put((K)var1, (V)var2);
   }

   public void z(Function<V, K> var1, V... var2) {
      for (Object var9 : var2) {
         this.D((K)var1.apply((V)(var9)), (V)var9);
      }
   }

   public boolean X() {
      return this.getValue() == null;
   }

   public void p(Map<? extends K, ? extends V> var1) {
      this.getValue().putAll(var1);
   }

   public Set<K> Z() {
      return Collections.unmodifiableSet(this.getValue().keySet());
   }

   public Collection<V> d() {
      return Collections.unmodifiableCollection(this.getValue().values());
   }

   public V e(K var1) {
      return this.getValue().get(var1);
   }

   public Optional<V> Z(K var1) {
      return Optional.ofNullable(this.getValue().get(var1));
   }

   public <T extends V> Optional<T> A(Class<T> var1) {
      for (Object var6 : this.d()) {
         if (var6.getClass() == var1) {
            return Optional.of((T)var6);
         }
      }

      return Optional.empty();
   }

   public V E(K var1) {
      return this.getValue().remove(var1);
   }

   public boolean v(K var1, V var2) {
      return this.getValue().remove(var1, var2);
   }

   public boolean c(K var1) {
      return this.getValue().containsKey(var1);
   }

   public boolean j(V var1) {
      return this.getValue().containsValue(var1);
   }

   public void H() {
      this.getValue().clear();
   }
}
