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

   public MapShape(Map<K, Integer> map) {
      this.slots = map;
   }

   private K d(K object) {
      return (K)(object == null ? NULL_KEY : object);
   }

   private K Q(K object) {
      return (K)(object == NULL_KEY ? null : object);
   }

   public java.util.Map<K, V> getDefaults() {
      return Collections.emptyMap();
   }

   public abstract KeyShape<K, V> getKeyShape();

   public V k(ShapedMap<K, V> shapedMap, K object) {
      Object object2 = this.w(shapedMap, object);
      return (V)(object2 != REMOVED ? object2 : null);
   }

   private Object w(ShapedMap<K, V> shapedMap, Object object) {
      Object object2 = this.d((K)object);
      Integer integer = this.slots.get((K)object2);
      return integer == null ? this.getDefaults().get(object2) : Y(shapedMap, integer);
   }

   public static Object Y(ShapedMap shapedMap, int i) {
      switch (i) {
         case -3:
            return shapedMap.M;
         case -2:
            return shapedMap.D;
         case -1:
            return shapedMap.w;
         default:
            return ((Object[])shapedMap.w)[i];
      }
   }

   public V n(ShapedMap<K, V> shapedMap, K object, Object object2) {
      Object object3 = this.d((K)object);
      Integer integer = this.slots.get((K)object3);
      Object object4 = REMOVED;
      if (integer == null) {
         object4 = this.getDefaults().get(object3);
         java.util.Map map = ShapeCache.v(this.getDefaults(), (K)object3, object2);
         if (map != null) {
            shapedMap.shape = this.getKeyShape().V(map);
            return (V)object4;
         }

         if (object2 == REMOVED) {
            return (V)object4;
         }

         integer = this.W(shapedMap, (K)object3);
      }

      switch (integer) {
         case -3:
            if (object4 == REMOVED) {
               object4 = shapedMap.M;
            }

            shapedMap.M = object2;
            break;
         case -2:
            if (object4 == REMOVED) {
               object4 = shapedMap.D;
            }

            shapedMap.D = object2;
            break;
         case -1:
            if (object4 == REMOVED) {
               object4 = shapedMap.w;
            }

            shapedMap.w = object2;
            break;
         default:
            Object[] objects = (Object[])shapedMap.w;
            if (object4 == REMOVED) {
               object4 = objects[integer];
            }

            objects[integer] = object2;
      }

      return (V)object4;
   }

   private Integer W(ShapedMap<K, V> shapedMap, K object) {
      MapShape mapShape = this.getKeyShape().O((K)object, this.getDefaults());
      shapedMap.shape = mapShape;
      int i = this.slots.size();
      if (i == 3) {
         Object[] objects = new Object[4];
         objects[0] = shapedMap.w;
         shapedMap.w = objects;
      } else if (i > 3) {
         Object[] objects2 = (Object[])shapedMap.w;
         if (objects2.length < i - 1) {
            int j = objects2.length * 3 / 2;
            j += j & 1;
            Object[] objects3 = new Object[j];
            System.arraycopy(objects2, 0, objects3, 0, objects2.length);
            shapedMap.w = objects3;
         }
      }

      return (java.lang.Integer)(mapShape.slots.get((K)object));
   }

   public int P(ShapedMap<K, V> shapedMap) {
      return this.slots.size() + this.getDefaults().size() - this.O(shapedMap);
   }

   private int O(ShapedMap<K, V> shapedMap) {
      int i;
      label34: {
         i = 0;
         switch (this.slots.size()) {
            case 0:
               return i;
            case 1:
               break label34;
            case 2:
               break;
            default:
               for (Object object : (Object[])shapedMap.w) {
                  if (object == REMOVED) {
                     i++;
                  }
               }
            case 3:
               if (shapedMap.w == REMOVED) {
                  i++;
               }
         }

         if (shapedMap.D == REMOVED) {
            i++;
         }
      }

      if (shapedMap.M == REMOVED) {
         i++;
      }

      return i;
   }

   public boolean O(ShapedMap<K, V> shapedMap, Object object) {
      Object object2 = this.d((K)object);
      Integer integer = this.slots.get((K)object2);
      return integer == null ? this.getDefaults().containsKey(object2) : Y(shapedMap, integer) != REMOVED;
   }

   public Set<K> J(ShapedMap<K, V> shapedMap) {
      return new ShapedMapKeySet<>(shapedMap);
   }

   public Set<V> i(ShapedMap<K, V> shapedMap) {
      return new ShapedMapValues<>(shapedMap);
   }

   public Set<Entry<K, V>> f(ShapedMap<K, V> shapedMap) {
      return new ShapedMapEntrySet<>(shapedMap);
   }

   public void A(ShapedMap<K, V> shapedMap, ObjectOutputStream objectOutputStream) throws IOException {
      int i = this.slots.size() - this.O(shapedMap);
      objectOutputStream.writeInt(i);
      if (i > 0) {
         for (Pair pair : this.slots) {
            Object object = Y(shapedMap, (Integer)pair.component2());
            if (object != REMOVED) {
               objectOutputStream.writeObject(this.Q((K)pair.component1()));
               objectOutputStream.writeObject(object);
            }
         }
      }

      objectOutputStream.writeObject(this.getDefaults());
   }

   public static <K, V> void i(ShapedMap<K, V> shapedMap, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
      int j = objectInputStream.readInt();
      shapedMap.shape = EMPTY;

      for (int k = 0; k < j; k++) {
         Object object = objectInputStream.readObject();
         Object object2 = objectInputStream.readObject();
         shapedMap.put((K)(object), (V)(object2));
      }

      java.util.Map map = (java.util.Map)objectInputStream.readObject();

      for (Object object3 : map.entrySet()) {
         Entry entry = (Entry)object3;
         shapedMap.put((K)(entry.getKey()), (V)(entry.getValue()));
      }
   }

   public static Object f(MapShape mapShape, Object object) {
      return mapShape.Q(object);
   }

   public static Object getNULL_KEY() {
      return NULL_KEY;
   }
}
