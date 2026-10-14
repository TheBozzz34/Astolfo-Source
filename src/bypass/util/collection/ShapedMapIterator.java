package bypass.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public abstract class ShapedMapIterator<K, V, E> implements Iterator<E> {
   public boolean V = true;
   private final ShapedMap<K, V> Q;
   public Iterator G;
   public Entry<K, V> M;
   public Entry<K, V> y;
   private static final long a = 66100522127055L;

   public ShapedMapIterator(ShapedMap<K, V> shapedMap) {
      this.Q = shapedMap;
      if (!shapedMap.isEmpty()) {
         this.G = shapedMap.shape.getDefaults().entrySet().iterator();
         this.q();
      }
   }

   private void q() {
      if (!this.G.hasNext() && this.V) {
         this.V = false;
         this.G = this.Q.shape.slots.asMap().entrySet().iterator();
      }

      if (!this.G.hasNext()) {
         this.y = null;
      } else {
         Entry entry;
         Object object;
         do {
            if (!this.G.hasNext()) {
               this.y = null;
               return;
            }

            entry = (Entry)this.G.next();
            if (this.V) {
               object = entry.getValue();
               break;
            }

            object = MapShape.Y(this.Q, (Integer)entry.getValue());
         } while (object == MapShape.REMOVED);

         this.y = new ShapedMapEntry<>(this.Q, (K)entry.getKey(), (V)object);
      }
   }

   @Override
   public boolean hasNext() {
      return this.y != null;
   }

   public Entry<K, V> D() {
      if (this.y == null) {
         throw new NoSuchElementException();
      }

      this.M = this.y;
      this.q();
      return this.M;
   }

   @Override
   public void remove() {
      if (this.M == null) {
         throw new IllegalStateException();
      }

      this.Q.remove(this.M.getKey());
      this.M = null;
   }
}
