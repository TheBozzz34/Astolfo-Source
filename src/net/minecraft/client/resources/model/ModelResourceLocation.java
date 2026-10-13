package net.minecraft.client.resources.model;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class ModelResourceLocation extends ResourceLocation {
   private final String variant;
   private static final long a = 139437241945711L;

   public ModelResourceLocation(int var1, String... var2) {
      super(0, var2[0], var2[1]);
      this.variant = StringUtils.isEmpty(var2[2]) ? "normal" : var2[2].toLowerCase();
   }

   public ModelResourceLocation(String var1) {
      this(0, parsePathString(var1));
   }

   public ModelResourceLocation(ResourceLocation var1, String var2) {
      this(var1.toString(), var2);
   }

   public ModelResourceLocation(String var1, String var2) {
      this(0, parsePathString(var1 + '#' + (var2 == null ? "normal" : var2)));
   }

   public static String[] parsePathString(String var0) {
      String[] var3 = new String[]{null, var0, null};
      int var4 = var0.indexOf(35);
      String var5 = var0;
      if (var4 >= 0) {
         var3[2] = var0.substring(var4 + 1, var0.length());
         if (var4 > 1) {
            var5 = var0.substring(0, var4);
         }
      }

      System.arraycopy(ResourceLocation.splitObjectName(var5), 0, var3, 0, 2);
      return var3;
   }

   public String getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof ModelResourceLocation && super.equals(var1)) {
         ModelResourceLocation var5 = (ModelResourceLocation)var1;
         return this.variant.equals(var5.variant);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 31 * super.hashCode() + this.variant.hashCode();
   }

   @Override
   public String toString() {
      return super.toString() + '#' + this.variant;
   }
}
