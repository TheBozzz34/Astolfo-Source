package net.minecraft.src;

import java.util.Comparator;

public class CustomItemsComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      CustomItemProperties customItemProperties = (CustomItemProperties)o1;
      CustomItemProperties customItemProperties2 = (CustomItemProperties)o2;
      return customItemProperties.weight != customItemProperties2.weight
         ? customItemProperties2.weight - customItemProperties.weight
         : (!Config.equals(customItemProperties.basePath, customItemProperties2.basePath) ? customItemProperties.basePath.compareTo(customItemProperties2.basePath) : customItemProperties.name.compareTo(customItemProperties2.name));
   }
}
