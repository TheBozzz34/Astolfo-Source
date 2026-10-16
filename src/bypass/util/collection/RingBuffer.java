package bypass.util.collection;

import java.lang.reflect.Array;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RingBuffer<T> {
   private final T[] buffer;
   private final Class<? extends T> elementType;
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private int count;
   private int writeIndex;

   public RingBuffer(Class<? extends T> class2, int i) {
      this.elementType = class2;
      this.buffer = (T[])((Object[])Array.newInstance(class2, i));
   }

   public T add(T object) {
      this.lock.writeLock().lock();
      this.buffer[this.writeIndex] = (T)object;
      this.writeIndex = (this.writeIndex + 1) % this.getCapacity();
      if (this.count < this.getCapacity()) {
         this.count++;
      }

      this.lock.writeLock().unlock();
      return (T)object;
   }

   public int getCapacity() {
      this.lock.readLock().lock();
      int i = this.buffer.length;
      this.lock.readLock().unlock();
      return i;
   }

   public T[] toOrderedArray() {
      Object[] objects = (Object[])Array.newInstance(this.elementType, this.count);
      this.lock.readLock().lock();

      for (int i = 0; i < this.count; i++) {
         int j = (this.writeIndex - this.count + i) % this.getCapacity();
         if (j < 0) {
            j += this.getCapacity();
         }

         objects[i] = this.buffer[j];
      }

      this.lock.readLock().unlock();
      return (T[])objects;
   }
}
