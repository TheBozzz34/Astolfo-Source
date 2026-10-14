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
   public Animation F = new TimedAnimation().setDuration(100).setEasing(Easing.INERTIA);

   public Component(String string, float f, float g, int i, int j) {
      super(string, f, g);
      this.z = i;
      this.v = j;
   }

   public void T(int i, int j) {
      this.r(i - this.m, j - this.k);
   }

   public void U(int i, int j, int k) {
      this.l(i);
   }

   public abstract void l(int i);

   public void P(char c, int i) {
   }

   public void W(int i, int j, int k) {
   }

   public boolean G(int i, int j) {
      return i >= this.M() && i <= this.M() + this.x() && j >= this.E_float() && j <= this.E_float() + this.Y();
   }

   public boolean u(int i, int j, int k, int l, int m, int n) {
      return m >= i && m <= i + k && n >= j && n <= j + l;
   }

   public void f(int i, int j) {
      this.o(i);
      this.q(j);
   }

   public void r(float f, float g) {
      this.o(f);
      this.q(g);
   }

   public int x() {
      return this.z;
   }

   public void t(int i) {
      this.z = i;
   }

   public int Y() {
      return this.v;
   }

   public void B(int i) {
      this.v = i;
   }

   public void F(boolean bl, int i, int j) {
      if (bl) {
         this.m = i - this.M();
         this.k = j - this.E_float();
      }

      this.b = bl;
   }

   public void x(boolean bl) {
      this.m = 0.0F;
      this.k = 0.0F;
      this.b = bl;
   }

   public boolean u_boolean() {
      return this.b;
   }

   public boolean c_boolean() {
      return this.y;
   }

   public void F(boolean bl) {
      this.y = bl;
   }
}
