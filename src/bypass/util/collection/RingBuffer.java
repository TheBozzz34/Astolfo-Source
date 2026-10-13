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

   public RingBuffer(Class<? extends T> var1, int var2) {
      this.k = var1;
      this.s = (T[])((Object[])Array.newInstance(var1, var2));
   }

   public T G(T var1) {
      this.q.writeLock().lock();
      this.s[this.R] = (T)var1;
      this.R = (this.R + 1) % this.d();
      if (this.Y < this.d()) {
         this.Y++;
      }

      this.q.writeLock().unlock();
      return (T)var1;
   }

   public int d() {
      this.q.readLock().lock();
      int var1 = this.s.length;
      this.q.readLock().unlock();
      return var1;
   }

   public T[] v() {
      Object[] var4 = (Object[])Array.newInstance(this.k, this.Y);
      this.q.readLock().lock();

      for (int var5 = 0; var5 < this.Y; var5++) {
         int var6 = (this.R - this.Y + var5) % this.d();
         if (var6 < 0) {
            var6 += this.d();
         }

         var4[var5] = this.s[var6];
      }

      this.q.readLock().unlock();
      return (T[])var4;
   }
}
