package bypass.util.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

public final class FilteredMapContainer<K, V> extends MapContainer<K, V> implements ContainerSupplier<MapContainer<K, V>> {
   private final MapContainer<K, V> source;
   private final Predicate<? super K> keyFilter;
   private final Predicate<? super V> valueFilter;
   private Map<K, V> accepted;
   private Map<K, V> rejected;

   public FilteredMapContainer(MapContainer<K, V> mapContainer, Predicate<? super K> predicate, Predicate<? super V> predicate2, boolean bl) {
      super(mapContainer.getValue());
      this.source = mapContainer;
      this.keyFilter = predicate;
      this.valueFilter = predicate2;
      if (bl) {
         this.accepted = new HashMap<>();
         this.rejected = new HashMap<>();
      }
   }

   @Override
   public V put(K object, V object2) {
      return !this.testAndRecord((K)object, (V)object2) ? null : super.put((K)object, (V)object2);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map) {
      for (Entry entry : map.entrySet()) {
         if (!this.testAndRecord((K)entry.getKey(), (V)entry.getValue())) {
            return;
         }
      }

      super.putAll(map);
   }

   private boolean testAndRecord(K object, V object2) {
      boolean bl = this.valueFilter != null ? this.valueFilter.test((V)object2) : true;
      boolean bl2 = this.keyFilter != null ? this.keyFilter.test((K)object) : true;
      if (bl2 && bl) {
         if (this.accepted != null) {
            this.accepted.put((K)object, (V)object2);
         }

         return true;
      } else {
         if (this.rejected != null) {
            this.rejected.put((K)object, (V)object2);
         }

         return false;
      }
   }

   public Predicate<? super K> getKeyFilter() {
      return this.keyFilter;
   }

   public Predicate<? super V> getValueFilter() {
      return this.valueFilter;
   }

   public MapContainer<K, V> getContainer() {
      return this.source;
   }
}
