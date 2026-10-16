package bypass.util.collection;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ShapeCache {
   private static Map<Object, Map<Object, Map<Map, Map>>> cache = new HashMap<>();
   private static ReadWriteLock lockPair = new ReentrantReadWriteLock();
   private static Lock readLock = lockPair.readLock();
   private static Lock writeLock = lockPair.writeLock();
   public static final String ALL_VALUES_MATCH = new String("All values match");

   public static void clear() {
      writeLock.lock();

      try {
         cache.clear();
      } finally {
         writeLock.unlock();
      }
   }

   public static boolean registerAllValues(Object object) {
      return register(object, ALL_VALUES_MATCH);
   }

   public static boolean register(Object object, Object object2) {
      writeLock.lock();

      try {
         Map map = cache.get(object);
         if (map == null) {
            cache.put(object, map = new HashMap());
         }

         if (map.get(object2) != null) {
            return false;
         }

         map.put(object2, new IdentityHashMap());
         return true;
      } finally {
         writeLock.unlock();
      }
   }

   public static <K, V> Map<K, V> getDerivedMap(Map<K, V> map, K object, Object object2) {
      readLock.lock();

      Map map2;
      Map map3;
      try {
         map2 = cache.get(object);
         if (map2 == null) {
            return null;
         }

         map3 = (Map)map2.get(object2);
         if (map3 == null) {
            if (object2 != MapShape.REMOVED && map2.get(ALL_VALUES_MATCH) == null) {
               return null;
            }
         } else {
            Map map4 = (Map)map3.get(map);
            if (map4 != null) {
               return map4;
            }
         }
      } finally {
         readLock.unlock();
      }

      HashMap hashMap = new HashMap((int)((map.size() + 1) / 0.75F));
      hashMap.putAll(map);
      if (object2 == MapShape.REMOVED) {
         hashMap.remove(object);
      } else {
         hashMap.put(object, object2);
      }

      writeLock.lock();

      try {
         if (map3 == null) {
            map3 = (Map)map2.get(object2);
            if (map3 == null) {
               map2.put(object2, map3 = new IdentityHashMap());
            }
         }

         Map map5 = (Map)map3.get(map);
         if (map5 != null) {
            return map5;
         }

         map3.put(map, hashMap);
         return hashMap;
      } finally {
         writeLock.unlock();
      }
   }
}
