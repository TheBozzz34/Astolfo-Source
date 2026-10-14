package bypass.util.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

public final class FilteredMapContainer<K, V> extends MapContainer<K, V> implements ContainerSupplier<MapContainer<K, V>> {
   private final MapContainer<K, V> j;
   private final Predicate<? super K> E;
   private final Predicate<? super V> D;
   private Map<K, V> X;
   private Map<K, V> u;
   private static final long b = 35275775015591L;

   public FilteredMapContainer(MapContainer<K, V> mapContainer, Predicate<? super K> predicate, Predicate<? super V> predicate2, boolean bl) {
      super(mapContainer.getValue());
      this.j = mapContainer;
      this.E = predicate;
      this.D = predicate2;
      if (bl) {
         this.X = new HashMap<>();
         this.u = new HashMap<>();
      }
   }

   @Override
   public V D(K object, V object2) {
      return !this.S((K)object, (V)object2) ? null : super.D((K)object, (V)object2);
   }

   @Override
   public void p(Map<? extends K, ? extends V> map) {
      for (Entry entry : map.entrySet()) {
         if (!this.S((K)entry.getKey(), (V)entry.getValue())) {
            return;
         }
      }

      super.p(map);
   }

   private boolean S(K object, V object2) {
      boolean bl = this.D != null ? this.D.test((V)object2) : true;
      boolean bl2 = this.E != null ? this.E.test((K)object) : true;
      if (bl2 && bl) {
         if (this.X != null) {
            this.X.put((K)object, (V)object2);
         }

         return true;
      } else {
         if (this.u != null) {
            this.u.put((K)object, (V)object2);
         }

         return false;
      }
   }

   public Predicate<? super K> M() {
      return this.E;
   }

   public Predicate<? super V> g() {
      return this.D;
   }

   public MapContainer<K, V> W() {
      return this.j;
   }
}
