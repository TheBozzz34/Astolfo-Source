package bypass.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public abstract class ShapedMapIterator<K, V, E> implements Iterator<E> {
   public boolean iteratingDefaults = true;
   private final ShapedMap<K, V> map;
   public Iterator currentIterator;
   public Entry<K, V> lastReturned;
   public Entry<K, V> pendingEntry;

   public ShapedMapIterator(ShapedMap<K, V> shapedMap) {
      this.map = shapedMap;
      if (!shapedMap.isEmpty()) {
         this.currentIterator = shapedMap.shape.getDefaults().entrySet().iterator();
         this.advance();
      }
   }

   private void advance() {
      if (!this.currentIterator.hasNext() && this.iteratingDefaults) {
         this.iteratingDefaults = false;
         this.currentIterator = this.map.shape.slots.asMap().entrySet().iterator();
      }

      if (!this.currentIterator.hasNext()) {
         this.pendingEntry = null;
      } else {
         Entry entry;
         Object object;
         do {
            if (!this.currentIterator.hasNext()) {
               this.pendingEntry = null;
               return;
            }

            entry = (Entry)this.currentIterator.next();
            if (this.iteratingDefaults) {
               object = entry.getValue();
               break;
            }

            object = MapShape.getSlotValue(this.map, (Integer)entry.getValue());
         } while (object == MapShape.REMOVED);

         this.pendingEntry = new ShapedMapEntry<>(this.map, (K)entry.getKey(), (V)object);
      }
   }

   @Override
   public boolean hasNext() {
      return this.pendingEntry != null;
   }

   public Entry<K, V> nextEntry() {
      if (this.pendingEntry == null) {
         throw new NoSuchElementException();
      }

      this.lastReturned = this.pendingEntry;
      this.advance();
      return this.lastReturned;
   }

   @Override
   public void remove() {
      if (this.lastReturned == null) {
         throw new IllegalStateException();
      }

      this.map.remove(this.lastReturned.getKey());
      this.lastReturned = null;
   }
}
