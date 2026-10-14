package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;

public class PropertyInteger extends PropertyHelper<Integer> {
   private final ImmutableSet<Integer> allowedValues;

   public PropertyInteger(String name, int minIn, int maxIn) {
      super(name, Integer.class);
      if (minIn < 0) {
         throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
      }

      if (maxIn <= minIn) {
         throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + minIn + ")");
      }

      HashSet hashSet = Sets.newHashSet();

      for (int i = minIn; i <= maxIn; i++) {
         hashSet.add(i);
      }

      this.allowedValues = ImmutableSet.copyOf(hashSet);
   }

   @Override
   public Collection<Integer> getAllowedValues() {
      return this.allowedValues;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (object == null || this.getClass() != object.getClass()) {
         return false;
      }

      if (!super.equals(object)) {
         return false;
      }

      PropertyInteger propertyInteger = (PropertyInteger)object;
      return this.allowedValues.equals(propertyInteger.allowedValues);
   }

   @Override
   public int hashCode() {
      int i = super.hashCode();
      return 31 * i + this.allowedValues.hashCode();
   }

   public static PropertyInteger create(String name, int minIn, int maxIn) {
      return new PropertyInteger(name, minIn, maxIn);
   }

   public String getName(Integer value) {
      return value.toString();
   }
}
