package net.minecraft.util;

public class MouseFilter {
   private float field_76336_a;
   private float field_76334_b;
   private float field_76335_c;

   public float smooth(float f, float g) {
      this.field_76336_a += f;
      f = (this.field_76336_a - this.field_76334_b) * g;
      this.field_76335_c = this.field_76335_c + (f - this.field_76335_c) * 0.5F;
      if (f > 0.0F && f > this.field_76335_c || f < 0.0F && f < this.field_76335_c) {
         f = this.field_76335_c;
      }

      this.field_76334_b += f;
      return f;
   }

   public void reset() {
      this.field_76336_a = 0.0F;
      this.field_76334_b = 0.0F;
      this.field_76335_c = 0.0F;
   }
}
