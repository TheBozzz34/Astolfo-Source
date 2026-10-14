package net.minecraft.client.resources.model;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class ModelResourceLocation extends ResourceLocation {
   private final String variant;
   private static final long a = 139437241945711L;

   public ModelResourceLocation(int i, String... strings) {
      super(0, strings[0], strings[1]);
      this.variant = StringUtils.isEmpty(strings[2]) ? "normal" : strings[2].toLowerCase();
   }

   public ModelResourceLocation(String string) {
      this(0, parsePathString(string));
   }

   public ModelResourceLocation(ResourceLocation resourceLocation, String string) {
      this(resourceLocation.toString(), string);
   }

   public ModelResourceLocation(String string, String string2) {
      this(0, parsePathString(string + '#' + (string2 == null ? "normal" : string2)));
   }

   public static String[] parsePathString(String string) {
      String[] strings = new String[]{null, string, null};
      int i = string.indexOf(35);
      String string2 = string;
      if (i >= 0) {
         strings[2] = string.substring(i + 1, string.length());
         if (i > 1) {
            string2 = string.substring(0, i);
         }
      }

      System.arraycopy(ResourceLocation.splitObjectName(string2), 0, strings, 0, 2);
      return strings;
   }

   public String getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object instanceof ModelResourceLocation && super.equals(object)) {
         ModelResourceLocation modelResourceLocation = (ModelResourceLocation)object;
         return this.variant.equals(modelResourceLocation.variant);
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
