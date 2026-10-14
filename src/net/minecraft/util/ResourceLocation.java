package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class ResourceLocation {
   public final String resourceDomain;
   public final String resourcePath;
   private static final long b = 38198896827140L;

   public ResourceLocation(int i, String... resourceName) {
      this.resourceDomain = org.apache.commons.lang3.StringUtils.isEmpty(resourceName[0]) ? "minecraft" : resourceName[0].toLowerCase();
      this.resourcePath = resourceName[1];
      Validate.notNull(this.resourcePath);
   }

   public ResourceLocation(String resourceName) {
      this(0, splitObjectName(resourceName));
   }

   public ResourceLocation(String resourceDomainIn, String resourcePathIn) {
      this(0, resourceDomainIn, resourcePathIn);
   }

   public static String[] splitObjectName(String toSplit) {
      String[] strings = new String[]{null, toSplit};
      int i = toSplit.indexOf(58);
      if (i >= 0) {
         strings[1] = toSplit.substring(i + 1, toSplit.length());
         if (i > 1) {
            strings[0] = toSplit.substring(0, i);
         }
      }

      return strings;
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
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof ResourceLocation)) {
         return false;
      }

      ResourceLocation resourceLocation = (ResourceLocation)object;
      return this.resourceDomain.equals(resourceLocation.resourceDomain) && this.resourcePath.equals(resourceLocation.resourcePath);
   }

   @Override
   public int hashCode() {
      return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
   }
}
