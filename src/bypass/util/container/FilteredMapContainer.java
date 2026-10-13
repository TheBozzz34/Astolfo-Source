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

   public FilteredMapContainer(MapContainer<K, V> var1, Predicate<? super K> var2, Predicate<? super V> var3, boolean var4) {
      super(var1.getValue());
      this.j = var1;
      this.E = var2;
      this.D = var3;
      if (var4) {
         this.X = new HashMap<>();
         this.u = new HashMap<>();
      }
   }

   @Override
   public V D(K var1, V var2) {
      return !this.S((K)var1, (V)var2) ? null : super.D((K)var1, (V)var2);
   }

   @Override
   public void p(Map<? extends K, ? extends V> var1) {
      for (Entry var6 : var1.entrySet()) {
         if (!this.S((K)var6.getKey(), (V)var6.getValue())) {
            return;
         }
      }

      super.p(var1);
   }

   private boolean S(K var1, V var2) {
      boolean var6 = this.D != null ? this.D.test((V)var2) : true;
      boolean var7 = this.E != null ? this.E.test((K)var1) : true;
      if (var7 && var6) {
         if (this.X != null) {
            this.X.put((K)var1, (V)var2);
         }

         return true;
      } else {
         if (this.u != null) {
            this.u.put((K)var1, (V)var2);
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
