package bypass.gui.component;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;

public abstract class Component extends Element {
   private int z;
   private int v;
   private boolean y;
   private boolean b;
   private float m;
   private float k;
   public Animation F;
   private static final long cb = 100330973966023L;
   private static final long db = -2954289139974930332L;

   public Component(String var1, float var2, float var3, int var4, int var5) {
      super(var1, var2, var3);
      this.F = new TimedAnimation().setDuration((int)db).setEasing(Easing.INERTIA);
      this.z = var4;
      this.v = var5;
   }

   public void T(int var1, int var2) {
      this.r(var1 - this.m, var2 - this.k);
   }

   public void U(int var1, int var2, int var3) {
      this.l(var1);
   }

   public abstract void l(int var1);

   public void P(char var1, int var2) {
   }

   public void W(int var1, int var2, int var3) {
   }

   public boolean G(int var1, int var2) {
      return var1 >= this.M() && var1 <= this.M() + this.x() && var2 >= this.E_float() && var2 <= this.E_float() + this.Y();
   }

   public boolean u(int var1, int var2, int var3, int var4, int var5, int var6) {
      return var5 >= var1 && var5 <= var1 + var3 && var6 >= var2 && var6 <= var2 + var4;
   }

   public void f(int var1, int var2) {
      this.o(var1);
      this.q(var2);
   }

   public void r(float var1, float var2) {
      this.o(var1);
      this.q(var2);
   }

   public int x() {
      return this.z;
   }

   public void t(int var1) {
      this.z = var1;
   }

   public int Y() {
      return this.v;
   }

   public void B(int var1) {
      this.v = var1;
   }

   public void F(boolean var1, int var2, int var3) {
      if (var1) {
         this.m = var2 - this.M();
         this.k = var3 - this.E_float();
      }

      this.b = var1;
   }

   public void x(boolean var1) {
      this.m = 0.0F;
      this.k = 0.0F;
      this.b = var1;
   }

   public boolean u_boolean() {
      return this.b;
   }

   public boolean c_boolean() {
      return this.y;
   }

   public void F(boolean var1) {
      this.y = var1;
   }
}
