package net.minecraft.src;

import java.util.ArrayList;

public class CompactArrayList {
   private ArrayList l = null;
   private int F = 0;
   private float U = 1.0F;
   private int W = 0;
   private static final long a = 132765221309218L;
   private static final long b = -7190864315476344822L;

   public CompactArrayList() {
      this((int)b, 0.75F);
   }

   public CompactArrayList(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public CompactArrayList(int initialCapacity, float loadFactor) {
      this.l = new ArrayList(initialCapacity);
      this.F = initialCapacity;
      this.U = loadFactor;
   }

   public void F(int i, Object object) {
      if (object != null) {
         this.W++;
      }

      this.l.add(i, object);
   }

   public boolean h(Object object) {
      if (object != null) {
         this.W++;
      }

      return this.l.add(object);
   }

   public Object h(int i, Object object) {
      Object object2 = this.l.set(i, object);
      if (object != object2) {
         if (object2 == null) {
            this.W++;
         }

         if (object == null) {
            this.W--;
         }
      }

      return object2;
   }

   public Object o(int i) {
      Object object = this.l.remove(i);
      if (object != null) {
         this.W--;
      }

      return object;
   }

   public void l() {
      this.l.clear();
      this.W = 0;
   }

   public void v_void() {
      if (this.W <= 0 && this.l.size() <= 0) {
         this.l();
      } else if (this.l.size() > this.F) {
         float f = this.W * 1.0F / this.l.size();
         if (f <= this.U) {
            int i = 0;

            for (int j = 0; j < this.l.size(); j++) {
               Object object = this.l.get(j);
               if (object != null) {
                  if (j != i) {
                     this.l.set(i, object);
                  }

                  i++;
               }
            }

            for (int k = this.l.size() - 1; k >= i; k--) {
               this.l.remove(k);
            }
         }
      }
   }

   public boolean a(Object object) {
      return this.l.contains(object);
   }

   public Object W(int i) {
      return this.l.get(i);
   }

   public boolean c() {
      return this.l.isEmpty();
   }

   public int v() {
      return this.l.size();
   }

   public int m() {
      return this.W;
   }
}
