package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;

public class PropertyInteger extends PropertyHelper<Integer> {
   private final ImmutableSet<Integer> allowedValues;
   private static final long a = 59174525225973L;
   private static final long j = 5886005911822008351L;

   public PropertyInteger(String var1, int var2, int var3) {
      super(var1, Integer.class);
      if (var2 < 0) {
         throw new IllegalArgumentException("Min value of " + var1 + " must be 0 or greater");
      }

      if (var3 <= var2) {
         throw new IllegalArgumentException("Max value of " + var1 + " must be greater than min (" + var2 + ")");
      }

      HashSet var6 = Sets.newHashSet();

      for (int var7 = var2; var7 <= var3; var7++) {
         var6.add(var7);
      }

      this.allowedValues = ImmutableSet.copyOf(var6);
   }

   @Override
   public Collection<Integer> getAllowedValues() {
      return this.allowedValues;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 == null || this.getClass() != var1.getClass()) {
         return false;
      }

      if (!super.equals(var1)) {
         return false;
      }

      PropertyInteger var5 = (PropertyInteger)var1;
      return this.allowedValues.equals(var5.allowedValues);
   }

   @Override
   public int hashCode() {
      int var4 = super.hashCode();
      return (int)j * var4 + this.allowedValues.hashCode();
   }

   public static PropertyInteger create(String var0, int var1, int var2) {
      return new PropertyInteger(var0, var1, var2);
   }

   public String getName(Integer var1) {
      return var1.toString();
   }
}
