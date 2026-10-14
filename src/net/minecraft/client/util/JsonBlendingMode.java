package net.minecraft.client.util;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.JsonUtils;
import org.lwjgl.opengl.GL14;

public class JsonBlendingMode {
   private static JsonBlendingMode field_148118_a = null;
   private final int field_148116_b;
   private final int field_148117_c;
   private final int field_148114_d;
   private final int field_148115_e;
   private final int field_148112_f;
   private final boolean field_148113_g;
   private final boolean field_148119_h;
   private static final long a = 53479563966374L;

   private JsonBlendingMode(boolean bl, boolean bl2, int i, int j, int k, int l, int m) {
      this.field_148113_g = bl;
      this.field_148116_b = i;
      this.field_148114_d = j;
      this.field_148117_c = k;
      this.field_148115_e = l;
      this.field_148119_h = bl2;
      this.field_148112_f = m;
   }

   public JsonBlendingMode() {
      this(false, true, 1, 0, 1, 0, 32774);
   }

   public JsonBlendingMode(int i, int j, int k) {
      this(false, false, i, j, i, j, k);
   }

   public JsonBlendingMode(int i, int j, int k, int l, int m) {
      this(true, false, i, j, k, l, m);
   }

   public void func_148109_a() {
      if (!this.equals(field_148118_a)) {
         if (field_148118_a == null || this.field_148119_h != field_148118_a.func_148111_b()) {
            field_148118_a = this;
            if (this.field_148119_h) {
               GlStateManager.disableBlend();
               return;
            }

            GlStateManager.enableBlend();
         }

         GL14.glBlendEquation(this.field_148112_f);
         if (this.field_148113_g) {
            GlStateManager.tryBlendFuncSeparate(this.field_148116_b, this.field_148114_d, this.field_148117_c, this.field_148115_e);
         } else {
            GlStateManager.blendFunc(this.field_148116_b, this.field_148114_d);
         }
      }
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof JsonBlendingMode)) {
         return false;
      }

      JsonBlendingMode jsonBlendingMode = (JsonBlendingMode)object;
      return this.field_148112_f != jsonBlendingMode.field_148112_f
         ? false
         : (
            this.field_148115_e != jsonBlendingMode.field_148115_e
               ? false
               : (
                  this.field_148114_d != jsonBlendingMode.field_148114_d
                     ? false
                     : (
                        this.field_148119_h != jsonBlendingMode.field_148119_h
                           ? false
                           : (
                              this.field_148113_g != jsonBlendingMode.field_148113_g
                                 ? false
                                 : (this.field_148117_c != jsonBlendingMode.field_148117_c ? false : this.field_148116_b == jsonBlendingMode.field_148116_b)
                           )
                     )
               )
         );
   }

   @Override
   public int hashCode() {
      int i = this.field_148116_b;
      i = 31 * i + this.field_148117_c;
      i = 31 * i + this.field_148114_d;
      i = 31 * i + this.field_148115_e;
      i = 31 * i + this.field_148112_f;
      i = 31 * i + (this.field_148113_g ? 1 : 0);
      return 31 * i + (this.field_148119_h ? 1 : 0);
   }

   public boolean func_148111_b() {
      return this.field_148119_h;
   }

   public static JsonBlendingMode func_148110_a(JsonObject jsonObject) {
      if (jsonObject == null) {
         return new JsonBlendingMode();
      }

      int i = 32774;
      int j = 1;
      int k = 0;
      int l = 1;
      int m = 0;
      boolean bl = true;
      boolean bl2 = false;
      if (JsonUtils.isString(jsonObject, "func")) {
         i = func_148108_a(jsonObject.get("func").getAsString());
         if (i != 32774) {
            bl = false;
         }
      }

      if (JsonUtils.isString(jsonObject, "srcrgb")) {
         j = func_148107_b(jsonObject.get("srcrgb").getAsString());
         if (j != 1) {
            bl = false;
         }
      }

      if (JsonUtils.isString(jsonObject, "dstrgb")) {
         k = func_148107_b(jsonObject.get("dstrgb").getAsString());
         if (k != 0) {
            bl = false;
         }
      }

      if (JsonUtils.isString(jsonObject, "srcalpha")) {
         l = func_148107_b(jsonObject.get("srcalpha").getAsString());
         if (l != 1) {
            bl = false;
         }

         bl2 = true;
      }

      if (JsonUtils.isString(jsonObject, "dstalpha")) {
         m = func_148107_b(jsonObject.get("dstalpha").getAsString());
         if (m != 0) {
            bl = false;
         }

         bl2 = true;
      }

      return bl ? new JsonBlendingMode() : (bl2 ? new JsonBlendingMode(j, k, l, m, i) : new JsonBlendingMode(j, k, i));
   }

   private static int func_148108_a(String string) {
      String string2 = string.trim().toLowerCase();
      return string2.equals("add")
         ? 32774
         : (
            string2.equals("subtract")
               ? 32778
               : (
                  string2.equals("reversesubtract")
                     ? 32779
                     : (string2.equals("reverse_subtract") ? 32779 : (string2.equals("min") ? 32775 : (string2.equals("max") ? 32776 : 32774)))
               )
         );
   }

   private static int func_148107_b(String string) {
      String string2 = string.trim().toLowerCase();
      string2 = string2.replaceAll("_", "");
      string2 = string2.replaceAll("one", "1");
      string2 = string2.replaceAll("zero", "0");
      string2 = string2.replaceAll("minus", "-");
      return string2.equals("0")
         ? 0
         : (
            string2.equals("1")
               ? 1
               : (
                  string2.equals("srccolor")
                     ? 768
                     : (
                        string2.equals("1-srccolor")
                           ? 769
                           : (
                              string2.equals("dstcolor")
                                 ? 774
                                 : (
                                    string2.equals("1-dstcolor")
                                       ? 775
                                       : (
                                          string2.equals("srcalpha")
                                             ? 770
                                             : (string2.equals("1-srcalpha") ? 771 : (string2.equals("dstalpha") ? 772 : (string2.equals("1-dstalpha") ? 773 : -1)))
                                       )
                                 )
                           )
                     )
               )
         );
   }
}
