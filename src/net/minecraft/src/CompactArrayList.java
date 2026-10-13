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

   public CompactArrayList(int var1) {
      this(var1, 0.75F);
   }

   public CompactArrayList(int var1, float var2) {
      this.l = new ArrayList(var1);
      this.F = var1;
      this.U = var2;
   }

   public void F(int var1, Object var2) {
      if (var2 != null) {
         this.W++;
      }

      this.l.add(var1, var2);
   }

   public boolean h(Object var1) {
      if (var1 != null) {
         this.W++;
      }

      return this.l.add(var1);
   }

   public Object h(int var1, Object var2) {
      Object var6 = this.l.set(var1, var2);
      if (var2 != var6) {
         if (var6 == null) {
            this.W++;
         }

         if (var2 == null) {
            this.W--;
         }
      }

      return var6;
   }

   public Object o(int var1) {
      Object var5 = this.l.remove(var1);
      if (var5 != null) {
         this.W--;
      }

      return var5;
   }

   public void l() {
      this.l.clear();
      this.W = 0;
   }

   public void v_void() {
      if (this.W <= 0 && this.l.size() <= 0) {
         this.l();
      } else if (this.l.size() > this.F) {
         float var4 = this.W * 1.0F / this.l.size();
         if (var4 <= this.U) {
            int var5 = 0;

            for (int var6 = 0; var6 < this.l.size(); var6++) {
               Object var7 = this.l.get(var6);
               if (var7 != null) {
                  if (var6 != var5) {
                     this.l.set(var5, var7);
                  }

                  var5++;
               }
            }

            for (int var8 = this.l.size() - 1; var8 >= var5; var8--) {
               this.l.remove(var8);
            }
         }
      }
   }

   public boolean a(Object var1) {
      return this.l.contains(var1);
   }

   public Object W(int var1) {
      return this.l.get(var1);
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
