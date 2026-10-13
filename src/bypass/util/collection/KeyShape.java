package bypass.util.collection;

import com.github.andrewoma.dexx.collection.Pair;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class KeyShape<K, V> extends MapShape<K, V> {
   private Map<K, KeyShape<K, V>> a;
   private Map<Map<K, V>, MapShape<K, V>> h;
   private static final long c = 42495198659535L;

   public KeyShape(com.github.andrewoma.dexx.collection.Map<K, Integer> var1) {
      super(var1);
   }

   @Override
   public KeyShape<K, V> getKeyShape() {
      return this;
   }

   public MapShape<K, V> V(Map<K, V> var1) {
      if (var1 != null && !var1.isEmpty()) {
         synchronized (this) {
            Map var6 = this.h;
            if (var6 == null) {
               this.h = var6 = new IdentityHashMap();
            }

            MapShape var4 = (MapShape)var6.get(var1);
            if (var4 == null) {
               var4 = new DefaultsShape<>(this.slots, var1, this);
               var6.put(var1, var4);
            }

            return var4;
         }
      } else {
         return this;
      }
   }

   public MapShape<K, V> O(K var1, Map<K, V> var2) {
      KeyShape var5 = null;
      synchronized (this) {
         Map var7 = this.a;
         if (var7 != null) {
            var5 = (KeyShape)var7.get(var1);
         }
      }

      if (var2.containsKey(var1)) {
         var2 = ShapeCache.v(var2, var1, REMOVED);
      }

      if (var5 != null) {
         return var5.V(var2);
      }

      int var6 = this.slots.size();
      com.github.andrewoma.dexx.collection.Map var15 = this.slots;
      if (var6 < 3) {
         var6 -= 3;
      } else if (var6 == 3) {
         var6 = 1;

         for (Pair var9 : this.slots) {
            if ((Integer)var9.component2() == -1) {
               var15 = var15.put(var9.component1(), 0);
               break;
            }
         }
      } else {
         var6 -= 2;
      }

      var15 = var15.put(var1, var6);
      var5 = new KeyShape(var15);
      synchronized (this) {
         if (this.a == null) {
            this.a = Collections.singletonMap((K)var1, var5);
         } else {
            KeyShape var17 = this.a.get(var1);
            if (var17 != null) {
               var5 = var17;
            } else {
               if (this.a.size() == 1) {
                  this.a = new HashMap<>(this.a);
               }

               this.a.put((K)var1, var5);
            }
         }
      }

      return var5.V(var2);
   }
}
