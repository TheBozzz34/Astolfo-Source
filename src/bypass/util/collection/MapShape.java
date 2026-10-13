package bypass.util.collection;

import com.github.andrewoma.dexx.collection.HashMap;
import com.github.andrewoma.dexx.collection.Map;
import com.github.andrewoma.dexx.collection.Pair;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Set;
import java.util.Map.Entry;

public abstract class MapShape<K, V> {
   public static final MapShape EMPTY = new KeyShape<>(new HashMap<>());
   public final Map<K, Integer> slots;
   public static final String REMOVED = new String("Non existing mapping value");
   private static final Object NULL_KEY = new Object();
   private static final long b = 49304527385437L;

   public MapShape(Map<K, Integer> var1) {
      this.slots = var1;
   }

   private K d(K var1) {
      return (K)(var1 == null ? NULL_KEY : var1);
   }

   private K Q(K var1) {
      return (K)(var1 == NULL_KEY ? null : var1);
   }

   public java.util.Map<K, V> getDefaults() {
      return Collections.emptyMap();
   }

   public abstract KeyShape<K, V> getKeyShape();

   public V k(ShapedMap<K, V> var1, K var2) {
      Object var6 = this.w(var1, var2);
      return (V)(var6 != REMOVED ? var6 : null);
   }

   private Object w(ShapedMap<K, V> var1, Object var2) {
      Object var6 = this.d((K)var2);
      Integer var7 = this.slots.get((K)var6);
      return var7 == null ? this.getDefaults().get(var6) : Y(var1, var7);
   }

   public static Object Y(ShapedMap var0, int var1) {
      switch (var1) {
         case -3:
            return var0.M;
         case -2:
            return var0.D;
         case -1:
            return var0.w;
         default:
            return ((Object[])var0.w)[var1];
      }
   }

   public V n(ShapedMap<K, V> var1, K var2, Object var3) {
      Object var6 = this.d((K)var2);
      Integer var7 = this.slots.get((K)var6);
      Object var8 = REMOVED;
      if (var7 == null) {
         var8 = this.getDefaults().get(var6);
         java.util.Map var9 = ShapeCache.v(this.getDefaults(), (K)var6, var3);
         if (var9 != null) {
            var1.shape = this.getKeyShape().V(var9);
            return (V)var8;
         }

         if (var3 == REMOVED) {
            return (V)var8;
         }

         var7 = this.W(var1, (K)var6);
      }

      switch (var7) {
         case -3:
            if (var8 == REMOVED) {
               var8 = var1.M;
            }

            var1.M = var3;
            break;
         case -2:
            if (var8 == REMOVED) {
               var8 = var1.D;
            }

            var1.D = var3;
            break;
         case -1:
            if (var8 == REMOVED) {
               var8 = var1.w;
            }

            var1.w = var3;
            break;
         default:
            Object[] var10 = (Object[])var1.w;
            if (var8 == REMOVED) {
               var8 = var10[var7];
            }

            var10[var7] = var3;
      }

      return (V)var8;
   }

   private Integer W(ShapedMap<K, V> var1, K var2) {
      MapShape var5 = this.getKeyShape().O((K)var2, this.getDefaults());
      var1.shape = var5;
      int var6 = this.slots.size();
      if (var6 == 3) {
         Object[] var7 = new Object[4];
         var7[0] = var1.w;
         var1.w = var7;
      } else if (var6 > 3) {
         Object[] var10 = (Object[])var1.w;
         if (var10.length < var6 - 1) {
            int var8 = var10.length * 3 / 2;
            var8 += var8 & 1;
            Object[] var9 = new Object[var8];
            System.arraycopy(var10, 0, var9, 0, var10.length);
            var1.w = var9;
         }
      }

      return (java.lang.Integer)(var5.slots.get((K)var2));
   }

   public int P(ShapedMap<K, V> var1) {
      return this.slots.size() + this.getDefaults().size() - this.O(var1);
   }

   private int O(ShapedMap<K, V> var1) {
      int var5;
      label34: {
         var5 = 0;
         switch (this.slots.size()) {
            case 0:
               return var5;
            case 1:
               break label34;
            case 2:
               break;
            default:
               for (Object var9 : (Object[])var1.w) {
                  if (var9 == REMOVED) {
                     var5++;
                  }
               }
            case 3:
               if (var1.w == REMOVED) {
                  var5++;
               }
         }

         if (var1.D == REMOVED) {
            var5++;
         }
      }

      if (var1.M == REMOVED) {
         var5++;
      }

      return var5;
   }

   public boolean O(ShapedMap<K, V> var1, Object var2) {
      Object var6 = this.d((K)var2);
      Integer var7 = this.slots.get((K)var6);
      return var7 == null ? this.getDefaults().containsKey(var6) : Y(var1, var7) != REMOVED;
   }

   public Set<K> J(ShapedMap<K, V> var1) {
      return new ShapedMapKeySet<>(var1);
   }

   public Set<V> i(ShapedMap<K, V> var1) {
      return new ShapedMapValues<>(var1);
   }

   public Set<Entry<K, V>> f(ShapedMap<K, V> var1) {
      return new ShapedMapEntrySet<>(var1);
   }

   public void A(ShapedMap<K, V> var1, ObjectOutputStream var2) throws IOException {
      int var6 = this.slots.size() - this.O(var1);
      var2.writeInt(var6);
      if (var6 > 0) {
         for (Pair var8 : this.slots) {
            Object var9 = Y(var1, (Integer)var8.component2());
            if (var9 != REMOVED) {
               var2.writeObject(this.Q((K)var8.component1()));
               var2.writeObject(var9);
            }
         }
      }

      var2.writeObject(this.getDefaults());
   }

   public static <K, V> void i(ShapedMap<K, V> var0, ObjectInputStream var1) throws IOException, ClassNotFoundException {
      int var6 = var1.readInt();
      var0.shape = EMPTY;

      for (int var7 = 0; var7 < var6; var7++) {
         Object var8 = var1.readObject();
         Object var9 = var1.readObject();
         var0.put((K)(var8), (V)(var9));
      }

      java.util.Map var10 = (java.util.Map)var1.readObject();

      for (Object var12$raw : var10.entrySet()) {
         Entry var12 = (Entry)var12$raw;
         var0.put((K)(var12.getKey()), (V)(var12.getValue()));
      }
   }

   public static Object f(MapShape var0, Object var1) {
      return var0.Q(var1);
   }

   public static Object getNULL_KEY() {
      return NULL_KEY;
   }
}
