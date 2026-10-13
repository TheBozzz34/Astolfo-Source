package net.minecraft.util;

public class Vec4b {
   private byte field_176117_a;
   private byte field_176115_b;
   private byte field_176116_c;
   private byte field_176114_d;
   private static final long a = 102805311912583L;

   public Vec4b(byte var1, byte var2, byte var3, byte var4) {
      this.field_176117_a = var1;
      this.field_176115_b = var2;
      this.field_176116_c = var3;
      this.field_176114_d = var4;
   }

   public Vec4b(Vec4b var1) {
      this.field_176117_a = var1.field_176117_a;
      this.field_176115_b = var1.field_176115_b;
      this.field_176116_c = var1.field_176116_c;
      this.field_176114_d = var1.field_176114_d;
   }

   public byte func_176110_a() {
      return this.field_176117_a;
   }

   public byte func_176112_b() {
      return this.field_176115_b;
   }

   public byte func_176113_c() {
      return this.field_176116_c;
   }

   public byte func_176111_d() {
      return this.field_176114_d;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof Vec4b)) {
         return false;
      }

      Vec4b var5 = (Vec4b)var1;
      return this.field_176117_a != var5.field_176117_a
         ? false
         : (
            this.field_176114_d != var5.field_176114_d
               ? false
               : (this.field_176115_b != var5.field_176115_b ? false : this.field_176116_c == var5.field_176116_c)
         );
   }

   @Override
   public int hashCode() {
      int var3 = this.field_176117_a;
      var3 = 31 * var3 + this.field_176115_b;
      var3 = 31 * var3 + this.field_176116_c;
      return 31 * var3 + this.field_176114_d;
   }
}
