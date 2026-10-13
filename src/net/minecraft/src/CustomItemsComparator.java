package net.minecraft.src;

import java.util.Comparator;

public class CustomItemsComparator implements Comparator {
   private static final long a = 103753410208287L;

   @Override
   public int compare(Object var1, Object var2) {
      CustomItemProperties var6 = (CustomItemProperties)var1;
      CustomItemProperties var7 = (CustomItemProperties)var2;
      return var6.weight != var7.weight
         ? var7.weight - var6.weight
         : (!Config.equals(var6.basePath, var7.basePath) ? var6.basePath.compareTo(var7.basePath) : var6.name.compareTo(var7.name));
   }
}
