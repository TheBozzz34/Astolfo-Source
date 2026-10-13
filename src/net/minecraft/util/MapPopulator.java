package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapPopulator {
   private static final long a = 63924765332250L;

   public static <K, V> Map<K, V> createMap(Iterable<K> var0, Iterable<V> var1) {
      return populateMap(var0, var1, Maps.newLinkedHashMap());
   }

   public static <K, V> Map<K, V> populateMap(Iterable<K> var0, Iterable<V> var1, Map<K, V> var2) {
      Iterator var6 = var1.iterator();

      for (Object var8 : var0) {
         var2.put((K)(var8), (V)(var6.next()));
      }

      if (var6.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return var2;
      }
   }
}
