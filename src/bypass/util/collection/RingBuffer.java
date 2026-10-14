package bypass.util.collection;

import java.lang.reflect.Array;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RingBuffer<T> {
   private final T[] s;
   private final Class<? extends T> k;
   private final ReadWriteLock q = new ReentrantReadWriteLock();
   private int Y;
   private int R;
   private static final long a = 21303592652026L;

   public RingBuffer(Class<? extends T> class2, int i) {
      this.k = class2;
      this.s = (T[])((Object[])Array.newInstance(class2, i));
   }

   public T G(T object) {
      this.q.writeLock().lock();
      this.s[this.R] = (T)object;
      this.R = (this.R + 1) % this.d();
      if (this.Y < this.d()) {
         this.Y++;
      }

      this.q.writeLock().unlock();
      return (T)object;
   }

   public int d() {
      this.q.readLock().lock();
      int i = this.s.length;
      this.q.readLock().unlock();
      return i;
   }

   public T[] v() {
      Object[] objects = (Object[])Array.newInstance(this.k, this.Y);
      this.q.readLock().lock();

      for (int i = 0; i < this.Y; i++) {
         int j = (this.R - this.Y + i) % this.d();
         if (j < 0) {
            j += this.d();
         }

         objects[i] = this.s[j];
      }

      this.q.readLock().unlock();
      return (T[])objects;
   }
}
