package bypass.util.collection;

import com.github.andrewoma.dexx.collection.Pair;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class KeyShape<K, V> extends MapShape<K, V> {
   private Map<K, KeyShape<K, V>> childShapes;
   private Map<Map<K, V>, MapShape<K, V>> defaultsShapes;

   public KeyShape(com.github.andrewoma.dexx.collection.Map<K, Integer> map) {
      super(map);
   }

   @Override
   public KeyShape<K, V> getKeyShape() {
      return this;
   }

   public MapShape<K, V> withDefaults(Map<K, V> map) {
      if (map != null && !map.isEmpty()) {
         synchronized (this) {
            Map map2 = this.defaultsShapes;
            if (map2 == null) {
               this.defaultsShapes = map2 = new IdentityHashMap();
            }

            MapShape mapShape = (MapShape)map2.get(map);
            if (mapShape == null) {
               mapShape = new DefaultsShape<>(this.slots, map, this);
               map2.put(map, mapShape);
            }

            return mapShape;
         }
      } else {
         return this;
      }
   }

   public MapShape<K, V> withKey(K object, Map<K, V> map) {
      KeyShape keyShape = null;
      synchronized (this) {
         Map map2 = this.childShapes;
         if (map2 != null) {
            keyShape = (KeyShape)map2.get(object);
         }
      }

      if (map.containsKey(object)) {
         map = ShapeCache.getDerivedMap(map, object, REMOVED);
      }

      if (keyShape != null) {
         return keyShape.withDefaults(map);
      }

      int i = this.slots.size();
      com.github.andrewoma.dexx.collection.Map map3 = this.slots;
      if (i < 3) {
         i -= 3;
      } else if (i == 3) {
         i = 1;

         for (Pair pair : this.slots) {
            if ((Integer)pair.component2() == -1) {
               map3 = map3.put(pair.component1(), 0);
               break;
            }
         }
      } else {
         i -= 2;
      }

      map3 = map3.put(object, i);
      keyShape = new KeyShape(map3);
      synchronized (this) {
         if (this.childShapes == null) {
            this.childShapes = Collections.singletonMap((K)object, keyShape);
         } else {
            KeyShape keyShape2 = this.childShapes.get(object);
            if (keyShape2 != null) {
               keyShape = keyShape2;
            } else {
               if (this.childShapes.size() == 1) {
                  this.childShapes = new HashMap<>(this.childShapes);
               }

               this.childShapes.put((K)object, keyShape);
            }
         }
      }

      return keyShape.withDefaults(map);
   }
}
