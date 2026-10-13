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

   public static boolean j(Object var0) {
      return b(var0, O);
   }

   public static boolean b(Object var0, Object var1) {
      Z.lock();

      try {
         Map var5 = m.get(var0);
         if (var5 == null) {
            m.put(var0, var5 = new HashMap());
         }

         if (var5.get(var1) != null) {
            return false;
         }

         var5.put(var1, new IdentityHashMap());
         return true;
      } finally {
         Z.unlock();
      }
   }

   public static <K, V> Map<K, V> v(Map<K, V> var0, K var1, Object var2) {
      E.lock();

      Map var5;
      Map var6;
      try {
         var5 = m.get(var1);
         if (var5 == null) {
            return null;
         }

         var6 = (Map)var5.get(var2);
         if (var6 == null) {
            if (var2 != MapShape.REMOVED && var5.get(O) == null) {
               return null;
            }
         } else {
            Map var7 = (Map)var6.get(var0);
            if (var7 != null) {
               return var7;
            }
         }
      } finally {
         E.unlock();
      }

      HashMap var17 = new HashMap((int)((var0.size() + 1) / 0.75F));
      var17.putAll(var0);
      if (var2 == MapShape.REMOVED) {
         var17.remove(var1);
      } else {
         var17.put(var1, var2);
      }

      Z.lock();

      try {
         if (var6 == null) {
            var6 = (Map)var5.get(var2);
            if (var6 == null) {
               var5.put(var2, var6 = new IdentityHashMap());
            }
         }

         Map var8 = (Map)var6.get(var0);
         if (var8 != null) {
            return var8;
         }

         var6.put(var0, var17);
         return var17;
      } finally {
         Z.unlock();
      }
   }
}
