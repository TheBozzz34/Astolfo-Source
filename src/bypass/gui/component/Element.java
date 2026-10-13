package bypass.gui.component;

import bypass.ClientAccess;

public abstract class Element implements ClientAccess {
   private String g;
   private float D;
   private float J;
   private static final long ab = 100453377317205L;

   public Element(String var1, float var2, float var3) {
      this.g = var1;
      this.D = var2;
      this.J = var3;
   }

   public abstract void render(int var1, int var2);

   public String J() {
      return this.g;
   }

   public float M() {
      return this.D;
   }

   public void o(float var1) {
      this.D = var1;
   }

   public float E_float() {
      return this.J;
   }

   public void q(float var1) {
      this.J = var1;
   }
}
