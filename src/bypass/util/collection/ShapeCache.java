package bypass.util.collection;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ShapeCache {
   private static Map<Object, Map<Object, Map<Map, Map>>> m = new HashMap<>();
   private static ReadWriteLock G = new ReentrantReadWriteLock();
   private static Lock E = G.readLock();
   private static Lock Z = G.writeLock();
   public static final String O = new String("All values match");
   private static final long a = 5206427367639L;

   public static void g() {
      Z.lock();

      try {
         m.clear();
      } finally {
         Z.unlock();
      }
   }

   public static boolean j(Object object) {
      return b(object, O);
   }

   public static boolean b(Object object, Object object2) {
      Z.lock();

      try {
         Map map = m.get(object);
         if (map == null) {
            m.put(object, map = new HashMap());
         }

         if (map.get(object2) != null) {
            return false;
         }

         map.put(object2, new IdentityHashMap());
         return true;
      } finally {
         Z.unlock();
      }
   }

   public static <K, V> Map<K, V> v(Map<K, V> map, K object, Object object2) {
      E.lock();

      Map map2;
      Map map3;
      try {
         map2 = m.get(object);
         if (map2 == null) {
            return null;
         }

         map3 = (Map)map2.get(object2);
         if (map3 == null) {
            if (object2 != MapShape.REMOVED && map2.get(O) == null) {
               return null;
            }
         } else {
            Map map4 = (Map)map3.get(map);
            if (map4 != null) {
               return map4;
            }
         }
      } finally {
         E.unlock();
      }

      HashMap hashMap = new HashMap((int)((map.size() + 1) / 0.75F));
      hashMap.putAll(map);
      if (object2 == MapShape.REMOVED) {
         hashMap.remove(object);
      } else {
         hashMap.put(object, object2);
      }

      Z.lock();

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
         Z.unlock();
      }
   }
}
