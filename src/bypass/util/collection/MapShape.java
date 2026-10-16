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

   private K maskNull(K object) {
      return (K)(object == null ? NULL_KEY : object);
   }

   private K unmaskNull(K object) {
      return (K)(object == NULL_KEY ? null : object);
   }

   public java.util.Map<K, V> getDefaults() {
      return Collections.emptyMap();
   }

   public abstract KeyShape<K, V> getKeyShape();

   public V getValue(ShapedMap<K, V> shapedMap, K object) {
      Object object2 = this.lookupRaw(shapedMap, object);
      return (V)(object2 != REMOVED ? object2 : null);
   }

   private Object lookupRaw(ShapedMap<K, V> shapedMap, Object object) {
      Object object2 = this.maskNull((K)object);
      Integer integer = this.slots.get((K)object2);
      return integer == null ? this.getDefaults().get(object2) : getSlotValue(shapedMap, integer);
   }

   public static Object getSlotValue(ShapedMap shapedMap, int i) {
      switch (i) {
         case -3:
            return shapedMap.firstValue;
         case -2:
            return shapedMap.secondValue;
         case -1:
            return shapedMap.slotValues;
         default:
            return ((Object[])shapedMap.slotValues)[i];
      }
   }

   public V putValue(ShapedMap<K, V> shapedMap, K object, Object object2) {
      Object object3 = this.maskNull((K)object);
      Integer integer = this.slots.get((K)object3);
      Object object4 = REMOVED;
      if (integer == null) {
         object4 = this.getDefaults().get(object3);
         java.util.Map map = ShapeCache.getDerivedMap(this.getDefaults(), (K)object3, object2);
         if (map != null) {
            shapedMap.shape = this.getKeyShape().withDefaults(map);
            return (V)object4;
         }

         if (object2 == REMOVED) {
            return (V)object4;
         }

         integer = this.addSlot(shapedMap, (K)object3);
      }

      switch (integer) {
         case -3:
            if (object4 == REMOVED) {
               object4 = shapedMap.firstValue;
            }

            shapedMap.firstValue = object2;
            break;
         case -2:
            if (object4 == REMOVED) {
               object4 = shapedMap.secondValue;
            }

            shapedMap.secondValue = object2;
            break;
         case -1:
            if (object4 == REMOVED) {
               object4 = shapedMap.slotValues;
            }

            shapedMap.slotValues = object2;
            break;
         default:
            Object[] objects = (Object[])shapedMap.slotValues;
            if (object4 == REMOVED) {
               object4 = objects[integer];
            }

            objects[integer] = object2;
      }

      return (V)object4;
   }

   private Integer addSlot(ShapedMap<K, V> shapedMap, K object) {
      MapShape mapShape = this.getKeyShape().withKey((K)object, this.getDefaults());
      shapedMap.shape = mapShape;
      int i = this.slots.size();
      if (i == 3) {
         Object[] objects = new Object[4];
         objects[0] = shapedMap.slotValues;
         shapedMap.slotValues = objects;
      } else if (i > 3) {
         Object[] objects2 = (Object[])shapedMap.slotValues;
         if (objects2.length < i - 1) {
            int j = objects2.length * 3 / 2;
            j += j & 1;
            Object[] objects3 = new Object[j];
            System.arraycopy(objects2, 0, objects3, 0, objects2.length);
            shapedMap.slotValues = objects3;
         }
      }

      return (java.lang.Integer)(mapShape.slots.get((K)object));
   }

   public int getSize(ShapedMap<K, V> shapedMap) {
      return this.slots.size() + this.getDefaults().size() - this.countRemoved(shapedMap);
   }

   private int countRemoved(ShapedMap<K, V> shapedMap) {
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
               for (Object object : (Object[])shapedMap.slotValues) {
                  if (object == REMOVED) {
                     i++;
                  }
               }
            case 3:
               if (shapedMap.slotValues == REMOVED) {
                  i++;
               }
         }

         if (shapedMap.secondValue == REMOVED) {
            i++;
         }
      }

      if (shapedMap.firstValue == REMOVED) {
         i++;
      }

      return i;
   }

   public boolean hasKey(ShapedMap<K, V> shapedMap, Object object) {
      Object object2 = this.maskNull((K)object);
      Integer integer = this.slots.get((K)object2);
      return integer == null ? this.getDefaults().containsKey(object2) : getSlotValue(shapedMap, integer) != REMOVED;
   }

   public Set<K> createKeySet(ShapedMap<K, V> shapedMap) {
      return new ShapedMapKeySet<>(shapedMap);
   }

   public Set<V> createValues(ShapedMap<K, V> shapedMap) {
      return new ShapedMapValues<>(shapedMap);
   }

   public Set<Entry<K, V>> createEntrySet(ShapedMap<K, V> shapedMap) {
      return new ShapedMapEntrySet<>(shapedMap);
   }

   public void serialize(ShapedMap<K, V> shapedMap, ObjectOutputStream objectOutputStream) throws IOException {
      int i = this.slots.size() - this.countRemoved(shapedMap);
      objectOutputStream.writeInt(i);
      if (i > 0) {
         for (Pair pair : this.slots) {
            Object object = getSlotValue(shapedMap, (Integer)pair.component2());
            if (object != REMOVED) {
               objectOutputStream.writeObject(this.unmaskNull((K)pair.component1()));
               objectOutputStream.writeObject(object);
            }
         }
      }

      objectOutputStream.writeObject(this.getDefaults());
   }

   public static <K, V> void deserialize(ShapedMap<K, V> shapedMap, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
      int i = objectInputStream.readInt();
      shapedMap.shape = EMPTY;

      for (int j = 0; j < i; j++) {
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

   public static Object unmaskNullStatic(MapShape mapShape, Object object) {
      return mapShape.unmaskNull(object);
   }

   public static Object getNULL_KEY() {
      return NULL_KEY;
   }
}
