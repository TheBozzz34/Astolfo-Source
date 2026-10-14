package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapPopulator {
   private static final long a = 63924765332250L;

   public static <K, V> Map<K, V> createMap(Iterable<K> keys, Iterable<V> values) {
      return populateMap(keys, values, Maps.newLinkedHashMap());
   }

   public static <K, V> Map<K, V> populateMap(Iterable<K> keys, Iterable<V> values, Map<K, V> map) {
      Iterator iterator2 = values.iterator();

      for (Object object : keys) {
         map.put((K)(object), (V)(iterator2.next()));
      }

      if (iterator2.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return map;
      }
   }
}
