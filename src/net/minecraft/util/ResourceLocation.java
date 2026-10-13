package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class ResourceLocation {
   public final String resourceDomain;
   public final String resourcePath;
   private static final long b = 38198896827140L;

   public ResourceLocation(int var1, String... var2) {
      this.resourceDomain = org.apache.commons.lang3.StringUtils.isEmpty(var2[0]) ? "minecraft" : var2[0].toLowerCase();
      this.resourcePath = var2[1];
      Validate.notNull(this.resourcePath);
   }

   public ResourceLocation(String var1) {
      this(0, splitObjectName(var1));
   }

   public ResourceLocation(String var1, String var2) {
      this(0, var1, var2);
   }

   public static String[] splitObjectName(String var0) {
      String[] var3 = new String[]{null, var0};
      int var4 = var0.indexOf(58);
      if (var4 >= 0) {
         var3[1] = var0.substring(var4 + 1, var0.length());
         if (var4 > 1) {
            var3[0] = var0.substring(0, var4);
         }
      }

      return var3;
   }

   public String getResourcePath() {
      return this.resourcePath;
   }

   public String getResourceDomain() {
      return this.resourceDomain;
   }

   @Override
   public String toString() {
      return this.resourceDomain + ':' + this.resourcePath;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ResourceLocation)) {
         return false;
      }

      ResourceLocation var5 = (ResourceLocation)var1;
      return this.resourceDomain.equals(var5.resourceDomain) && this.resourcePath.equals(var5.resourcePath);
   }

   @Override
   public int hashCode() {
      return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
   }
}
