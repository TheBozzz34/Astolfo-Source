package bypass.gui.component;

import bypass.ClientAccess;

public abstract class Element implements ClientAccess {
   private String g;
   private float D;
   private float J;

   public Element(String string, float f, float g) {
      this.g = string;
      this.D = f;
      this.J = g;
   }

   public abstract void render(int i, int j);

   public String J() {
      return this.g;
   }

   public float M() {
      return this.D;
   }

   public void o(float f) {
      this.D = f;
   }

   public float E_float() {
      return this.J;
   }

   public void q(float f) {
      this.J = f;
   }
}
