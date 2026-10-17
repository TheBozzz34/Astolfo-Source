package net.minecraft.src;

import java.util.ArrayList;

public class CompactArrayList {
   private ArrayList list = null;
   private int initialCapacity = 0;
   private float loadFactor = 1.0F;
   private int countValid = 0;

   public CompactArrayList() {
      this(10, 0.75F);
   }

   public CompactArrayList(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public CompactArrayList(int initialCapacity, float loadFactor) {
      this.list = new ArrayList(initialCapacity);
      this.initialCapacity = initialCapacity;
      this.loadFactor = loadFactor;
   }

   public void insert(int i, Object object) {
      if (object != null) {
         this.countValid++;
      }

      this.list.add(i, object);
   }

   public boolean add(Object element) {
      if (element != null) {
         this.countValid++;
      }

      return this.list.add(element);
   }

   public Object set(int index, Object element) {
      Object object = this.list.set(index, element);
      if (element != object) {
         if (object == null) {
            this.countValid++;
         }

         if (element == null) {
            this.countValid--;
         }
      }

      return object;
   }

   public Object remove(int index) {
      Object object = this.list.remove(index);
      if (object != null) {
         this.countValid--;
      }

      return object;
   }

   public void clear() {
      this.list.clear();
      this.countValid = 0;
   }

   public void v_void() {
      if (this.countValid <= 0 && this.list.size() <= 0) {
         this.clear();
      } else if (this.list.size() > this.initialCapacity) {
         float f = this.countValid * 1.0F / this.list.size();
         if (f <= this.loadFactor) {
            int i = 0;

            for (int j = 0; j < this.list.size(); j++) {
               Object object = this.list.get(j);
               if (object != null) {
                  if (j != i) {
                     this.list.set(i, object);
                  }

                  i++;
               }
            }

            for (int k = this.list.size() - 1; k >= i; k--) {
               this.list.remove(k);
            }
         }
      }
   }

   public boolean contains(Object elem) {
      return this.list.contains(elem);
   }

   public Object get(int index) {
      return this.list.get(index);
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public int size() {
      return this.list.size();
   }

   public int getCountValid() {
      return this.countValid;
   }
}
